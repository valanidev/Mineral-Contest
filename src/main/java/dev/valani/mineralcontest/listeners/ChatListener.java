package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener {

    private final TeamManager teamManager;

    public ChatListener(Main plugin) {
        this.teamManager = plugin.getGameManager().getTeamManager();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<Team> teamOpt = teamManager.getPlayerTeam(player);
        String format;

        if (teamOpt.isEmpty()) {
            format = ChatColor.DARK_GRAY + "STF " + ChatColor.RESET + player.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + "%2$s";
        } else {
            Team team = teamOpt.get();
            format = team.getDisplayName() + " " + player.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + "%2$s";
        }

        event.setFormat(format);
    }

}
