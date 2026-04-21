package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class VillagerRightClickListener implements Listener {
    private final GameManager gameManager;

    public VillagerRightClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onVillagerRightClick(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        EntityType entityType = event.getRightClicked().getType();
        if (!gameManager.isState(GameState.STARTED)) return;
        if (!entityType.equals(EntityType.VILLAGER)) return;

        player.sendMessage("§c§lRATÉ §cLe trade est désactivé.");
        event.setCancelled(true);
    }
}
