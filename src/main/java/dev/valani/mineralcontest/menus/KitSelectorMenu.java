package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.KitManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitSelectorMenu extends MenuBase{
    private static final String TITLE = "§8Choisis un kit";

    private final KitManager kitManager;

    public KitSelectorMenu(KitManager kitManager) {
        super(TITLE, 1);
        this.kitManager = kitManager;
    }

    @Override
    protected void update(Player player) {
        List<KitBase> kits = kitManager.getKits();

        for (int i = 0; i < kits.size(); i++) {
            getInventory().setItem(i, buildKitItem(kits.get(i), player));
        }
    }

    private ItemStack buildKitItem(KitBase kit, Player player) {
        ItemStack item = new ItemStack(
                kit.getMaterial() == null ? Material.STONE : kit.getMaterial()
        );

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        boolean selected = kitManager.hasKit(player, kit);

        meta.setDisplayName(
                "§7" + kit.getDisplayName() + (selected ? "§8 (sélectionné) " : "")
        );

        List<String> lore = new ArrayList<>(List.of(kit.getDescription().split("\n")));

        lore.add("§r");
        lore.add(selected
                ? "§a§lKit actuel"
                : "§6Clique pour sélectionner");

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }
}
