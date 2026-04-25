package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamLocationStore {

    public enum LocationType {CHEST, SPAWN, ARENA}

    private final FileManager file;
    private final Map<Team, Location> chests = new HashMap<>();
    private final Map<Team, Location> spawns = new HashMap<>();
    private final Map<Team, Location> arenas = new HashMap<>();

    public TeamLocationStore(FileManager file, List<Team> teams) {
        this.file = file;
        load(teams);
    }

    // --- Accès ---
    public Location get(Team team, LocationType type) {
        return mapFor(type).get(team);
    }

    public void set(Team team, LocationType type, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        mapFor(type).put(team, loc);
        save(team, type, loc);
    }

    public void remove(Team team, LocationType type) {
        mapFor(type).remove(team);
        file.getConfig().set(keyFor(type, team), null);
        file.save();
    }

    public boolean has(Team team, LocationType type) {
        return mapFor(type).containsKey(team);
    }

    // --- Chargement ---
    private void load(List<Team> teams) {
        for (LocationType type : LocationType.values()) {
            for (Team team : teams) {
                String path = keyFor(type, team);
                if (!file.getConfig().contains(path)) continue;

                String worldName = file.getConfig().getString(path + ".world");
                World world = Bukkit.getWorld(worldName != null ? worldName : "");
                if (world == null) continue;

                double x = file.getConfig().getDouble(path + ".x");
                double y = file.getConfig().getDouble(path + ".y");
                double z = file.getConfig().getDouble(path + ".z");
                float yaw = (float) file.getConfig().getDouble(path + ".yaw");
                float pitch = (float) file.getConfig().getDouble(path + ".pitch");

                mapFor(type).put(team, new Location(world, x, y, z, yaw, pitch));
            }
        }
    }

    // --- Sauvegarde ---
    private void save(Team team, LocationType type, Location loc) {
        String path = keyFor(type, team);
        file.getConfig().set(path + ".world", loc.getWorld().getName());
        file.getConfig().set(path + ".x", loc.getX());
        file.getConfig().set(path + ".y", loc.getY());
        file.getConfig().set(path + ".z", loc.getZ());
        file.getConfig().set(path + ".yaw", loc.getYaw());
        file.getConfig().set(path + ".pitch", loc.getPitch());
        file.save();
    }

    // --- Utilitaires ---
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
}