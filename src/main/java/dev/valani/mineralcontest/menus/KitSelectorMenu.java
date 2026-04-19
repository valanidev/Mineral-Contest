package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitSelectorMenu {

    private static final String TITLE = "§8Choisir un kit";
    private final KitManager kitManager;

    public KitSelectorMenu(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    public void open(Player player) {
        List<KitBase> kits = KitManager.KITS;
        int size = Utils.roundToMultipleOf9(kits.size());
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        for (int i = 0; i < kits.size(); i++) {
            inv.setItem(i, buildKitItem(kits.get(i), player));
        }

        player.openInventory(inv);
    }

    private ItemStack buildKitItem(KitBase kit, Player player) {
        ItemStack item = new ItemStack(kit.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        boolean selected = kitManager.hasKit(player, kit);

        meta.setDisplayName((selected ? "§a✔ " : "§7") + kit.getDisplayName());
        List<String> lore = new ArrayList<>(Arrays.asList(kit.getDescription().split("\n")));
        lore.add("");
        lore.add(selected ? "§a§lKit actuel" : "§6Clique pour sélectionner");
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    public String getTitle() {
        return TITLE;
    }
}