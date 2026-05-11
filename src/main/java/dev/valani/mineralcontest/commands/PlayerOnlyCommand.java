package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public abstract class PlayerOnlyCommand implements CommandExecutor {

    protected final Main plugin;

    public PlayerOnlyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getString("messages.commands.player_only"));
            return false;
        }
        return onPlayerCommand(player, cmd, label, args);
    }

    protected abstract boolean onPlayerCommand(Player player, Command cmd, String label, String[] args);

}
