package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.FileManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TeamManager {

    private final Main plugin;
    private final List<Team> teams;
    private final FileManager teamFile;
    private final TeamLocationStore locationStore;

    public TeamManager(Main plugin) {
        this.plugin = plugin;
        this.teamFile = new FileManager(plugin, "teams.yml");
        this.teams = loadTeamsFromConfig();
        this.locationStore = new TeamLocationStore(teamFile, teams);
    }

    // --- Config ---

    private List<Team> loadTeamsFromConfig() {
        List<Team> result = new ArrayList<>();
        int maxPlayers = plugin.getConfigManager().getInt("game.max_players_per_team");

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("game.teams");
        if (section == null) return result;

        for (String key : section.getKeys(false)) {
            String path = "game.teams." + key;
            String name = plugin.getConfig().getString(path + ".name", key);
            ChatColor color = Utils.parseChatColor(plugin.getConfig().getString(path + ".color", "WHITE"));
            Material mat = Utils.parseMaterial(plugin.getConfig().getString(path + ".icon", "WHITE_WOOL"));
            result.add(new Team(name, color, mat, maxPlayers));
        }

        return result;
    }

    // --- Membres ---

    public void joinTeam(Player player, Team team) {
        if (team.hasMember(player) || team.isFull()) return;
        getPlayerTeam(player).ifPresent(t -> t.removeMember(player));
        player.setDisplayName(team.getColor() + player.getName());
        player.setPlayerListName(team.getColor() + player.getName());
        team.addMember(player);
    }

    public Optional<Team> getPlayerTeam(Player player) {
        return teams.stream().filter(t -> t.hasMember(player)).findFirst();
    }

    public Team getTeam(Player player) {
        return getPlayerTeam(player).orElse(null);
    }

    public void clearAll() {
        teams.forEach(Team::clear);
    }

    // --- Locations (délégué à TeamLocationStore) ---

    public Location getTeamChestLocation(Team team) {
        return locationStore.get(team, TeamLocationStore.LocationType.CHEST);
    }

    public Location getTeamSpawnLocation(Team team) {
        return locationStore.get(team, TeamLocationStore.LocationType.SPAWN);
    }

    public Location getTeamArenaLocation(Team team) {
        return locationStore.get(team, TeamLocationStore.LocationType.ARENA);
    }

    public void setTeamChest(Location loc, Team team) {
        locationStore.set(team, TeamLocationStore.LocationType.CHEST, loc);
    }

    public void setTeamSpawn(Location loc, Team team) {
        locationStore.set(team, TeamLocationStore.LocationType.SPAWN, loc);
    }

    public void setTeamArena(Location loc, Team team) {
        locationStore.set(team, TeamLocationStore.LocationType.ARENA, loc);
    }

    public void removeTeamChest(Team team) {
        locationStore.remove(team, TeamLocationStore.LocationType.CHEST);
    }

    public void removeTeamSpawn(Team team) {
        locationStore.remove(team, TeamLocationStore.LocationType.SPAWN);
    }

    public void removeTeamArena(Team team) {
        locationStore.remove(team, TeamLocationStore.LocationType.ARENA);
    }

    public boolean isTeamChest(Location loc) {
        return teams.stream()
                .map(team -> locationStore.get(team, TeamLocationStore.LocationType.CHEST))
                .filter(Objects::nonNull)
                .anyMatch(chestLoc ->
                        chestLoc.getBlockX() == loc.getBlockX() &&
                                chestLoc.getBlockY() == loc.getBlockY() &&
                                chestLoc.getBlockZ() == loc.getBlockZ() &&
                                Objects.equals(chestLoc.getWorld(), loc.getWorld())
                );
    }

    // --- Getters ---

    public List<Team> getTeams() {
        return List.copyOf(teams);
    }

    public FileManager getTeamFile() {
        return teamFile;
    }

    @Override
    public String toString() {
        return teams.stream()
                .map(Team::toString)
                .reduce((a, b) -> a + "§r, " + b)
                .map(s -> "§r§nTeams§7: §r" + s)
                .orElse("Aucune équipe");
    }
}