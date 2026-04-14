package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMineralContest implements CommandExecutor {

    private final Main plugin;
    private final GameManager gameManager;

    public CommandMineralContest(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if(args.length < 1) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: §e\n- kit \n- team");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start" -> {
                gameManager.start();
                player.sendMessage("§aGame started.");
            }
            case "stop" -> {
                gameManager.stop();
                player.sendMessage("§aGame stopped.");
            }
            default -> {
                player.sendMessage(plugin.getString("plugin.invalid_command"));
            }
        }

        return true;
    }
}
