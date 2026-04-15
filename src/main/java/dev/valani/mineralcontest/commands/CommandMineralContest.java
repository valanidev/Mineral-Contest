package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMineralContest implements CommandExecutor {

    private final Main plugin;
    private final GameManager gameManager;

    private final TeamSelectorMenu teamSelectorMenu;
    private final KitSelectorMenu kitSelectorMenu;

    public CommandMineralContest(Main plugin, GameManager gameManager, TeamSelectorMenu teamSelectorMenu, KitSelectorMenu kitSelectorMenu) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamSelectorMenu = teamSelectorMenu;
        this.kitSelectorMenu = kitSelectorMenu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: §e\n- kit \n- team");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "team" -> {
                if (!gameManager.isState(GameState.WAITING)) {
                    player.sendMessage("§cLa partie a déjà commencé.");
                    return false;
                }
                teamSelectorMenu.open(player);
            }
            case "kit" -> {
                if (!gameManager.isState(GameState.WAITING)) {
                    player.sendMessage("§cLa partie a déjà commencé.");
                    return false;
                }
                kitSelectorMenu.open(player);
            }
            case "start" -> {
                gameManager.start();
            }
            case "stop" -> {
                gameManager.end();
            }
            default -> {
                player.sendMessage(plugin.getString("plugin.invalid_command"));
            }
        }

        return true;
    }
}
