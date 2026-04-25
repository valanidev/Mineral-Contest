package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerTakeDamageListener implements Listener {
    private final TeamManager teamManager;

    public PlayerTakeDamageListener(GameManager gameManager) {
        this.teamManager = gameManager.getTeamManager();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Team damagerTeam = teamManager.getPlayerTeam(damager).orElse(null);
        Team victimTeam = teamManager.getPlayerTeam(victim).orElse(null);
        if (damagerTeam != victimTeam) return;

        event.setCancelled(true);
    }
}
