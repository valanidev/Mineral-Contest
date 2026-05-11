package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamLocationManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

public class DoorListener implements Listener {

    private final GameManager         gameManager;
    private final TeamManager         teamManager;
    private final TeamLocationManager locationManager;

    private static final double OPEN_DISTANCE  = 3.5;
    private static final double CLOSE_DISTANCE = 3.5;

    public DoorListener(Main plugin) {
        this.gameManager     = plugin.getGameManager();
        this.teamManager     = gameManager.getTeamManager();
        this.locationManager = teamManager.getTeamLocationManager();
    }

    // --- Ouverture / fermeture ---

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!hasMovedBlock(event)) return;

        Player player = event.getPlayer();
        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);

        for (Map.Entry<Team, TeamDoor> entry : locationManager.getDoors().entrySet()) {
            Team team   = entry.getKey();
            TeamDoor door = entry.getValue();
            Location center = door.getCenter();

            if (!isWorldValid(center, player)) continue;

            double distance = player.getLocation().distance(center);

            // Ouverture / fermeture
            if (team.equals(playerTeam) && distance <= OPEN_DISTANCE) {
                door.open();
            } else if (door.isOpen() && !anyMemberNearby(team, door)) {
                door.close();
            }

            if (door.isOpen() && !team.equals(playerTeam) && isInDoorZone(event.getTo(), door)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    // =========================================================================
    // Utilitaires
    // =========================================================================

    private boolean anyMemberNearby(Team team, TeamDoor door) {
        Location center = door.getCenter();
        if (!center.isWorldLoaded()) return false;

        return team.getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .filter(p -> p.getWorld().equals(center.getWorld()))
                .anyMatch(p -> p.getLocation().distance(center) <= CLOSE_DISTANCE);
    }

    private boolean isInDoorZone(Location to, TeamDoor door) {
        Block feet = to.getBlock();
        Block head = to.clone().add(0, 1, 0).getBlock();
        return door.contains(feet) || door.contains(head);
    }

    private boolean isWorldValid(Location center, Player player) {
        return center.getWorld() != null
                && center.isWorldLoaded()
                && player.getWorld().equals(center.getWorld());
    }

    private boolean hasMovedBlock(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}