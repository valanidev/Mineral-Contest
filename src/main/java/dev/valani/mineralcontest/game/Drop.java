package dev.valani.mineralcontest.game;

import dev.valani.mineralcontest.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Drop {

    private final int maxAttempts = 4;

    public Drop(Main plugin) {
        int minX = plugin.getConfig().getInt("drop.spawn_location.min_x");
        int maxX = plugin.getConfig().getInt("drop.spawn_location.max_x");
        int minZ = plugin.getConfig().getInt("drop.spawn_location.min_z");
        int maxZ = plugin.getConfig().getInt("drop.spawn_location.max_z");
        World world = Bukkit.getWorlds().getFirst();
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int attempts = 0;

        Location targetLocation = null;
        while (attempts < maxAttempts) {
            attempts++;
            int x = rng.nextBoolean()
                    ? rng.nextInt(minX, maxX + 1)
                    : rng.nextInt(-maxX, -minX + 1);
            int z = rng.nextBoolean()
                    ? rng.nextInt(minZ, maxZ + 1)
                    : rng.nextInt(-maxZ, -minZ + 1);
            int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            Location loc = new Location(world, x, y, z);
            Material ground = loc.getBlock().getType();

            if (ground == Material.WATER || ground == Material.LAVA) continue;
            if (!ground.isSolid()) continue;

            targetLocation = loc.clone().add(0, 1, 0);
            break;
        }

        if (targetLocation == null) {
            Bukkit.getConsoleSender().sendMessage("§cImpossible de trouver un emplacement pour le drop.");
            return;
        }

        targetLocation.getBlock().setType(Material.CHEST);

        if (targetLocation.getBlock().getState() instanceof Chest chest) {
            fillChest(plugin, chest);
        }

        String message = plugin.getConfigManager().getString("drop.chest_placed")
                .replace("{X}", String.valueOf(targetLocation.getBlockX()))
                .replace("{Y}", String.valueOf(targetLocation.getBlockY()))
                .replace("{Z}", String.valueOf(targetLocation.getBlockZ()));
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + message);
    }

    private void fillChest(Main plugin, Chest chest) {
        List<Map<?, ?>> loot = plugin.getConfig().getMapList("drop.loot");
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (Map<?, ?> entry : loot) {
            String materialName = (String) entry.get("material");
            int min = ((Number) entry.get("min")).intValue();
            int max = ((Number) entry.get("max")).intValue();
            Material material = Material.getMaterial(materialName);
            if (material == null) {
                Bukkit.getConsoleSender().sendMessage("§cdrop.loot: matériau inconnu '" + materialName + "'");
                continue;
            }
            int amount = rng.nextInt(min, max + 1);
            chest.getBlockInventory().addItem(new ItemStack(material, amount));
        }
    }
}
