package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListener implements Listener {

    private final GameManager gameManager;

    public ChatListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<Team> teamOpt = gameManager.getPlayerTeam(player);

        if (teamOpt.isEmpty()) return;

        Team team = teamOpt.get();
        String format = team.getColor() + team.getName() + " " + player.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + "%2$s";
        event.setFormat(format);
    }
}
