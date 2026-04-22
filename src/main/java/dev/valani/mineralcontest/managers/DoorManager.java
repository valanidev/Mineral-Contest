package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.DoorOrientation;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class DoorManager {

    private final FileManager teamFile;
    private final Map<Team, TeamDoor> doors = new HashMap<>();

    public DoorManager(GameManager gameManager) {
        this.teamFile = gameManager.getTeamManager().getTeamFile();
        loadDoors(gameManager.getTeamManager().getTeams());
    }

    private Material getConcrete(Team team) {
        return switch (team.getColor()) {
            case RED -> Material.RED_CONCRETE;
            case BLUE -> Material.BLUE_CONCRETE;
            case GREEN -> Material.GREEN_CONCRETE;
            case YELLOW -> Material.YELLOW_CONCRETE;
            case AQUA -> Material.CYAN_CONCRETE;
            case LIGHT_PURPLE -> Material.PINK_CONCRETE;
            case WHITE -> Material.WHITE_CONCRETE;
            case BLACK -> Material.BLACK_CONCRETE;
            default -> Material.WHITE_CONCRETE;
        };
    }

    private Material getGlass(Team team) {
        return switch (team.getColor()) {
            case RED -> Material.RED_STAINED_GLASS;
            case BLUE -> Material.BLUE_STAINED_GLASS;
            case GREEN -> Material.GREEN_STAINED_GLASS;
            case YELLOW -> Material.YELLOW_STAINED_GLASS;
            case AQUA -> Material.CYAN_STAINED_GLASS;
            case LIGHT_PURPLE -> Material.PINK_STAINED_GLASS;
            case WHITE -> Material.WHITE_STAINED_GLASS;
            case BLACK -> Material.BLACK_STAINED_GLASS;
            default -> Material.WHITE_STAINED_GLASS;
        };
    }

    public void setDoor(Team team, Location center, DoorOrientation orientation) {
        removeDoor(team);
        TeamDoor door = new TeamDoor(
                center,
                orientation,
                getConcrete(team),
                getGlass(team)
        );
        door.place();
        doors.put(team, door);
        saveDoor(team, center, orientation);
    }

    public void removeDoor(Team team) {
        TeamDoor door = doors.remove(team);
        if (door != null) {
            door.close();
            deleteDoorFromFile(team);
        }
    }

    public Optional<TeamDoor> getDoor(Team team) {
        return Optional.ofNullable(doors.get(team));
    }

    public Map<Team, TeamDoor> getDoors() {
        return Collections.unmodifiableMap(doors);
    }

    public void onPlayerMove(Player player, Team playerTeam) {
        doors.forEach((team, door) -> {
            double distance = player.getLocation().distance(door.getCenter());

            if (team.equals(playerTeam) && distance <= 3.5) {
                door.open();
            } else if (door.isOpen() && !anyTeamMemberNearby(team, door)) {
                door.close();
            }
        });
    }

    private boolean anyTeamMemberNearby(Team team, TeamDoor door) {
        return team.getMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .anyMatch(p -> p.getLocation().distance(door.getCenter()) <= 2.0);
    }

    private void saveDoor(Team team, Location center, DoorOrientation orientation) {
        String path = "team.door." + team.getName();
        teamFile.getConfig().set(path + ".world", center.getWorld().getName());
        teamFile.getConfig().set(path + ".x", center.getBlockX());
        teamFile.getConfig().set(path + ".y", center.getBlockY());
        teamFile.getConfig().set(path + ".z", center.getBlockZ());
        teamFile.getConfig().set(path + ".orientation", orientation.name());
        teamFile.save();
    }

    private void deleteDoorFromFile(Team team) {
        teamFile.getConfig().set("doors." + team.getName(), null);
        teamFile.save();
    }

    public void loadDoors(List<Team> teams) {
        for (Team team : teams) {
            String path = "team.door." + team.getName();
            if (!teamFile.getConfig().contains(path)) continue;

            String worldName = teamFile.getConfig().getString(path + ".world");
            int x = teamFile.getConfig().getInt(path + ".x");
            int y = teamFile.getConfig().getInt(path + ".y");
            int z = teamFile.getConfig().getInt(path + ".z");
            String orientation = teamFile.getConfig().getString(path + ".orientation", "NORTH_SOUTH");

            Location center = new Location(Bukkit.getWorld(worldName), x, y, z);
            setDoor(team, center, DoorOrientation.valueOf(orientation));
        }
    }
}