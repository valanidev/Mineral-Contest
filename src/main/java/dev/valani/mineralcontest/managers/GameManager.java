package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.FormatUtil;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;

public class GameManager {

    private final Main plugin;
    private final ConfigManager configManager;
    private final TimeManager timeManager;
    private final TeamManager teamManager;
    private final KitManager kitManager;
    private final SbManager scoreboardManager;
    private final ArenaManager arenaManager;
    private final ScoreManager scoreManager;
    private BorderManager borderManager;

    private GameState state;

    // --- Menus ---
    private final TeamSelectorMenu teamSelectorMenu;
    private final KitSelectorMenu kitSelectorMenu;

    private final int kitSelectTime;
    private BukkitTask kitSelectTask;

    private final World world;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.timeManager = new TimeManager(plugin, configManager);
        this.teamManager = new TeamManager(plugin, configManager);
        this.scoreboardManager = new SbManager(plugin, this);
        this.kitManager = new KitManager(plugin);
        this.arenaManager = new ArenaManager(plugin);
        this.scoreManager = new ScoreManager(plugin);

        this.teamSelectorMenu = new TeamSelectorMenu(teamManager);
        this.kitSelectorMenu = new KitSelectorMenu(kitManager);

        this.kitSelectTime = configManager.getInt("settings.kits.select_time");

        String worldName = plugin.getConfigManager().getString("settings.world");
        this.world = Bukkit.getWorld(worldName);
        if(world == null) {
            Bukkit.getLogger().severe("Le monde " + worldName + " n'existe pas !");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        this.state = GameState.WAITING;
    }

    public void startKitSelect() {
        if (!isWaiting()) return;

        boolean shouldStart = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (teamManager.getTeams().stream().noneMatch(t -> t.hasMember(player))) {
                Bukkit.broadcastMessage("§c" + player.getName() + " doit choisir une équipe avant de commencer !");
                player.performCommand("team");
                shouldStart = false;
            }
        }

        if (!shouldStart) return;

        this.state = GameState.KIT_SELECT;
        SoundUtil.playForAll(Sound.ENTITY_PLAYER_LEVELUP);
        sendState();

        Bukkit.getOnlinePlayers().forEach(player -> player.performCommand("kit"));

        kitSelectTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!isKitSelect()) return;

            Bukkit.getOnlinePlayers().forEach(player -> {
                if(!kitManager.hasKit(player)) kitManager.assignRandomKit(player);
            });

            startGame();
        }, kitSelectTime * 20L);

    }

    public void startGame() {
        if (!isKitSelect()) return;

        this.state = GameState.IN_GAME;
        timeManager.start();

        for(Player player : Bukkit.getOnlinePlayers()) {
            AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0;
            player.setHealth(maxHealth);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.closeInventory();
            Team team = teamManager.getPlayerTeam(player).orElse(null);
            if(team == null) continue;
            player.teleport(teamManager.getTeamSpawnLocation(team));
        }

        this.borderManager = new BorderManager(plugin, world);
        borderManager.start();

        SoundUtil.playForAll(Sound.ENTITY_PLAYER_LEVELUP);
        sendState();
    }

    public void endGame() {
        if (!isInGame()) return;

        this.state = GameState.ENDED;
        timeManager.stop();
        if (borderManager != null) borderManager.stop();
        arenaManager.makeUnavailable();

        SoundUtil.playForAll(Sound.ENTITY_ENDER_DRAGON_GROWL);
        sendState();

        Bukkit.broadcastMessage("\n§8§m                    §r  §6§lScores finaux §r §8§m                    ");
        teamManager.getTeams().stream()
                .sorted(Comparator.comparingInt(Team::getScore).reversed())
                .forEach(team -> Bukkit.broadcastMessage(
                        "§7- " + team.getDisplayName() + " §6→ §e§l" + team.getScore() + " POINTS"
                ));
        Bukkit.broadcastMessage("§r");
    }

    public void resetGame() {
        if (kitSelectTask != null && !kitSelectTask.isCancelled()) {
            kitSelectTask.cancel();
            kitSelectTask = null;
        }

        this.state = GameState.WAITING;
        timeManager.stop();
        if (borderManager != null) borderManager.stop();
        arenaManager.makeUnavailable();

        teamManager.clearAll();
        kitManager.resetAll();

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getInventory().clear();
            p.setDisplayName(p.getName());
            p.setPlayerListName(p.getName());
            p.setFoodLevel(20);
            for (PotionEffect pe : p.getActivePotionEffects()) {
                p.removePotionEffect(pe.getType());
            }
        });

        SoundUtil.playForAll(Sound.ENTITY_PLAYER_LEVELUP);
        sendState();
    }

    private void sendState() {
        String message = configManager.getString("messages.game_state." + state.name().toLowerCase());

        if (state == GameState.KIT_SELECT) {
            message = message.replace("%time%", FormatUtil.formatTimeLong(kitSelectTime));
        }

        Bukkit.broadcastMessage(message);
    }

    // --- Getters ---

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public SbManager getSbManager() {
        return scoreboardManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public BorderManager getBorderManager() {
        return borderManager;
    }

    public TeamSelectorMenu getTeamSelectorMenu() {
        return teamSelectorMenu;
    }

    public KitSelectorMenu getKitSelectorMenu() {
        return kitSelectorMenu;
    }

    public boolean isWaiting() {
        return state == GameState.WAITING;
    }

    public boolean isKitSelect() {
        return state == GameState.KIT_SELECT;
    }

    public boolean isInGame() {
        return state == GameState.IN_GAME;
    }

    public boolean isEnded() {
        return state == GameState.ENDED;
    }

}
