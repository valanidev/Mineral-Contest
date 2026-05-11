package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.SoundUtil;
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

    public TeamSelectorListener(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.teamManager = gameManager.getTeamManager();
        this.menu = gameManager.getTeamSelectorMenu();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(menu.getInventory())) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        if (!gameManager.isWaiting()) {
            player.sendMessage(plugin.getConfigManager().getString("messages.game.already_started"));
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        int slot = event.getRawSlot();
        List<Team> teams = teamManager.getTeams();
        if (slot >= teams.size()) return;

        Team team = teams.get(slot);

        if (team.isFull()) {
            player.sendMessage("§cCette équipe est pleine !");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        if (team.hasMember(player)) {
            player.sendMessage("§cTu fais déjà partie de cette équipe.");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        teamManager.addPlayerToTeam(player, team);
        plugin.getGameManager().getSbManager().refreshTeam(team);

        Bukkit.broadcastMessage("§6§lTEAM §a" + player.getDisplayName() + " §aa rejoint la team " + team.getDisplayName() + "§a.");
        SoundUtil.playForPlayer(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

        menu.open(player);
    }
}