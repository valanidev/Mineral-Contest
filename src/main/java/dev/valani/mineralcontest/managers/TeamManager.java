package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.FileManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {
    private final Main plugin;
    private final List<Team> teams;
    private final FileManager teamFile;

    Map<Team, Location> teamChestLocations;
    Map<Team, Location> teamArenaLocations;

    public TeamManager(Main plugin) {
        this.plugin = plugin;
        this.teams = loadTeamsFromConfig();
        this.teamFile = new FileManager(plugin, "team.yml");
        this.teamChestLocations = new HashMap<>();
        this.teamArenaLocations = new HashMap<>();

        loadTeamChests();
        loadTeamArenas();
    }

    private List<Team> loadTeamsFromConfig() {
        List<Team> result = new ArrayList<>();
        int maxPlayers = plugin.getInt("game.max_players_per_team");

        ConfigurationSection teamsSection = plugin.getConfig().getConfigurationSection("game.teams");
        if (teamsSection == null) return result;

        for (String key : teamsSection.getKeys(false)) {
            String path = "game.teams." + key;
            String name = plugin.getConfig().getString(path + ".name", key);
            ChatColor color = Utils.parseChatColor(plugin.getConfig().getString(path + ".color", "WHITE"));
            Material material = Utils.parseMaterial(plugin.getConfig().getString(path + ".icon", "WHITE_WOOL"));
            result.add(new Team(name, color, material, maxPlayers));
        }

        return result;
    }

    private void loadTeamChests() {
        ConfigurationSection section = teamFile.getConfig().getConfigurationSection("team.chest");
        if (section == null) return;

        for (String teamName : section.getKeys(false)) {
            Team team = getTeams().stream()
                    .filter(t -> t.getName().equalsIgnoreCase(teamName))
                    .findFirst()
                    .orElse(null);

            if (team == null) continue;

            String path = "team.chest." + teamName;

            String worldName = teamFile.getConfig().getString(path + ".world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            int x = teamFile.getConfig().getInt(path + ".x");
            int y = teamFile.getConfig().getInt(path + ".y");
            int z = teamFile.getConfig().getInt(path + ".z");

            Location loc = new Location(world, x, y, z);
            teamChestLocations.put(team, loc);
        }
    }

    private void loadTeamArenas() {
        ConfigurationSection section = teamFile.getConfig().getConfigurationSection("team.arena");
        if (section == null) return;

        for (String teamName : section.getKeys(false)) {
            Team team = getTeams().stream()
                    .filter(t -> t.getName().equalsIgnoreCase(teamName))
                    .findFirst()
                    .orElse(null);

            if (team == null) continue;

            String path = "team.arena." + teamName;

            String worldName = teamFile.getConfig().getString(path + ".world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            int x = teamFile.getConfig().getInt(path + ".x");
            int y = teamFile.getConfig().getInt(path + ".y");
            int z = teamFile.getConfig().getInt(path + ".z");
            int yaw = teamFile.getConfig().getInt(path + ".yaw");
            int pitch = teamFile.getConfig().getInt(path + ".pitch");

            Location loc = new Location(world, x, y, z, yaw, pitch);
            teamArenaLocations.put(team, loc);
        }
    }

    public GameResult joinTeam(Player player, Team team) {
        if (team.hasMember(player)) return GameResult.ALREADY_IN_TEAM;
        if (team.isFull()) return GameResult.TEAM_FULL;

        getPlayerTeam(player).ifPresent(t -> t.removeMember(player));

        team.addMember(player);
        return GameResult.SUCCESS;
    }

    public Optional<Team> getPlayerTeam(Player player) {
        return teams.stream()
                .filter(t -> t.hasMember(player))
                .findFirst();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void clearAll() {
        teams.forEach(Team::clear);
    }

    public void setTeamChest(Location loc, Team team) {
        if (loc == null || team == null) return;
        World world = loc.getWorld();
        if (world == null) return;

        String key = "team.chest." + team.getName();
        teamFile.getConfig().set(key + ".world", world.getName());
        teamFile.getConfig().set(key + ".x", loc.getBlockX());
        teamFile.getConfig().set(key + ".y", loc.getBlockY());
        teamFile.getConfig().set(key + ".z", loc.getBlockZ());
        teamFile.save();

        teamChestLocations.put(team, loc);
    }

    public void removeTeamChest(Team team) {
        String key = "team.chest." + team.getName();
        teamFile.getConfig().set(key, null);
        teamFile.save();

        teamChestLocations.remove(team);
    }

    public Location getTeamChestLocation(Team team) {
        if (team == null) return null;
        return teamChestLocations.get(team);
    }

    public boolean isTeamChest(Location loc) {
        return teamChestLocations.containsValue(loc);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("§r§nTeams§7: §r");
        teams.forEach(
                team -> builder
                        .append(team.toString())
                        .append("§r, "));
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }


    public void setTeamArena(Location loc, Team team) {
        if (loc == null || team == null) return;
        World world = loc.getWorld();
        if (world == null) return;

        String key = "team.arena." + team.getName();
        teamFile.getConfig().set(key + ".world", world.getName());
        teamFile.getConfig().set(key + ".x", loc.getBlockX());
        teamFile.getConfig().set(key + ".y", loc.getBlockY());
        teamFile.getConfig().set(key + ".z", loc.getBlockZ());
        teamFile.getConfig().set(key + ".yaw", loc.getYaw());
        teamFile.getConfig().set(key + ".pitch", loc.getPitch());
        teamFile.save();

        teamArenaLocations.put(team, loc);
    }

    public void removeTeamArena(Team team) {
        String key = "team.arena." + team.getName();
        teamFile.getConfig().set(key, null);
        teamFile.save();
        teamArenaLocations.remove(team);
    }

    public Location getTeamArenaLocation(Team team) {
        if (team == null) return null;
        return teamArenaLocations.get(team);
    }
}
