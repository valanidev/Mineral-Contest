package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerTakeDamageListener implements Listener {
    private final GameManager gameManager;

    public PlayerTakeDamageListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (((Player) event.getEntity()).getGameMode() != GameMode.SURVIVAL) return;
        if (gameManager.isState(GameState.STARTED)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Team damagerTeam = gameManager.getPlayerTeam(damager).orElse(null);
        Team victimTeam = gameManager.getPlayerTeam(victim).orElse(null);
        if (damagerTeam != victimTeam) return;

        event.setCancelled(true);
    }
}
