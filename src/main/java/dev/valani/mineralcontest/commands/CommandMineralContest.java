package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMineralContest implements CommandExecutor {

    Main plugin = Main.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }

        if(args.length < 1) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            player.sendMessage("§cTu peux utiliser les commandes suivantes:§e \n- kit \n- team");
            return false;
        }

        player.sendMessage("all good");

        return true;
    }
}
