package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.kits.KitMiner;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.utils.KitUtil;
import dev.valani.mineralcontest.utils.SoundUtil;
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

public class KitMinerListener implements Listener {

    private final KitManager kitManager;

    public KitMinerListener(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(!(kitManager.getKit(player) instanceof KitMiner)) return;

        Collection<ItemStack> drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player);
        if (drops.isEmpty()) return;

        for (ItemStack drop : drops) {
            ItemStack smelted = getSmeltedResult(drop);

            if (smelted != null) {
                event.setExpToDrop(1);
                event.setDropItems(false);
                smelted.setAmount(drop.getAmount());
                player.getInventory().addItem(smelted);
                SoundUtil.playForPlayer(player, Sound.ENTITY_ITEM_PICKUP, 0.1f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(kitManager.getKit(player) instanceof KitMiner)) return;
        Inventory clicked = event.getClickedInventory();
        if(clicked == null) return;
        if (!clicked.equals(player.getInventory())) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getCurrentItem().equals(KitUtil.getBlockedItem())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(kitManager.getKit(player) instanceof KitMiner)) return;

        for (int rawSlot : event.getRawSlots()) {
            Inventory inv = event.getView().getInventory(rawSlot);
            if(inv == null) continue;
            if(!inv.equals(player.getInventory())) continue;
            if (!KitUtil.isBlockedSlot(event.getView().convertSlot(rawSlot))) continue;
            event.setCancelled(true);
        }
    }

    private ItemStack getSmeltedResult(ItemStack input) {
        Material result = switch (input.getType()) {
            case RAW_IRON, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case RAW_GOLD, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            case RAW_COPPER, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            default -> null;
        };
        if (result == null) return null;
        return new ItemStack(result, input.getAmount());
    }

}
