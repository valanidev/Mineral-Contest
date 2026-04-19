package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.utils.FileManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    private final Main plugin;
    private final GameManager gameManager;
    private final FileManager arenaFile;

    private boolean chestAvailable = false;

    private Location chestLocation;
    private Material chestMaterial;

    private BukkitTask availabilityTask;
    private BukkitTask particleTask;

    public ArenaManager(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.arenaFile = new FileManager(plugin, "arena.yml");
        this.chestLocation = loadArenaChestLocation();
        this.chestMaterial = loadArenaChestMaterial();
    }

    public void setArenaChest(Player player) {
        removeArenaChest();
        Location loc = player.getLocation();
        int blockX = loc.getBlockX();
        int blockY = loc.getBlockY();
        int blockZ = loc.getBlockZ();
        Location chestLoc = new Location(loc.getWorld(), blockX, blockY, blockZ);
        String materialName = plugin.getString("arena.chest_block");
        chestMaterial = Utils.parseMaterial(materialName);

        arenaFile.getConfig().set("chest.world", Objects.requireNonNull(chestLoc.getWorld()).getName());
        arenaFile.getConfig().set("chest.x", blockX);
        arenaFile.getConfig().set("chest.y", blockY);
        arenaFile.getConfig().set("chest.z", blockZ);
        arenaFile.save();

        chestLoc.getBlock().setType(chestMaterial);
        chestLocation = chestLoc;
        makeAvailable();
    }

    public void removeArenaChest() {
        if (chestLocation == null) return;

        chestLocation.getBlock().setType(Material.AIR);

        arenaFile.getConfig().set("chest", null);
        arenaFile.save();

        cancelRecoveryTask();
        stopParticleTask();
        chestAvailable = false;
        chestLocation = null;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public Material getChestMaterial() {
        return chestMaterial;
    }

    public boolean isChestAvailable() {
        return chestAvailable;
    }

    private void makeAvailable() {
        cancelRecoveryTask();
        if (gameManager != null && !gameManager.isState(GameState.STARTED)) {
            Bukkit.getConsoleSender().sendMessage("§cLa partie n'a pas encore commencé.");
            return;
        }
        chestAvailable = true;
        Bukkit.broadcastMessage(plugin.getString("arena.chest_available"));
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1));
        startParticleTask();
    }

    public void lockForAnimation() {
        chestAvailable = false;
        stopParticleTask();
    }

    public void unlockFromAnimation() {
        cancelRecoveryTask();
        chestAvailable = true;
        startParticleTask();
    }

    public void scheduleAvailability() {
        cancelRecoveryTask();
        long minSeconds = plugin.getInt("arena.cooldown_min_seconds");
        long maxSeconds = plugin.getInt("arena.cooldown_max_seconds");
        long delayTicks = ThreadLocalRandom.current().nextLong(minSeconds, maxSeconds + 1) * 20L;
        availabilityTask = Bukkit.getScheduler().runTaskLater(plugin, this::makeAvailable, delayTicks);
    }

    public void onChestLooted() {
        cancelRecoveryTask();
        chestAvailable = false;
        stopParticleTask();
        scheduleAvailability();
    }

    private void cancelRecoveryTask() {
        if (availabilityTask != null && !availabilityTask.isCancelled()) {
            availabilityTask.cancel();
        }
        availabilityTask = null;
    }

    private void startParticleTask() {
        stopParticleTask();
        particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (chestLocation == null || chestLocation.getWorld() == null) return;
            chestLocation.getWorld().spawnParticle(
                    Particle.HAPPY_VILLAGER,
                    chestLocation.clone().add(0.5, 0.5, 0.5),
                    8, 0.3, 0.3, 0.3, 0.02
            );
        }, 0L, 20L);
    }

    private void stopParticleTask() {
        if (particleTask != null && !particleTask.isCancelled()) {
            particleTask.cancel();
        }
        particleTask = null;
    }

    private Location loadArenaChestLocation() {
        if (!arenaFile.getConfig().contains("chest.world")) return null;
        String worldName = arenaFile.getConfig().getString("chest.world", "world");
        return new Location(
                Bukkit.getWorld(worldName),
                arenaFile.getConfig().getInt("chest.x"),
                arenaFile.getConfig().getInt("chest.y"),
                arenaFile.getConfig().getInt("chest.z")
        );
    }

    private Material loadArenaChestMaterial() {
        String name = plugin.getString("arena.chest_block");
        return Utils.parseMaterial(name);
    }
}
