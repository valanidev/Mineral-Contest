package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.game.Drop;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private final Main plugin;
    private GameState state;

    private final ArenaManager arenaManager;
    private final TeamManager teamManager;
    private final KitManager kitManager;

    private BukkitTask gameEndTimer;
    private BukkitTask dropTimer;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.arenaManager = new ArenaManager(plugin, this);
        this.teamManager = new TeamManager(plugin);
        this.kitManager = new KitManager(plugin);

        Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage(teamManager.toString());
                    player.sendMessage(kitManager.toString());
                }
        );

        reset();
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameResult start() {
        if (!isState(GameState.WAITING)) return GameResult.ALREADY_STARTED;

        state = GameState.STARTED;
        Bukkit.broadcastMessage(plugin.getString("game.started"));

        int durationSeconds = plugin.getInt("game.duration_seconds");
        gameEndTimer = Bukkit.getScheduler().runTaskLater(plugin, this::end, durationSeconds * 20L);

        scheduleNextDrop();

        if (arenaManager != null && arenaManager.getChestLocation() != null) {
            arenaManager.scheduleAvailability();
        }

        return GameResult.SUCCESS;
    }

    public GameResult end() {
        if (isState(GameState.ENDED)) return GameResult.ALREADY_ENDED;

        state = GameState.ENDED;
        cancelGameTimer();
        cancelDropTimer();
        Bukkit.broadcastMessage(plugin.getString("game.ended"));

        return GameResult.SUCCESS;
    }

    public void reset() {
        state = GameState.WAITING;                              // Reset game state to waiting
        cancelGameTimer();                                          // Cancel the end timer
        cancelDropTimer();                                      // Cancel the drop timer
        this.teamManager.clearAll();                             // Clear all teams
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.setDisplayName(p.getName());
            p.setPlayerListName(p.getName());
        });                                                     // Reset player names
        Bukkit.broadcastMessage(plugin.getString("game.reset"));
    }

    private void cancelGameTimer() {
        if (gameEndTimer != null && !gameEndTimer.isCancelled()) {
            gameEndTimer.cancel();
            gameEndTimer = null;
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

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameState getState() {
        return state;
    }

    public boolean isState(GameState s) {
        return state == s;
    }
}