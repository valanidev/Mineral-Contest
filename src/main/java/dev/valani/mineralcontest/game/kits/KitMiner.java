package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class KitMiner extends KitBase {
    private static final int[] BLOCKED_SLOTS = {9, 18, 27, 17, 26, 35};
    private static final ItemStack BLOCKED_ITEM = buildBlockedItem();

    public KitMiner() {
        super(
                "Mineur",
                "§a+ Cuit automatiquement les minerais.\n§c- Retire 6 slots d'inventaire.",
                Material.IRON_PICKAXE
        );
    }

    @Override
    public void apply(Player player) {
        for (int slot : BLOCKED_SLOTS) {
            player.getInventory().setItem(slot, BLOCKED_ITEM);
        }
    }

    @Override
    public void remove(Player player) {
        for (int slot : BLOCKED_SLOTS) {
            ItemStack current = player.getInventory().getItem(slot);
            if (current != null && current.isSimilar(BLOCKED_ITEM)) {
                player.getInventory().setItem(slot, null);
            }
        }
    }

    public boolean isBlockedSlot(int slot) {
        for (int s : BLOCKED_SLOTS) if (s == slot) return true;
        return false;
    }

    private static ItemStack buildBlockedItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§8Slot bloqué");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getBlockedItem() {
        return BLOCKED_ITEM;
    }

    public ItemStack getSmeltedResult(Player player, ItemStack itemStack) {
        return null;
    }
}
