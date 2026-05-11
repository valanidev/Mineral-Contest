package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class TeamCommand extends PlayerOnlyCommand {

    private final GameManager gameManager;

    private final TeamSelectorMenu teamSelectorMenu;

    public TeamCommand(Main plugin) {
        super(plugin);
        this.gameManager = plugin.getGameManager();
        this.teamSelectorMenu = gameManager.getTeamSelectorMenu();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (!gameManager.isWaiting()) {
            player.sendMessage(plugin.getConfigManager().getString("messages.not_allowed_now"));
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return false;
        }

        teamSelectorMenu.open(player);
        return true;
    }


}
