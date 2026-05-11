package dev.valani.mineralcontest.game;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.utils.LogUtil;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Drop {

    private final Location location;
    private final List<ItemStack> loot;
    private boolean opening;

    public Drop(Location location, List<ItemStack> loot) {
        this.location = location;
        this.loot = loot;
        this.opening = false;
    }

    public static Drop create(Main plugin) {
        String worldName = plugin.getConfig().getString("settings.world");
        int minX = plugin.getConfig().getInt("settings.drops.location.min");
        int maxX = plugin.getConfig().getInt("settings.drops.location.max");
        int minZ = plugin.getConfig().getInt("settings.drops.location.min");
        int maxZ = plugin.getConfig().getInt("settings.drops.location.max");

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            LogUtil.error("(DROP) Monde introuvable.");
            return null;
        }

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        Location targetLocation = null;
        for (int i = 0; i < 5; i++) {
            int x = rng.nextBoolean()
                    ? rng.nextInt(minX, maxX + 1)
                    : rng.nextInt(-maxX, -minX + 1);
            int z = rng.nextBoolean()
                    ? rng.nextInt(minZ, maxZ + 1)
                    : rng.nextInt(-maxZ, -minZ + 1);
            int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);

            Location loc = new Location(world, x, y, z);
            Material ground = loc.getBlock().getType();

            if (!ground.isSolid()) continue;
            if (ground == Material.WATER) continue;
            if (ground == Material.LAVA) continue;

            targetLocation = loc.clone().add(0, 1, 0);
            break;
        }

        if (targetLocation == null) {
            LogUtil.error("Impossible de trouver un emplacement pour le drop.");
            return null;
        }

        List<ItemStack> loot = generateLoot(plugin);

        Drop drop = new Drop(targetLocation, loot);
        drop.spawn();

        String message = plugin.getConfigManager()
                .getString("messages.drop_spawned")
                .replace("%x%", String.valueOf(targetLocation.getBlockX()))
                .replace("%y%", String.valueOf(targetLocation.getBlockY()))
                .replace("%z%", String.valueOf(targetLocation.getBlockZ()));

        Bukkit.broadcastMessage(message);
        SoundUtil.playForAll(Sound.ENTITY_CHICKEN_EGG);

        return drop;
    }

    private static List<ItemStack> generateLoot(Main plugin) {
        List<ItemStack> items = new ArrayList<>();

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (Map<?, ?> entry : plugin.getConfig().getMapList("settings.drops.loot")) {
            String materialName = (String) entry.get("material");
            int min = ((Number) entry.get("min")).intValue();
            int max = ((Number) entry.get("max")).intValue();
            Material material = Material.getMaterial(materialName);
            if (material == null) {
                LogUtil.error("(DROP) Matériau inconnu '" + materialName + "'");
                continue;
            }
            int amount = rng.nextInt(min, max + 1);
            if(amount <= 0) continue;

            items.add(new ItemStack(material, amount));
        }

        return items;
    }

    public void spawn() {
        location.getBlock().setType(Material.CHEST);
    }

    public void remove() {
        location.getBlock().setType(Material.AIR);
    }

    public void giveLoot(Player player) {
        for (ItemStack item : loot) {
            player.getInventory().addItem(item);
        }
    }

    public Location getLocation() {
        return location;
    }

    public List<ItemStack> getLoot() {
        return loot;
    }

    public boolean isOpening() {
        return opening;
    }

    public void setOpening(boolean opening) {
        this.opening = opening;
    }
}
