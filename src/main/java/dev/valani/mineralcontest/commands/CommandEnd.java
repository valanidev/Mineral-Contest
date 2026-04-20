package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandEnd implements CommandExecutor {
    private final Main plugin;
    private final GameManager gameManager;

    public CommandEnd(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!gameManager.isState(GameState.STARTED)) {
            sender.sendMessage("§cLa partie n'est pas en cours.");
            return false;
        }

        gameManager.end();
        return true;
    }
}
