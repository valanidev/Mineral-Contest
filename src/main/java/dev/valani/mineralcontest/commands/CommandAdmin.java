package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandAdmin implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final GameManager gameManager;

    private final Map<String, List<String>> commandStructure = Map.of(
            "chest", List.of("set", "remove", "get"),
            "arena", List.of("set", "remove", "get", "tp"),
            "score", List.of("set", "get")
    );
    List<String> teamNames;
    List<Team> teams;

    public CommandAdmin(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        teams = gameManager.getTeamManager().getTeams();
        teamNames = teams.stream().map(Team::getName).toList();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        if (args.length == 1) {
            return commandStructure.keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            List<String> actions = commandStructure.get(args[0].toLowerCase());
            if (actions == null) return List.of();

            return actions.stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3) {
            return teamNames.stream()
                    .filter(t -> t.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("score") && args[1].equalsIgnoreCase("set")) {
            return List.of("<valeur>");
        }

        return List.of();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 3) {
            player.sendMessage("§cUsage: /" + label + " <chest|arena> <set|remove|get> <team>");
            return false;
        }

        if (!commandStructure.containsKey(args[0].toLowerCase())) {
            player.sendMessage(plugin.getString("plugin.no_such_args"));
            return false;
        }
        if (!commandStructure.get(args[0].toLowerCase()).contains(args[1].toLowerCase())) {
            player.sendMessage(plugin.getString("plugin.no_such_args"));
            return false;
        }

        Team team = teams.stream().filter(t -> t.getName().equalsIgnoreCase(args[2])).findFirst().orElse(null);

        if (team == null) {
            player.sendMessage(plugin.getString("plugin.no_such_team"));
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "chest" -> {
                switch (args[1].toLowerCase()) {
                    case "set" -> {
                        Block targetBlock = player.getTargetBlockExact(6);
                        if (targetBlock == null) {
                            player.sendMessage("§cAucun bloc valide trouvé");
                            return false;
                        }
                        Location targetLocation = targetBlock.getLocation();
                        player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aa été placé en §e" + Utils.formatLocation(targetLocation) + "§a.");
                        gameManager.getTeamManager().setTeamChest(targetLocation, team);
                    }
                    case "remove" -> {
                        Location chestLocation = gameManager.getTeamManager().getTeamChestLocation(team);
                        if (chestLocation == null) {
                            player.sendMessage("§cLe coffre de l'équipe " + team.getDisplayName() + " §cn'est pas défini.");
                            return false;
                        }
                        player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aa été retiré.");
                        gameManager.getTeamManager().removeTeamChest(team);
                    }
                    case "get" -> {
                        Location chestLocation = gameManager.getTeamManager().getTeamChestLocation(team);
                        if (chestLocation == null)
                            player.sendMessage("§cLe coffre de l'équipe " + team.getDisplayName() + " §cn'est pas défini.");
                        else
                            player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aest situé en §e" + Utils.formatLocation(chestLocation) + "§a.");
                    }
                }
            }
            case "arena" -> {
                switch (args[1].toLowerCase()) {
                    case "set" -> {
                        Location targetLocation = player.getLocation();
                        player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aa été placé en §e" + Utils.formatLocation(targetLocation) + "§a.");
                        gameManager.getTeamManager().setTeamArena(targetLocation, team);
                    }
                    case "remove" -> {
                        Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
                        if (arenaLocation == null) {
                            player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
                            return false;
                        }
                        player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aa été retirée.");
                        gameManager.getTeamManager().removeTeamArena(team);
                    }
                    case "get" -> {
                        Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
                        if (arenaLocation == null)
                            player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
                        else
                            player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aest située en §e" + Utils.formatLocation(arenaLocation) + "§a.");
                    }
                    case "tp" -> {
                        Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
                        if (arenaLocation == null)
                            player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
                        else {
                            player.teleport(arenaLocation);
                            player.sendMessage("§aVous avez été téléporté vers le point d'arène de l'équipe " + team.getDisplayName() + "§a.");
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        }
                    }
                }
            }
            case "score" -> {
                switch (args[1].toLowerCase()) {
                    case "get" -> {
                        int score = team.getScore();
                        player.sendMessage("§aScore de l'équipe " + team.getDisplayName() + " §a: §e" + score);
                    }
                    case "set" -> {
                        if (args.length < 4) {
                            player.sendMessage("§cUsage: /" + label + " score set <team> <valeur>");
                            return false;
                        }

                        int value;
                        try {
                            value = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cValeur invalide.");
                            return false;
                        }

                        team.setScore(value);
                        player.sendMessage("§aScore de l'équipe " + team.getDisplayName() + " §adéfini à §e" + value + "§a.");
                    }
                }
            }
        }


        return true;
    }
}
