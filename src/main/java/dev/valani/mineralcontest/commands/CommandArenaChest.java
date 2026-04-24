package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.ArenaManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArenaChest implements CommandExecutor {
    private final Main plugin;
    private final ArenaManager arenaManager;

    public CommandArenaChest(Main plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getGameManager().getArenaManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: §e\n- set \n- remove \n- view");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "set" -> {
                arenaManager.setArenaChest(player);
                player.sendMessage(plugin.getConfigManager().getString("arena.chest_placed"));
            }
            case "remove" -> {
                if (arenaManager.getChestLocation() == null) {
                    player.sendMessage(plugin.getConfigManager().getString("arena.chest_not_placed"));
                    break;
                }
                arenaManager.removeArenaChest();
                player.sendMessage(plugin.getConfigManager().getString("arena.chest_removed"));
            }
            case "view" -> {
                if (arenaManager.getChestLocation() == null) {
                    player.sendMessage(plugin.getConfigManager().getString("arena.chest_not_placed"));
                    break;
                }
                player.sendMessage(plugin.getConfigManager().getString("arena.chest_location").replace("{LOCATION}", Utils.formatLocation(arenaManager.getChestLocation())));
            }

            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                return false;
            }
        }

        return true;
    }

}
