package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class KitSelectorListener implements Listener {

    private final GameManager gameManager;
    private final KitManager kitManager;
    private final KitSelectorMenu menu;

    public KitSelectorListener(GameManager gameManager, KitManager kitManager, KitSelectorMenu menu) {
        this.gameManager = gameManager;
        this.kitManager = kitManager;
        this.menu = menu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(menu.getTitle())) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        if (!gameManager.isState(GameState.WAITING)) {
            player.sendMessage("§cTu ne peux pas changer de kit pendant la partie !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        int slot = event.getRawSlot();
        List<KitBase> kits = KitManager.KITS;
        if (slot >= kits.size()) return;

        KitBase selected = kits.get(slot);

        if (kitManager.hasKit(player, selected)) {
            player.sendMessage("§cTu as déjà ce kit !");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        kitManager.assignKit(player, selected);
        player.sendMessage("§aKit §f" + selected.getDisplayName() + " §asélectionné !");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        menu.open(player);
    }
}