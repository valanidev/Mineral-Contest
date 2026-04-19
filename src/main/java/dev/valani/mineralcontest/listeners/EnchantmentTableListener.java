package dev.valani.mineralcontest.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantmentTableListener implements Listener {

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory instanceof EnchantingInventory)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        inventory.setItem(1, new ItemStack(Material.LAPIS_LAZULI, 3));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof EnchantingInventory)) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlot() != 1) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory() instanceof EnchantingInventory)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        event.getInventory().setItem(1, null);
    }

}
