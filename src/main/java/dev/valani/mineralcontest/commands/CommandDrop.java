package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class CommandDrop implements CommandExecutor {

    private final Main plugin;

    public CommandDrop(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        String action = args.length == 0 ? "force" : args[0];

        if (action.equals("force")) {
            plugin.getGameManager().getTimeManager().spawnDrop();
        }

        return true;
    }
}
