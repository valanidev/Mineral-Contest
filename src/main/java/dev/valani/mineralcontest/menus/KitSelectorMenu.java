package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitSelectorMenu {

    private static final String TITLE = "§8Choisir un kit";
    private final KitManager kitManager;

    public KitSelectorMenu(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    public void open(Player player) {
        List<KitBase> kits = KitManager.KITS;
        int size = (int) Math.ceil(kits.size() / 9.0) * 9;
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        for (int i = 0; i < kits.size(); i++) {
            inv.setItem(i, buildKitItem(kits.get(i), player));
        }

        player.openInventory(inv);
    }

    private ItemStack buildKitItem(KitBase kit, Player player) {
        ItemStack item = new ItemStack(kit.getMaterial());
        ItemMeta meta = item.getItemMeta();


        boolean selected = kitManager.hasKit(player, kit);

        meta.setDisplayName((selected ? "§a✔ " : "§f") + kit.getDisplayName());
        meta.setLore(List.of(
                kit.getDescription(),
                "",
                selected ? "§a§lKit actuel" : "§eCliquer pour sélectionner"
        ));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    public String getTitle() {
        return TITLE;
    }
}