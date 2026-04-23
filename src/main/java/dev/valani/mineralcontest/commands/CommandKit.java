package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKit implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private final GameManager gameManager;
    private final KitSelectorMenu kitSelectorMenu;

    public CommandKit(KitSelectorMenu kitSelectorMenu) {
        this.kitSelectorMenu = kitSelectorMenu;
        this.gameManager = plugin.getGameManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getString("plugin.only_player_command"));
            return false;
        }
        if (!gameManager.isState(GameState.KIT_SELECTION)) {
            player.sendMessage("§cLe kit peut être choisi uniquement pendant la phase de sélection des kits.");
            return false;
        }
        Team playerTeam = gameManager.getTeamManager().getPlayerTeam(player).orElse(null);
        if (playerTeam == null) {
            player.sendMessage("§cVous devez d'abord choisir une team.");
            return false;
        }

        kitSelectorMenu.open(player);
        return true;
    }
}
