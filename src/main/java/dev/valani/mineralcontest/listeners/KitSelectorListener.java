package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class KitSelectorListener implements Listener {

    private final Main plugin;
    private final GameManager gameManager;
    private final KitManager kitManager;
    private final KitSelectorMenu menu;

    public KitSelectorListener(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.kitManager = gameManager.getKitManager();
        this.menu = gameManager.getKitSelectorMenu();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(menu.getInventory())) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        if (!gameManager.isKitSelect()) {
            player.sendMessage("§cTu ne peux pas changer de kit hors de la phase de choix du kit !");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        int slot = event.getRawSlot();
        List<KitBase> kits = kitManager.getKits();
        if (slot >= kits.size()) return;

        KitBase selected = kits.get(slot);

        if (kitManager.hasKit(player, selected)) {
            player.sendMessage("§cTu as déjà ce kit !");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        boolean broadcast = plugin.getConfigManager().getBoolean("messages.kits.broadcast");
        if(broadcast) Bukkit.broadcastMessage("§6§lKIT §a" + player.getDisplayName() + " §aa choisi le kit " + selected.getDisplayName() + "§a.");

        kitManager.assignKit(player, selected);
        SoundUtil.playForPlayer(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

        menu.open(player);
    }

}
