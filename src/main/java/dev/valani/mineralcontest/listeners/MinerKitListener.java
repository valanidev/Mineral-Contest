package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.kits.KitMiner;
import dev.valani.mineralcontest.managers.KitManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class MinerKitListener implements Listener {

    private final KitManager kitManager;

    public MinerKitListener(KitManager manager) {
        this.kitManager = manager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(kitManager.getKit(player) instanceof KitMiner kitMiner)) return;

        Inventory clicked = event.getClickedInventory();
        if (clicked != null && clicked.equals(player.getInventory())) {
            if (kitMiner.isBlockedSlot(event.getSlot())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(kitManager.getKit(player) instanceof KitMiner kitMiner)) return;

        for (int rawSlot : event.getRawSlots()) {
            Inventory inv = event.getView().getInventory(rawSlot);
            if (inv != null && inv.equals(player.getInventory())) {
                if (kitMiner.isBlockedSlot(event.getView().convertSlot(rawSlot))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!(kitManager.getKit(player) instanceof KitMiner)) return;

        Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player);
        if (drops.isEmpty()) return;

        for (ItemStack drop : drops) {
            ItemStack smelted = getSmeltedResult(drop);

            if (smelted != null) {
                event.setDropItems(false);
                smelted.setAmount(drop.getAmount());
                player.getInventory().addItem(smelted);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    private ItemStack getSmeltedResult(ItemStack input) {
        Material result = switch (input.getType()) {
            case RAW_IRON, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case RAW_GOLD, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            default -> null;
        };

        if (result == null) return null;

        return new ItemStack(result, input.getAmount());
    }
}
