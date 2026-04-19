package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamSelectorMenu {

    private static final String TITLE = "§8Choisir une équipe";
    private final TeamManager teamManager;

    public TeamSelectorMenu(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void open(Player player) {
        List<Team> teams = teamManager.getTeams();
        int size = Utils.roundToMultipleOf9(teams.size());
        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        for (int i = 0; i < teams.size(); i++) {
            inv.setItem(i, buildTeamItem(teams.get(i), player));
        }

        player.openInventory(inv);
    }

    private ItemStack buildTeamItem(Team team, Player player) {
        ItemStack item = new ItemStack(team.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<String> lore = new ArrayList<>();
        lore.add("§7Joueurs : §f" + team.size() + "§7/§f" + team.getMaxPlayers());
        lore.add("");
        List<UUID> members = team.getMembers();
        if (!members.isEmpty()) {
            lore.add("§7Membres :");
            for (UUID uuid : members) {
                Player p = Bukkit.getPlayer(uuid);
                if (p == null) continue;
                String name = p.getName();
                lore.add(" §f" + name);
            }
            lore.add("");
        }
        lore.add(team.isFull() ? "§cÉquipe pleine" :
                team.hasMember(player) ? "§a§lÉquipe actuelle" :
                "§6Clique pour rejoindre");

        meta.setDisplayName(team.getDisplayName());
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public String getTitle() {
        return TITLE;
    }
}