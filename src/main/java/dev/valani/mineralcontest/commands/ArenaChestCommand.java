package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.ArenaManager;
import dev.valani.mineralcontest.utils.FormatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

public class ArenaChestCommand extends PlayerOnlyCommand implements TabCompleter {

    private final ArenaManager arenaManager;

    public ArenaChestCommand(Main plugin) {
        super(plugin);
        this.arenaManager = plugin.getGameManager().getArenaManager();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getString("plugin.not_enough_args"));
            player.sendMessage("§cUsage: /" + label + " <set|remove|get|force>");
            return false;
        }

        switch (args[0]) {
            case "set" -> {
                arenaManager.setArenaChest(player.getLocation());
                player.sendMessage("§aLe coffre d'arène a été défini avec succès!");
                return true;
            }
            case "get" -> {
                player.sendMessage("§aLe coffre d'arène est placé en " + FormatUtil.formatLocationShort(arenaManager.getChestLocation()));
                return true;
            }
            case "remove" -> {
                arenaManager.removeArenaChest();
                player.sendMessage("§aLe coffre d'arène a été supprimé avec succès!");
                return true;
            }
            case "force" -> {
                arenaManager.makeAvailable();
                player.sendMessage("§aLe coffre d'arène a été forcé avec succès!");
                return true;
            }
            default -> {
                player.sendMessage("§cUsage: /" + label + " <set|remove|get>");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String @NonNull [] args) {
        if (args.length == 1) {
            return Stream.of("set", "get", "remove", "force")
                    .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
