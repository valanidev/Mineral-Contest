package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameResult;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private final GameManager gameManager;

    public CommandStart() {
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!gameManager.isState(GameState.WAITING)) {
            sender.sendMessage(plugin.getString("plugin.game_already_started"));
            return false;
        }

        GameResult gr = gameManager.start();
        if (gr == GameResult.SUCCESS) return true;

        switch (gr) {
            case ALREADY_STARTED -> {
                sender.sendMessage("§cLa partie a déjà commencé.");
            }
            case PLAYER_HAS_NO_TEAM -> {
                sender.sendMessage("§cUn ou plusieurs joueurs n'ont pas de team.");
            }
            case PLAYER_HAS_NO_KIT -> {
                sender.sendMessage("§cUn ou plusieurs joueurs n'ont pas de kit.");
            }
        }

        return false;
    }
}
