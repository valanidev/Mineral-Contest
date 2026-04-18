package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.game.Drop;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private final Main plugin;
    private GameState state;
    private final List<Team> teams;
    private BukkitTask endTimer;
    private BukkitTask dropTimer;
    private CommandArenaChest arenaChestCommand;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.state = GameState.WAITING;
        this.teams = loadTeamsFromConfig();
    }

    private List<Team> loadTeamsFromConfig() {
        List<Team> result = new ArrayList<>();
        int maxPlayers = plugin.getInt("game.max_players_per_team");

        for (String key : plugin.getConfig().getConfigurationSection("game.teams").getKeys(false)) {
            String path = "game.teams." + key;
            String name = plugin.getConfig().getString(path + ".name", key);
            ChatColor color = parseChatColor(plugin.getConfig().getString(path + ".color", "WHITE"));
            Material material = parseMaterial(plugin.getConfig().getString(path + ".icon", "WHITE_WOOL"));
            result.add(new Team(name, color, material, maxPlayers));
        }

        return result;
    }

    private ChatColor parseChatColor(String value) {
        try {
            return ChatColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.consoleError("Couleur invalide en config : " + value);
            return ChatColor.WHITE;
        }
    }

    private Material parseMaterial(String value) {
        Material mat = Material.getMaterial(value.toUpperCase());
        if (mat == null) {
            plugin.consoleError("Matériau invalide en config : " + value);
            return Material.WHITE_WOOL;
        }
        return mat;
    }

    public GameResult start() {
        if (!isState(GameState.WAITING)) return GameResult.ALREADY_STARTED;

        state = GameState.STARTED;
        Bukkit.broadcastMessage(plugin.getString("game.started"));

        int durationSeconds = plugin.getInt("game.duration_seconds");
        endTimer = Bukkit.getScheduler().runTaskLater(plugin, this::end, durationSeconds * 20L);

        scheduleNextDrop();

        if (arenaChestCommand != null && arenaChestCommand.getCachedChestLocation() != null) {
            arenaChestCommand.scheduleAvailability();
        }

        return GameResult.SUCCESS;
    }

    public GameResult end() {
        if (isState(GameState.ENDED)) return GameResult.ALREADY_ENDED;

        state = GameState.ENDED;
        cancelTimer();
        cancelDropTimer();
        Bukkit.broadcastMessage(plugin.getString("game.ended"));

        return GameResult.SUCCESS;
    }

    public GameResult reset() {
        state = GameState.WAITING;
        cancelTimer();
        cancelDropTimer();
        teams.forEach(Team::clear);
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setDisplayName(p.getName());
            p.setPlayerListName(p.getName());
        });
        Bukkit.broadcastMessage(plugin.getString("game.reset"));

        return GameResult.SUCCESS;
    }

    private void cancelTimer() {
        if (endTimer != null && !endTimer.isCancelled()) {
            endTimer.cancel();
            endTimer = null;
        }
    }

    private void scheduleNextDrop() {
        long minSeconds = plugin.getInt("drop.min_interval_seconds");
        long maxSeconds = plugin.getInt("drop.max_interval_seconds");
        long delayTicks = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds + 1) * 20L;
        dropTimer = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!isState(GameState.STARTED)) return;
            new Drop(plugin);
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));
            scheduleNextDrop();
        }, delayTicks);
    }

    private void cancelDropTimer() {
        if (dropTimer != null && !dropTimer.isCancelled()) {
            dropTimer.cancel();
            dropTimer = null;
        }
    }

    public GameResult joinTeam(Player player, Team team) {
        if (!isState(GameState.WAITING)) return GameResult.GAME_ALREADY_STARTED;
        if (team.hasMember(player)) return GameResult.ALREADY_IN_TEAM;
        if (team.isFull()) return GameResult.TEAM_FULL;

        getPlayerTeam(player).ifPresent(t -> t.removeMember(player));

        team.addMember(player);
        return GameResult.SUCCESS;
    }

    public Optional<Team> getPlayerTeam(Player player) {
        return teams.stream()
                .filter(t -> t.hasMember(player))
                .findFirst();
    }

    public List<Team> getTeams() {
        return List.copyOf(teams);
    }

    public GameState getState() {
        return state;
    }

    public boolean isState(GameState s) {
        return state == s;
    }

    public void setArenaChestCommand(CommandArenaChest arenaChestCommand) {
        this.arenaChestCommand = arenaChestCommand;
    }
}