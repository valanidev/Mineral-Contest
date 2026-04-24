package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandChat extends PlayerOnlyCommand {
    private final TeamManager teamManager;

    public CommandChat(Main plugin, GameManager gameManager) {
        super(plugin);
        this.teamManager = gameManager.getTeamManager();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§cUsage: /" + label + " <message>");
            return false;
        }

        Team team = teamManager.getTeam(player);
        if (team == null) {
            player.sendMessage("§cTu ne fais partie d'aucune team.");
            return false;
        }

        for (UUID uuid : team.getMembers()) {
            Player target = Bukkit.getPlayer(uuid);
            if (target == null) continue;
            target.sendMessage("§6§lTEAM §r" + player.getDisplayName() + " §7→ " + team.getColor() + String.join(" ", args));
        }

        return true;
    }
}
