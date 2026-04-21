package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final GameManager gameManager;

    public PlayerQuitListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        gameManager.getHealthDisplay().applyToPlayer(event.getPlayer());

        event.setQuitMessage("§a- " + player.getDisplayName() + " §aa quitté le serveur.");
    }
}
