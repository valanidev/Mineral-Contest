package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.ParseUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamManager {

    private final Main plugin;
    private final ConfigManager configManager;
    private final List<Team> teams;

    private final TeamLocationManager teamLocationManager;

    public TeamManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.teams = loadFromConfig();
        this.teamLocationManager = new TeamLocationManager(plugin, teams);
    }

    // --- Loading ---

    private List<Team> loadFromConfig() {
        List<Team> result = new ArrayList<>();

        int maxPlayers = configManager.getInt("settings.teams.max_players");
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("settings.teams.list");
        if (section == null) return result;

        for (String key : section.getKeys(false)) {
            String path = "settings.teams.list." + key;
            String name = configManager.getString(path + ".name", key);
            ChatColor color = ParseUtil.parseChatColor(configManager.getString(path + ".color", "WHITE"));
            Material mat = ParseUtil.parseMaterial(configManager.getString(path + ".icon", "WHITE_WOOL"));
            result.add(new Team(name, color, mat, maxPlayers));
        }

        return result;
    }

    // --- Locations ---

    public Location getTeamChestLocation(Team team) {
        return teamLocationManager.get(team, TeamLocationManager.LocationType.CHEST);
    }

    public Location getTeamSpawnLocation(Team team) {
        return teamLocationManager.get(team, TeamLocationManager.LocationType.SPAWN);
    }

    public Location getTeamArenaLocation(Team team) {
        return teamLocationManager.get(team, TeamLocationManager.LocationType.ARENA);
    }

    public void setTeamChest(Location loc, Team team) {
        teamLocationManager.set(team, TeamLocationManager.LocationType.CHEST, loc);
    }

    public void setTeamSpawn(Location loc, Team team) {
        teamLocationManager.set(team, TeamLocationManager.LocationType.SPAWN, loc);
    }

    public void setTeamArena(Location loc, Team team) {
        teamLocationManager.set(team, TeamLocationManager.LocationType.ARENA, loc);
    }

    public void removeTeamChest(Team team) {
        teamLocationManager.remove(team, TeamLocationManager.LocationType.CHEST);
    }

    public void removeTeamSpawn(Team team) {
        teamLocationManager.remove(team, TeamLocationManager.LocationType.SPAWN);
    }

    public void removeTeamArena(Team team) {
        teamLocationManager.remove(team, TeamLocationManager.LocationType.ARENA);
    }

    // ---

    public void addPlayerToTeam(Player player, Team team) {
        if (team.hasMember(player) || team.isFull()) return;
        getPlayerTeam(player).ifPresent(t -> t.removeMember(player));
        String newName = team.getColor() + player.getName();
        player.setDisplayName(newName);
        boolean showTeamInTab = configManager.isShowNameInTab();
        player.setPlayerListName(showTeamInTab ? team.getDisplayName() + " " + newName : newName);
        team.addMember(player);
    }

    public void clearAll() {
        teams.forEach(Team::clear);
    }

    // --- Getters ---

    public Optional<Team> getPlayerTeam(Player player) {
        return teams.stream().filter(t -> t.hasMember(player)).findFirst();
    }

    public List<Team> getTeams() {
        return teams;
    }

    public TeamLocationManager getTeamLocationManager() {
        return teamLocationManager;
    }

    public List<Team> getOtherTeams(Team excluded) {
        return teams.stream()
                .filter(team -> team != excluded)
                .filter(team -> !team.getMembers().isEmpty())
                .toList();
    }

}
