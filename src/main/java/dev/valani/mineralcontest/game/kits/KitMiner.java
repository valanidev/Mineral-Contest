package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class KitMiner extends KitBase {
    private static final int[] BLOCKED_SLOTS = {9, 18, 27, 17, 26, 35};
    private static final ItemStack BLOCKED_ITEM = buildBlockedItem();

    private static final float BLOCK_BREAK_SPEED_MULTIPLIER = 1.10f;

    private static final AttributeModifier BLOCK_BREAK_SPEED_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_miner_mining_speed")),
            BLOCK_BREAK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitMiner() {
        super(
                "Mineur",
                "§a+ Cuit automatiquement les minerais.\n§a+ Minez 10% plus rapidement les blocs.\n§c- Retire 6 slots d'inventaire.",
                Material.IRON_PICKAXE
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance miningSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        applyModifier(miningSpeed, BLOCK_BREAK_SPEED_MODIFIER);
        for (int slot : BLOCKED_SLOTS) {
            player.getInventory().setItem(slot, BLOCKED_ITEM);
        }
        Utils.applyItems(player);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), BLOCK_BREAK_SPEED_MODIFIER.getKey());
//        for (int slot : BLOCKED_SLOTS) {
//            ItemStack current = player.getInventory().getItem(slot);
//            if (current != null && current.isSimilar(BLOCKED_ITEM)) {
//                player.getInventory().setItem(slot, null);
//            }
//        }
        player.getInventory().clear();
    }

    public boolean isBlockedSlot(int slot) {
        for (int s : BLOCKED_SLOTS) if (s == slot) return true;
        return false;
    }

    private static ItemStack buildBlockedItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
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
