package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Drop;
import dev.valani.mineralcontest.utils.FormatUtil;
import dev.valani.mineralcontest.utils.LogUtil;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TimeManager {

    private final Main plugin;
    private final ConfigManager configManager;
    private final List<Integer> alertThresholds;

    private int timeLeft;
    private BukkitTask tickTask;
    private BukkitTask dropTask;
    private BukkitTask arenaTask;

    private final List<Drop> activeDrops = new ArrayList<>();

    public TimeManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.alertThresholds  = configManager.getIntList("settings.alerts");
        this.timeLeft = configManager.getInt("settings.duration");
    }

    public void start() {
        timeLeft = configManager.getInt("settings.duration");
        loadDrops();
        startTicking();
        startDropTask();
        startArenaTask();
    }

    public void stop() {
        cancelTicking();
        cancelDropTask();
        cancelArenaTask();

        saveDrops();
        for (Drop drop : activeDrops) {
            drop.remove();
        }
        activeDrops.clear();

        timeLeft = configManager.getInt("settings.duration");
    }

    private void loadDrops() {}
    private void saveDrops() {}

    public void pause() {
        cancelTicking();
        cancelDropTask();
        cancelArenaTask();
    }

    public void resume() {
        startTicking();
        startDropTask();
        startArenaTask();
    }

    private void startTicking() {
        cancelTicking();
        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    private void cancelTicking() {
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
            tickTask = null;
        }
    }

    private void tick() {
        timeLeft--;

        if(timeLeft <= 0) {
            cancelTicking();
            cancelDropTask();
            plugin.getGameManager().endGame();
            return;
        }

        sendAlertIfNeeded();

        BorderManager borderManager = plugin.getGameManager().getBorderManager();
        if(borderManager != null) borderManager.tick(timeLeft);
    }

    private void sendAlertIfNeeded() {
        if (!alertThresholds.contains(timeLeft)) return;

        String formatted = FormatUtil.formatTimeLong(timeLeft);
        String message = configManager.getString("messages.time_alert")
                .replace("%time%", formatted);
        Bukkit.broadcastMessage(message);
        SoundUtil.playForAll(Sound.BLOCK_NOTE_BLOCK_PLING);
    }

    private void startDropTask() {
        cancelDropTask();

        int min = configManager.getInt("settings.drops.timer.min");
        int max = configManager.getInt("settings.drops.timer.max");

        if(min <= 0 || max < min) {
            LogUtil.error("Intervalle de drop incorrecte dans la configuration.");
            return;
        }

        long delaySeconds = ThreadLocalRandom.current().nextInt(min, max + 1);
        LogUtil.log("Le prochain drop apparaîtra dans " + delaySeconds + " secondes");

        dropTask = Bukkit.getScheduler().runTaskLater(plugin, this::spawnDrop, delaySeconds * 20L);

    }

    public void spawnDrop() {
        Drop drop = Drop.create(plugin);
        if (drop != null) {
            activeDrops.add(drop);
        }
        startDropTask();
    }

    public void onDropLooted(Drop drop) {
        activeDrops.remove(drop);
    }

    public List<Drop> getActiveDrops() {
        return Collections.unmodifiableList(activeDrops);
    }

    private void cancelDropTask() {
        if (dropTask != null && !dropTask.isCancelled()) {
            dropTask.cancel();
            dropTask = null;
        }
    }

    public void startArenaTask() {
        cancelArenaTask();

        ArenaManager arenaManager = plugin.getGameManager().getArenaManager();

        if (arenaManager.getChestLocation() == null) {
            LogUtil.error("Aucun coffre d'arène défini, spawn ignoré.");
            return;
        }

        int min = configManager.getInt("settings.arena.time.min");
        int max = configManager.getInt("settings.arena.time.max");

        if (min <= 0 || max < min) {
            LogUtil.error("Intervalle d'arène incorrecte dans la configuration.");
            return;
        }

        long delaySeconds = ThreadLocalRandom.current().nextLong(min, max + 1);
        LogUtil.log("Le coffre d'arène apparaîtra dans " + delaySeconds + " secondes.");

        if (delaySeconds > 5) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!plugin.getGameManager().isInGame()) return;
                Bukkit.broadcastMessage(configManager.getString("messages.arena_chest_soon").replace("%time%", String.valueOf(5)));
                SoundUtil.playForAll(Sound.BLOCK_NOTE_BLOCK_PLING);
            }, (delaySeconds - 5) * 20L);
        }

        arenaTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!plugin.getGameManager().isInGame()) return;
            arenaManager.makeAvailable();
        }, delaySeconds * 20L);
    }

    private void cancelArenaTask() {
        if (arenaTask != null && !arenaTask.isCancelled()) {
            arenaTask.cancel();
            arenaTask = null;
        }
    }

    // --- Setters ---

    public void setTimeLeft(int seconds) {
        this.timeLeft = Math.max(0, seconds);
    }

    public void addTime(int seconds) {
        this.timeLeft = Math.max(0, timeLeft + seconds);
    }

    // --- Getters ---

    public int getTimeLeft() { return timeLeft; }
    public boolean isRunning() { return tickTask != null && !tickTask.isCancelled(); }

}
