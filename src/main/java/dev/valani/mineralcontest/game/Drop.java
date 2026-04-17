package dev.valani.mineralcontest.game;

import dev.valani.mineralcontest.Main;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Drop {
    public Drop(Main plugin) {
        int minX = -1000;
        int maxX = 1000;
        int minZ = -1000;
        int maxZ = 1000;

        World world = Bukkit.getWorld("world");
        if (world == null) return;

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int x = rng.nextInt(minX, maxX + 1);
        int z = rng.nextInt(minZ, maxZ + 1);

        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
        Location targetLocation = new Location(world, x, y + 1, z);
        targetLocation.getBlock().setType(Material.CHEST);

        if (targetLocation.getBlock().getState() instanceof Chest chest) {
            fillChest(plugin, chest);
        }

        String message = plugin.getString("drop.chest_placed")
                .replace("{X}", String.valueOf(x))
                .replace("{Y}", String.valueOf(y))
                .replace("{Z}", String.valueOf(z));
        Bukkit.broadcastMessage(plugin.getPrefix() + message);
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
                plugin.consoleError("drop.loot: matériau inconnu '" + materialName + "'");
                continue;
            }
            int amount = rng.nextInt(min, max + 1);
            chest.getBlockInventory().addItem(new ItemStack(material, amount));
        }
    }
}
