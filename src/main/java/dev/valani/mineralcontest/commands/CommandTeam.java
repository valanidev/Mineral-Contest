package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandTeam extends PlayerOnlyCommand {

    private final GameManager gameManager;
    private final TeamSelectorMenu teamSelectorMenu;

    public CommandTeam(Main plugin, GameManager gameManager) {
        super(plugin);
        this.gameManager = gameManager;
        this.teamSelectorMenu = gameManager.getTeamSelectorMenu();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (!gameManager.isWaiting()) {
            player.sendMessage(plugin.getConfigManager().getString("messages.game.already_started"));
            return false;
        }

        teamSelectorMenu.open(player);
        return true;
    }
}
