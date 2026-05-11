package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class MenuBase implements InventoryHolder {

    private final Inventory inventory;

    protected MenuBase(String title, int rows) {
        if(rows < 1 || rows > 6)
            LogUtil.error("Nombre de lignes invalide pour l'inventaire " + title + ": " + rows);
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    protected abstract void update(Player player);

    public final void open(Player player) {
        inventory.clear();
        update(player);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}