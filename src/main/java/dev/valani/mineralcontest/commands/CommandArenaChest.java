package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class CommandArenaChest implements CommandExecutor {
    private final Main plugin;
    private final FileManager arenaFile;
    private Location cachedChestLocation;
    private Material cachedChestMaterial;
    private BukkitTask particleTask;
    private BukkitTask availabilityTask;
    private boolean chestAvailable = false;

    public CommandArenaChest(Main plugin, FileManager arenaFile) {
        this.plugin = plugin;
        this.arenaFile = arenaFile;
        this.cachedChestLocation = loadChestLocationFromFile();
        this.cachedChestMaterial = loadChestMaterialFromConfig();
        if (cachedChestLocation != null) makeAvailable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: §e\n- set \n- remove \n- view");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "set" -> {
                setArenaChest(player);
                player.sendMessage(plugin.getString("arena.chest_placed"));
            }
            case "remove" -> {
                if (cachedChestLocation == null) {
                    player.sendMessage(plugin.getString("arena.chest_not_placed"));
                    break;
                }
                removeArenaChest();
                player.sendMessage(plugin.getString("arena.chest_removed"));
            }
            case "view" -> {
                if (cachedChestLocation == null) {
                    player.sendMessage(plugin.getString("arena.chest_not_placed"));
                    break;
                }
                player.sendMessage(plugin.getString("arena.chest_location").replace("{LOCATION}", formatLocation(cachedChestLocation)));
            }

            default -> {
                player.sendMessage(plugin.getString("plugin.no_such_args"));
                return false;
            }
        }

        return true;
    }

    private Location loadChestLocationFromFile() {
        if (!arenaFile.getConfig().contains("chest.world")) return null;
        String worldName = arenaFile.getConfig().getString("chest.world", "world");
        return new Location(
                Bukkit.getWorld(worldName),
                arenaFile.getConfig().getInt("chest.x"),
                arenaFile.getConfig().getInt("chest.y"),
                arenaFile.getConfig().getInt("chest.z")
        );
    }

    private Material loadChestMaterialFromConfig() {
        String name = plugin.getString("arena.chest_block");
        Material mat = Material.getMaterial(name);
        if (mat == null) Bukkit.getLogger().warning("[MineralContest] Matériau invalide en config : " + name);
        return mat;
    }

    public Location getCachedChestLocation() {
        return cachedChestLocation;
    }

    public Material getCachedChestMaterial() {
        return cachedChestMaterial;
    }

    public boolean isChestAvailable() {
        return chestAvailable;
    }

    private String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                + " (" + Objects.requireNonNull(loc.getWorld()).getName() + ")";
    }

    void setArenaChest(Player player) {
        removeArenaChest();
        Location loc = player.getLocation();
        String materialName = plugin.getString("arena.chest_block");

        arenaFile.getConfig().set("chest.world", Objects.requireNonNull(loc.getWorld()).getName());
        arenaFile.getConfig().set("chest.x", loc.getBlockX());
        arenaFile.getConfig().set("chest.y", loc.getBlockY());
        arenaFile.getConfig().set("chest.z", loc.getBlockZ());
        arenaFile.save();

        loc.getBlock().setType(cachedChestMaterial);
        cachedChestLocation = loc;
        makeAvailable();
    }

    void removeArenaChest() {
        if (cachedChestLocation == null) return;

        cachedChestLocation.getBlock().setType(Material.AIR);

        arenaFile.getConfig().set("chest", null); // supprime toute la section d'un coup
        arenaFile.save();

        cancelRecoveryTask();
        stopParticleTask();
        chestAvailable = false;
        cachedChestLocation = null;
    }

    private void makeAvailable() {
        cancelRecoveryTask();
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

    public void onChestLooted() {
        cancelRecoveryTask();
        chestAvailable = false;
        stopParticleTask();
        long delayTicks = (long) ((0 + Math.random() * 1) * 60 * 20);
        availabilityTask = Bukkit.getScheduler().runTaskLater(plugin, this::makeAvailable, delayTicks);
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
            if (cachedChestLocation == null || cachedChestLocation.getWorld() == null) return;
            cachedChestLocation.getWorld().spawnParticle(
                    Particle.HAPPY_VILLAGER,
                    cachedChestLocation.clone().add(0.5, 0.5, 0.5),
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
}
