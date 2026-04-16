package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class TeamSelectorListener implements Listener {

    private final GameManager gameManager;
    private final TeamSelectorMenu menu;

    public TeamSelectorListener(GameManager gameManager, TeamSelectorMenu menu) {
        this.gameManager = gameManager;
        this.menu = menu;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(menu.getTitle())) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        List<Team> teams = gameManager.getTeams();
        if (slot >= teams.size()) return;

        Team target = teams.get(slot);
        GameResult result = gameManager.joinTeam(player, target);

        switch (result) {
            case SUCCESS -> {
                player.sendMessage(target.getColor() + "Tu as rejoint l'équipe " + target.getName() + " !");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.setDisplayName(target.getColor() + player.getName());
                player.setPlayerListName(target.getColor() + player.getName());
            }
            case TEAM_FULL -> {
                player.sendMessage("§cCette équipe est pleine !");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
            case GAME_ALREADY_STARTED -> {
                player.sendMessage("§cLa partie a déjà commencé !");
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