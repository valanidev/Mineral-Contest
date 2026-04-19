package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamManager {
    private final Main plugin;
    private final List<Team> teams;

    public TeamManager(Main plugin) {
        this.plugin = plugin;
        this.teams = loadTeamsFromConfig();
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
}
