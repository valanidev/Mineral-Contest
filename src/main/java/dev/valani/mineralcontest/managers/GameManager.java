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
    private final DropManager dropManager;
    private final TeamManager teamManager;
    private final KitManager kitManager;

    private BukkitTask gameEndTimer;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.arenaManager = new ArenaManager(plugin, this);
        this.dropManager = new DropManager(plugin, this);
        this.teamManager = new TeamManager(plugin);
        this.kitManager = new KitManager(plugin);
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

        dropManager.scheduleNextDrop();

        if (arenaManager != null && arenaManager.getChestLocation() != null) {
            arenaManager.scheduleAvailability();
        }

        return GameResult.SUCCESS;
    }

    public GameResult end() {
        if (isState(GameState.ENDED)) return GameResult.ALREADY_ENDED;

        state = GameState.ENDED;
        cancelGameTimer();
        dropManager.cancelDropTimer();
        Bukkit.broadcastMessage(plugin.getString("game.ended"));

        return GameResult.SUCCESS;
    }

    public void reset() {
        state = GameState.WAITING;                              // Reset game state to waiting
        cancelGameTimer();                                          // Cancel the end timer
        dropManager.cancelDropTimer();                              // Cancel the drop timer
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

    public boolean isState(GameState s) {
        return state == s;
    }
}