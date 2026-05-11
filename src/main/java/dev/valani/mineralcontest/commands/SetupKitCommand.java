package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SetupKitCommand implements CommandExecutor, TabCompleter {

    private final KitManager kitManager;

    public SetupKitCommand(Main plugin) {
        this.kitManager = plugin.getGameManager().getKitManager();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /" + label + " <player> <kit>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return false;
        }

        String kitName = args[1];
        KitBase kit = kitManager.getKits().stream()
                .filter(k -> k.getDisplayName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);

        if (kit == null) {
            sender.sendMessage("§cKit introuvable.");
            return false;
        }

        kitManager.assignKit(target, kit);

        sender.sendMessage("§aKit §2" + kit.getDisplayName() + " §aattribué à §2" + target.getName() + "§a.");
        target.sendMessage("§aVotre kit a été changé en §2" + kit.getDisplayName() + "§a.");

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            return kitManager.getKits().stream()
                    .map(KitBase::getDisplayName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}