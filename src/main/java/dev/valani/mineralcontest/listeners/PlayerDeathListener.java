package dev.valani.mineralcontest.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    private final List<Material> allowedDrops = List.of(Material.DIAMOND, Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD);

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> items = event.getDrops();
        items.removeIf(item -> !allowedDrops.contains(item.getType()));
        player.sendMessage("§cVous êtes mort et avez perdu vos minerais...");
    }

}
