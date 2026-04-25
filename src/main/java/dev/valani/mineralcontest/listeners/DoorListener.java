package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.managers.DoorManager;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

public class DoorListener implements Listener {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final DoorManager doorManager;

    public DoorListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.teamManager = gameManager.getTeamManager();
        this.doorManager = gameManager.getDoorManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!hasMoved(event)) return;

//        if (!gameManager.isState(GameState.STARTED)) return;

        Player player = event.getPlayer();
        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);

        doorManager.onPlayerMove(player, playerTeam);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTryEnterDoor(PlayerMoveEvent event) {
        if (!gameManager.isState(GameState.STARTED)) return;
        if (!hasMoved(event)) return;

        Player player = event.getPlayer();
        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);

        // Pour chaque porte de chaque équipe
        for (Map.Entry<Team, TeamDoor> entry : doorManager.getDoors().entrySet()) {
            Team team = entry.getKey();
            TeamDoor door = entry.getValue();

            if (!door.isOpen()) continue;

            // Vérifie si la destination du joueur est dans la zone de la porte
            assert event.getTo() != null;
            if (!isInDoorZone(event.getTo(), door)) continue;

            // Si le joueur appartient à l'équipe, il peut passer
            if (team.equals(playerTeam)) continue;

            // Sinon on annule le mouvement
            event.setCancelled(true);
            return;
        }
    }

    private boolean isInDoorZone(Location to, TeamDoor door) {
        // Vérifie les blocs aux pieds ET à la tête du joueur
        Block feet = to.getBlock();
        Block head = to.clone().add(0, 1, 0).getBlock();
        return door.contains(feet) || door.contains(head);
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}