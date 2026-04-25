package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.game.DoorOrientation;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class DoorManager {

    private final FileManager teamFile;
    private final Map<Team, TeamDoor> doors = new HashMap<>();

    public DoorManager(GameManager gameManager) {
        this.teamFile = gameManager.getTeamManager().getTeamFile();
        loadDoors(gameManager.getTeamManager().getTeams());
    }

    public void setDoor(Team team, Location center, DoorOrientation orientation) {
        removeDoor(team);
        TeamDoor door = new TeamDoor(center, orientation, team.getConcrete(), team.getGlass());
        door.place();
        doors.put(team, door);
        saveDoor(team, center, orientation);
    }

    public void removeDoor(Team team) {
        TeamDoor door = doors.remove(team);
        if (door != null) {
            door.close();
            teamFile.getConfig().set("door." + team.getName(), null);
            teamFile.save();
        }
    }

    public void placeAllDoors(World world) {
        doors.forEach((team, door) -> {
            Location old = door.getCenter();
            Location newCenter = new Location(world, old.getBlockX(), old.getBlockY(), old.getBlockZ());
            TeamDoor newDoor = new TeamDoor(newCenter, door.getOrientation(), team.getConcrete(), team.getGlass());
            newDoor.place();
            doors.put(team, newDoor);
        });
    }

    public void onPlayerMove(Player player, Team playerTeam) {
        doors.forEach((team, door) -> {
            Location center = door.getCenter();
            if (center.getWorld() == null || !center.isWorldLoaded()) return;
            if (!player.getWorld().equals(center.getWorld())) return;

            double distance = player.getLocation().distance(center);

            if (team.equals(playerTeam) && distance <= 3.5) {
                door.open();
            } else if (door.isOpen() && !anyMemberNearby(team, door)) {
                door.close();
            }
        });
    }

    private boolean anyMemberNearby(Team team, TeamDoor door) {
        Location center = door.getCenter();
        if (center.getWorld() == null || !center.isWorldLoaded()) return false;

        return team.getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline() && p.getWorld().equals(center.getWorld()))
                .anyMatch(p -> p.getLocation().distance(center) <= 3.5);
    }

    public Optional<TeamDoor> getDoor(Team team) {
        return Optional.ofNullable(doors.get(team));
    }

    public Map<Team, TeamDoor> getDoors() {
        return Collections.unmodifiableMap(doors);
    }

    private void saveDoor(Team team, Location center, DoorOrientation orientation) {
        String path = "door." + team.getName();
        teamFile.getConfig().set(path + ".world", center.getWorld().getName());
        teamFile.getConfig().set(path + ".x", center.getBlockX());
        teamFile.getConfig().set(path + ".y", center.getBlockY());
        teamFile.getConfig().set(path + ".z", center.getBlockZ());
        teamFile.getConfig().set(path + ".orientation", orientation.name());
        teamFile.save();
    }

    private void loadDoors(List<Team> teams) {
        for (Team team : teams) {
            String path = "door." + team.getName();
            if (!teamFile.getConfig().contains(path)) continue;

            String worldName = teamFile.getConfig().getString(path + ".world");
            int x = teamFile.getConfig().getInt(path + ".x");
            int y = teamFile.getConfig().getInt(path + ".y");
            int z = teamFile.getConfig().getInt(path + ".z");
            String orientation = teamFile.getConfig().getString(path + ".orientation", "NORTH_SOUTH");

            // Stocke en mémoire sans appeler place() — monde peut ne pas exister encore
            World world = worldName != null ? Bukkit.getWorld(worldName) : null;
            Location center = new Location(world, x, y, z);
            TeamDoor door = new TeamDoor(center, DoorOrientation.valueOf(orientation),
                    team.getConcrete(), team.getGlass());
            doors.put(team, door);
        }
    }
}