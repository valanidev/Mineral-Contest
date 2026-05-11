package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.utils.ParseUtil;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class ArenaManager {

    private final Main plugin;

    private final FileManager arenaFile;

    private Location chestLocation;
    private Material chestMaterial;
    private boolean chestAvailable = false;

    private BukkitTask particleTask;

    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        this.arenaFile = new FileManager(plugin, "arena.yml");
        this.chestLocation = loadArenaChestLocation();
        this.chestMaterial = loadArenaChestMaterial();
    }

    public void setArenaChest(Location loc) {
        removeArenaChest();

        int blockX = loc.getBlockX();
        int blockY = loc.getBlockY();
        int blockZ = loc.getBlockZ();
        Location chestLoc = new Location(loc.getWorld(), blockX, blockY, blockZ);
        String materialName = plugin.getConfigManager().getString("settings.arena.chest_block");
        chestMaterial = ParseUtil.parseMaterial(materialName);

        arenaFile.getConfig().set("chest.world", Objects.requireNonNull(chestLoc.getWorld()).getName());
        arenaFile.getConfig().set("chest.x", blockX);
        arenaFile.getConfig().set("chest.y", blockY);
        arenaFile.getConfig().set("chest.z", blockZ);
        arenaFile.save();

        chestLoc.getBlock().setType(chestMaterial);
        chestLocation = chestLoc;
    }

    public void removeArenaChest() {
        if (chestLocation == null) return;

        chestLocation.getBlock().setType(Material.AIR);

        arenaFile.getConfig().set("chest", null);
        arenaFile.save();

        makeUnavailable();
        chestLocation = null;
    }

    public void makeAvailable() {
        chestAvailable = true;
        startParticleTask();
        Bukkit.broadcastMessage(plugin.getConfigManager().getString("messages.arena_chest_available"));
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1));
    }

    public void lockForAnimation() {
        chestAvailable = false;
    }

    public void unlockFromAnimation() {
        chestAvailable = true;
    }

    public void onChestLooted() {
        makeUnavailable();
        plugin.getGameManager().getTimeManager().startArenaTask();
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

    // --- Loaders ---

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
        String name = plugin.getConfigManager().getString("settings.arena.chest_block");
        return ParseUtil.parseMaterial(name);
    }

    public void makeUnavailable() {
        chestAvailable = false;
        stopParticleTask();
    }

    // --- Getters ---

    public Location getChestLocation() {
        return chestLocation;
    }

    public Material getChestMaterial() {
        return chestMaterial;
    }

    public boolean isChestAvailable() {
        return chestAvailable;
    }

}
