package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKit implements CommandExecutor {

    private final Main plugin;
    private final GameManager gameManager;
    private final KitSelectorMenu kitSelectorMenu;

    public CommandKit(Main plugin, KitSelectorMenu kitSelectorMenu, GameManager gameManager) {
        this.plugin = plugin;
        this.kitSelectorMenu = kitSelectorMenu;
        this.gameManager = gameManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (!gameManager.isState(GameState.WAITING)) {
            player.sendMessage("§cLa partie a déjà commencé.");
            return false;
        }

        kitSelectorMenu.open(player);
        return true;
    }
}
