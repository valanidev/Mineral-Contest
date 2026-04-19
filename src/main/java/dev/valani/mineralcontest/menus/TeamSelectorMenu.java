package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TeamSelectorMenu {

    private static final String TITLE = "§8Choisir une équipe";
    private final TeamManager teamManager;

    public TeamSelectorMenu(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void open(Player player) {
        List<Team> teams = teamManager.getTeams();
        int size = roundToMultipleOf9(teams.size());
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        for (int i = 0; i < teams.size(); i++) {
            inv.setItem(i, buildTeamItem(teams.get(i), player));
        }

        player.openInventory(inv);
    }

    private ItemStack buildTeamItem(Team team, Player player) {
        ItemStack item = new ItemStack(team.getMaterial());
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(team.getDisplayName());
        meta.setLore(List.of(
                "§7Joueurs : §f" + team.size() + "§7/§f" + team.getMaxPlayers(),
                team.isFull() ? "§cÉquipe pleine" :
                        team.hasMember(player) ? "§aTon équipe actuelle" :
                        "§eClique pour rejoindre"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private int roundToMultipleOf9(int n) {
        return (int) Math.ceil(Math.max(n, 1) / 9.0) * 9;
    }

    public String getTitle() {
        return TITLE;
    }
}