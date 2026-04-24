package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    private final Main plugin;
    private GameState state;

    private final ArenaManager arenaManager;
    private final DropManager dropManager;
    private final TeamManager teamManager;
    private final DoorManager doorManager;
    private final KitManager kitManager;
    private final ScoreManager scoreManager;
    private final SbManager scoreboardManager;

    private final List<BukkitTask> alertTasks;
    private BukkitTask gameEndTimer;

    private final Map<Material, Integer> dropScores;
    private final int kitSelectionTimer;

    private long endTimeMillis;

    public KitSelectorMenu getKitSelectorMenu() {
        return kitSelectorMenu;
    }

    public TeamSelectorMenu getTeamSelectorMenu() {
        return teamSelectorMenu;
    }

    // MENUS
    private final KitSelectorMenu kitSelectorMenu;
    private final TeamSelectorMenu teamSelectorMenu;

    public GameManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;

        this.arenaManager = new ArenaManager(plugin, this);
        this.dropManager = new DropManager(plugin, this);
        this.teamManager = new TeamManager(plugin);
        this.doorManager = new DoorManager(this);
        this.kitManager = new KitManager(plugin);
        this.scoreManager = new ScoreManager(this);
        this.scoreboardManager = new SbManager(plugin);
        this.alertTasks = new ArrayList<>();
        this.dropScores = new HashMap<>();
        this.kitSelectionTimer = configManager.getInt("game.kit_selection_timer");

        this.teamSelectorMenu = new TeamSelectorMenu(teamManager);
        this.kitSelectorMenu = new KitSelectorMenu(kitManager);

        loadDropScores();
        reset();
    }

    public void loadDropScores() {
        dropScores.clear();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("drop_scores");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                Material material = Material.valueOf(key.toUpperCase());
                int value = section.getInt(key);
                dropScores.put(material, value);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Matériau invalide dans drop_scores: " + key);
            }
        }
    }

    private void cancelAlertTasks() {
        for (BukkitTask task : alertTasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        alertTasks.clear();
    }

    public GameResult start() {
        if (!isState(GameState.WAITING)) return GameResult.ALREADY_STARTED;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (teamManager.getTeams().stream().noneMatch(t -> t.hasMember(player))) {
                return GameResult.PLAYER_HAS_NO_TEAM;
            }
        }

        state = GameState.KIT_SELECTION;
        Bukkit.broadcastMessage("§eChoisissez un kit ! Vous avez " + kitSelectionTimer + " secondes. §a/kit");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.performCommand("kit");
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::startGameAfterKitSelection, kitSelectionTimer * 20L);
        for (int i = kitSelectionTimer; i > 0; i--) {
            int time = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (time <= 5) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        player.sendTitle("", "§6Début dans §e" + time + " §6secondes", 10, 3 * 20, 10);
                    }
                }
            }, (kitSelectionTimer - i) * 20L);
        }

        return GameResult.SUCCESS;
    }

    public GameResult startGameAfterKitSelection() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!kitManager.hasKit(player)) {
                KitBase kit = kitManager.assignRandomKit(player);
                player.sendMessage("§cAucun kit choisi → kit aléatoire assigné: §e" + kit.getDisplayName());
                Bukkit.broadcastMessage("§6§lKIT §a" + player.getDisplayName() + " §aa choisi le kit " + kit.getDisplayName() + "§a.");
            }
        }

        state = GameState.STARTED;

        int durationSeconds = plugin.getConfigManager().getInt("game.duration_seconds");
        endTimeMillis = System.currentTimeMillis() + (durationSeconds * 1000L);
        gameEndTimer = Bukkit.getScheduler().runTaskLater(plugin, this::end, durationSeconds * 20L);

        List<Integer> alerts = plugin.getConfig().getIntegerList("game.alerts");

        for (int alert : alerts) {
            if (alert <= 0 || alert >= durationSeconds) continue;

            long delay = (durationSeconds - alert) * 20L;
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                String timeFormatted = Utils.formatTime(alert);

                Bukkit.broadcastMessage("§6§lTIMER §e⏳ Il reste " + timeFormatted + " !");

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                }
            }, delay);

            alertTasks.add(task);
        }

        dropManager.scheduleNextDrop();
        if (arenaManager != null && arenaManager.getChestLocation() != null) {
            arenaManager.scheduleAvailability();
        }


        String gameStartedStr = plugin.getConfigManager().getString("game.started");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);
            player.sendTitle(gameStartedStr, "", 10, 3 * 20, 10);
            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 0));
            player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(20, 255));
            player.setFoodLevel(20);
            player.setSaturation(20);
            scoreboardManager.create(player);

            Team team = teamManager.getPlayerTeam(player).orElse(null);
            if (team == null) continue;
            player.teleport(teamManager.getTeamArenaLocation(team));
        }

        World world = Bukkit.getWorld("world");
        if (world != null) {
            world.setTime(0);

            for (Entity entity : world.getEntities()) {
                List<EntityType> types = List.of(EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER);
                if (types.contains(entity.getType())) {
                    entity.remove();
                }
            }
        }

        Utils.playSoundForAll(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0f, 1.0f);

        Bukkit.broadcastMessage("\n§6§lSTART " + gameStartedStr + "\n ");
        return GameResult.SUCCESS;
    }

    public int getTimeLeftSeconds() {
        long remaining = endTimeMillis - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000);
    }

    public GameResult end() {
        if (isState(GameState.ENDED)) return GameResult.ALREADY_ENDED;

        state = GameState.ENDED;
        cancelGameTimer();
        cancelAlertTasks();
        dropManager.cancelDropTimer();
        scoreboardManager.resetAll();

        String gameEndedStr = plugin.getConfigManager().getString("game.ended");
        Bukkit.broadcastMessage("\n§6§lEND " + gameEndedStr + "\n ");

        List<Team> teams = teamManager.getTeams();
        Bukkit.broadcastMessage("§8§m                    §r  §6§lScores finaux §r §8§m                    ");
        for (Team team : teams) {
            Bukkit.broadcastMessage("§7- " + team.getDisplayName() + " §6→ §e§l" + team.getScore() + " POINTS");
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawnFireworks(player, 3);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1f, 1f);
            player.sendTitle(gameEndedStr, "", 10, 3 * 20, 10);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        return GameResult.SUCCESS;
    }

    public void spawnFireworks(Player player, int amount) {
        Location loc = player.getLocation().add(0, 1, 0);
        World world = loc.getWorld();
        if (world == null) return;

        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);
        if (playerTeam == null) return;
        Color color = Utils.translateChatColorToColor(playerTeam.getColor());
        if (color == null) return;

        Firework fw = (Firework) world.spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
            fw2.setFireworkMeta(fwm);
        }
    }

    public void reset() {
        state = GameState.WAITING;
        cancelGameTimer();
        cancelAlertTasks();
        dropManager.cancelDropTimer();
        teamManager.clearAll();
        kitManager.resetAll();
        scoreboardManager.resetAll();
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setDisplayName(p.getName());
            p.setPlayerListName(p.getName());
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setGameMode(GameMode.SURVIVAL);
            p.setAllowFlight(false);
            p.setFlying(false);
            for (PotionEffect pe : p.getActivePotionEffects()) {
                p.removePotionEffect(pe.getType());
            }
        });

        Bukkit.broadcastMessage("\n§6§lRESET " + plugin.getConfigManager().getString("game.reset") + "\n ");
    }

    private void cancelGameTimer() {
        if (gameEndTimer != null && !gameEndTimer.isCancelled()) {
            gameEndTimer.cancel();
            gameEndTimer = null;
        }
    }

    public boolean isState(GameState s) {
        return state == s;
    }

    public boolean isWaiting() {
        return isState(GameState.WAITING);
    }

    public DoorManager getDoorManager() {
        return doorManager;
    }

    public Map<Material, Integer> getDropScores() {
        return dropScores;
    }

    public SbManager getScoreboardManager() {
        return scoreboardManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public DropManager getDropManager() {
        return dropManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }
}