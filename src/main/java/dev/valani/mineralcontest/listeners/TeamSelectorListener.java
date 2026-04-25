package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class TeamSelectorListener implements Listener {

    private final Main plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final TeamSelectorMenu menu;

    public TeamSelectorListener(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamManager = gameManager.getTeamManager();
        this.menu = gameManager.getTeamSelectorMenu();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(menu.getTitle())) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        if (!gameManager.isWaiting()) {
            player.sendMessage(plugin.getConfigManager().getString("messages.game.already_started"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        int slot = event.getSlot();
        List<Team> teams = teamManager.getTeams();
        if (slot >= teams.size()) return;

        Team team = teams.get(slot);
        GameResult result = teamManager.joinTeam(player, team);

        switch (result) {
            case SUCCESS -> {
                Bukkit.broadcastMessage("§6§lTEAM §a" + player.getDisplayName() + " §aa rejoint la team " + team.getDisplayName() + "§a.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
            case TEAM_FULL -> {
                player.sendMessage("§cCette équipe est pleine !");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            case ALREADY_IN_TEAM -> {
                player.sendMessage("§cTu fais déjà partie de cette équipe.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }

        menu.open(player);
    }
}