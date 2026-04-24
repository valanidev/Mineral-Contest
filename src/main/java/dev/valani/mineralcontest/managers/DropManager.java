package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Drop;
import dev.valani.mineralcontest.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public class DropManager {

    private final Main plugin;
    private final GameManager gameManager;

    private BukkitTask dropTimer;

    public DropManager(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void scheduleNextDrop() {
        long minSeconds = plugin.getConfigManager().getInt("drop.min_interval_seconds");
        long maxSeconds = plugin.getConfigManager().getInt("drop.max_interval_seconds");
        long delayTicks = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds + 1) * 20L;
        dropTimer = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!gameManager.isState(GameState.STARTED)) return;
            spawnDrop();
            scheduleNextDrop();
        }, delayTicks);
    }

    public void spawnDrop() {
        new Drop(plugin);
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f));
    }

    public void cancelDropTimer() {
        if (dropTimer != null && !dropTimer.isCancelled()) {
            dropTimer.cancel();
            dropTimer = null;
        }
    }
}
