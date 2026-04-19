package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandAdmin implements CommandExecutor {

    private final Main plugin;
    private final GameManager gameManager;

    public CommandAdmin(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 3) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: §e\n- chest \n- arena");
            return false;
        }

        List<String> subCommands = new ArrayList<>(Arrays.asList("chest", "arena"));
        List<String> subCommandArgs = new ArrayList<>(Arrays.asList("set", "remove", "view"));
        List<Team> teams = gameManager.getTeamManager().getTeams();

        if (!subCommands.contains(args[0].toLowerCase())) {
            player.sendMessage(plugin.getString("plugin.no_such_args"));
            return false;
        }
        if (!subCommandArgs.contains(args[1].toLowerCase())) {
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
                        player.sendMessage("Set chest for team");
                    }
                    case "remove" -> {
                        player.sendMessage("Remove chest for team");
                    }
                    case "view" -> {
                        player.sendMessage("View chest for team");
                    }
                }
            }
            case "arena" -> {
                switch (args[1].toLowerCase()) {
                    case "set" -> {
                        Block targetBlock = player.getTargetBlockExact(6);
                        if (targetBlock == null) {
                            player.sendMessage("§cAucun bloc valide trouvé");
                            return false;
                        }
                        Location targetLocation = targetBlock.getLocation();
                        player.sendMessage("Set arena for team " + team.getName() + " at " + targetLocation.getBlockX() + ", " + targetLocation.getBlockY() + ", " + targetLocation.getBlockZ());
                        gameManager.getTeamManager().setTeamChest(targetLocation, team);
                    }
                    case "remove" -> {
                        player.sendMessage("Remove arena for team");
                    }
                    case "view" -> {
                        player.sendMessage("View arena for team");
                    }
                }
            }
        }


        return true;
    }
}
