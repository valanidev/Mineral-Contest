package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class CommandChat implements CommandExecutor {
    private final Main plugin;
    private final TeamManager teamManager;

    public CommandChat(Main plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getString("plugin.not_enough_args"));
            return false;
        }

        Optional<Team> playerTeam = teamManager.getPlayerTeam(player);
        if (playerTeam.isEmpty()) {
            player.sendMessage("§cTu ne fais partie d'aucune team.");
            return false;
        }

        Team team = playerTeam.get();

        for (UUID uuid : team.getMembers()) {
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) continue;
            target.sendMessage("§7[" + team.getDisplayName() + "§7] §r" + player.getName() + "§7: " + team.getColor() + String.join(" ", args));
        }

        return true;
    }
}
