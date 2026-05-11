package dev.valani.mineralcontest.menus;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamSelectorMenu extends MenuBase {

    private static final String TITLE = "§8Choisis une équipe";

    private final TeamManager teamManager;

    public TeamSelectorMenu(TeamManager teamManager) {
        super(TITLE, 1);
        this.teamManager = teamManager;
    }

    @Override
    protected void update(Player player) {
        List<Team> teams = teamManager.getTeams();

        for (int i = 0; i < teams.size(); i++) {
            getInventory().setItem(i, buildTeamItem(teams.get(i), player));
        }
    }

    private ItemStack buildTeamItem(Team team, Player player) {
        List<String> lore = new ArrayList<>();

        lore.add("§7Joueurs : §f" + team.size()
                + "§7/§f" + team.getMaxPlayers());
        lore.add("");

        List<UUID> members = team.getMembers();

        if (!members.isEmpty()) {
            lore.add("§7Membres :");

            for (UUID uuid : members) {
                Player member = Bukkit.getPlayer(uuid);

                if (member != null) {
                    lore.add(" §f• " + member.getName());
                } else {
                    lore.add(" §8• Déconnecté");
                }
            }

            lore.add("");
        }

        if (team.isFull()) {
            lore.add("§cÉquipe pleine");
        } else if (team.hasMember(player)) {
            lore.add("§a§lÉquipe actuelle");
        } else {
            lore.add("§6Clique pour rejoindre");
        }

        return new ItemBuilder(team.getMaterial())
                .setDisplayName(team.getDisplayName())
                .setLore(lore)
                .build();
    }
}
