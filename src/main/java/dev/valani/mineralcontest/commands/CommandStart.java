package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {
    private final Main plugin;
    private final GameManager gameManager;

    public CommandStart(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!gameManager.isState(GameState.WAITING)) {
            sender.sendMessage(plugin.getString("plugin.game_already_started"));
            return false;
        }

        gameManager.start();
        Bukkit.broadcastMessage(plugin.getString("game.started"));
        return true;
    }
}
