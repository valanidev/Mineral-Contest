package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.DoorOrientation;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamLocationManager {

    public enum LocationType { CHEST, SPAWN, ARENA }

    private final FileManager file;

    private final Map<Team, Location> chests = new HashMap<>();
    private final Map<Team, Location> spawns = new HashMap<>();
    private final Map<Team, Location> arenas = new HashMap<>();
    private final Map<Team, TeamDoor> doors  = new HashMap<>();

    public TeamLocationManager(Main plugin, List<Team> teams) {
        this.file = new FileManager(plugin, "team_locations.yml");
        load(teams);
        loadDoors(teams);
    }

    // =========================================================================
    // Locations simples (CHEST / SPAWN / ARENA)
    // =========================================================================

    public Location get(Team team, LocationType type) {
        return mapFor(type).get(team);
    }

    public void set(Team team, LocationType type, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        mapFor(type).put(team, loc);
        saveLocation(team, type, loc);
    }

    public void remove(Team team, LocationType type) {
        mapFor(type).remove(team);
        file.getConfig().set(keyFor(type, team), null);
        file.save();
    }

    public boolean has(Team team, LocationType type) {
        return mapFor(type).containsKey(team);
    }

    // =========================================================================
    // Portes
    // =========================================================================

    public void setDoor(Team team, Location center, DoorOrientation orientation) {
        removeDoor(team);

        TeamDoor door = new TeamDoor(center, orientation, team.getConcrete(), team.getGlass());
        door.place();
        doors.put(team, door);
        saveDoor(team, center, orientation);
    }

    public void removeDoor(Team team) {
        TeamDoor existing = doors.remove(team);
        if (existing != null) existing.close();
        file.getConfig().set("door." + team.getName(), null);
        file.save();
    }

    public Optional<TeamDoor> getDoor(Team team) {
        return Optional.ofNullable(doors.get(team));
    }

    public Map<Team, TeamDoor> getDoors() {
        return java.util.Collections.unmodifiableMap(doors);
    }

    public boolean hasDoor(Team team) {
        return doors.containsKey(team);
    }

    // =========================================================================
    // Chargement
    // =========================================================================

    private void load(List<Team> teams) {
        for (LocationType type : LocationType.values()) {
            for (Team team : teams) {
                String path = keyFor(type, team);
                if (!file.getConfig().contains(path)) continue;

                String worldName = file.getConfig().getString(path + ".world");
                World world = Bukkit.getWorld(worldName != null ? worldName : "");
                if (world == null) {
                    LogUtil.error("Monde introuvable pour " + type + " de " + team.getName() + " : " + worldName);
                    continue;
                }

                double x    = file.getConfig().getDouble(path + ".x");
                double y    = file.getConfig().getDouble(path + ".y");
                double z    = file.getConfig().getDouble(path + ".z");
                float yaw   = (float) file.getConfig().getDouble(path + ".yaw");
                float pitch = (float) file.getConfig().getDouble(path + ".pitch");

                mapFor(type).put(team, new Location(world, x, y, z, yaw, pitch));
            }
        }
    }

    private void loadDoors(List<Team> teams) {
        for (Team team : teams) {
            String path = "door." + team.getName();
            if (!file.getConfig().contains(path)) continue;

            String worldName   = file.getConfig().getString(path + ".world");
            World world        = Bukkit.getWorld(worldName != null ? worldName : "");
            String orientation = file.getConfig().getString(path + ".orientation", "NORTH_SOUTH");

            int x = file.getConfig().getInt(path + ".x");
            int y = file.getConfig().getInt(path + ".y");
            int z = file.getConfig().getInt(path + ".z");

            if (world == null) {
                // Monde pas encore chargé — stocke sans place()
                Location center = new Location(null, x, y, z);
                TeamDoor door = new TeamDoor(center,
                        DoorOrientation.valueOf(orientation),
                        team.getConcrete(), team.getGlass());
                doors.put(team, door);
                continue;
            }

            Location center = new Location(world, x, y, z);
            TeamDoor door = new TeamDoor(center,
                    DoorOrientation.valueOf(orientation),
                    team.getConcrete(), team.getGlass());
            door.place();
            doors.put(team, door);
        }
    }

    // Appelé après génération du monde pour replacer les portes
    public void placeAllDoors(World world) {
        doors.forEach((team, door) -> {
            Location old = door.getCenter();
            Location newCenter = new Location(world, old.getBlockX(), old.getBlockY(), old.getBlockZ());
            TeamDoor newDoor = new TeamDoor(newCenter, door.getOrientation(),
                    team.getConcrete(), team.getGlass());
            newDoor.place();
            doors.put(team, newDoor);
        });
    }

    // =========================================================================
    // Sauvegarde
    // =========================================================================

    private void saveLocation(Team team, LocationType type, Location loc) {
        String path = keyFor(type, team);
        file.getConfig().set(path + ".world", loc.getWorld().getName());
        file.getConfig().set(path + ".x",     loc.getX());
        file.getConfig().set(path + ".y",     loc.getY());
        file.getConfig().set(path + ".z",     loc.getZ());
        file.getConfig().set(path + ".yaw",   loc.getYaw());
        file.getConfig().set(path + ".pitch", loc.getPitch());
        file.save();
    }

    private void saveDoor(Team team, Location center, DoorOrientation orientation) {
        String path = "door." + team.getName();
        file.getConfig().set(path + ".world",       center.getWorld().getName());
        file.getConfig().set(path + ".x",           center.getBlockX());
        file.getConfig().set(path + ".y",           center.getBlockY());
        file.getConfig().set(path + ".z",           center.getBlockZ());
        file.getConfig().set(path + ".orientation", orientation.name());
        file.save();
    }

    // =========================================================================
    // Utilitaires
    // =========================================================================

    private Map<Team, Location> mapFor(LocationType type) {
        return switch (type) {
            case CHEST -> chests;
            case SPAWN -> spawns;
            case ARENA -> arenas;
        };
    }

    private String keyFor(LocationType type, Team team) {
        return type.name().toLowerCase() + "." + team.getName();
    }

    public boolean isTeamChest(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            return false;
        }

        return chests.values().stream()
                .filter(chest -> chest.getWorld() != null)
                .anyMatch(chest ->
                        chest.getWorld().equals(loc.getWorld())
                                && chest.getBlockX() == loc.getBlockX()
                                && chest.getBlockY() == loc.getBlockY()
                                && chest.getBlockZ() == loc.getBlockZ()
                );
    }
}