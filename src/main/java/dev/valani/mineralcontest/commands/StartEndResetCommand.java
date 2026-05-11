package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class StartEndResetCommand implements CommandExecutor {

    private final GameManager gameManager;

    public StartEndResetCommand(Main plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        return switch (label.toLowerCase()) {
            case "start" -> {
                if (!gameManager.isWaiting()) {
                    sender.sendMessage("§cLa partie est déjà en cours ou terminée.");
                    yield false;
                }
                gameManager.startKitSelect();
                yield true;
            }
            case "end" -> {
                if (!gameManager.isInGame()) {
                    sender.sendMessage("§cLa partie n'est pas en cours.");
                    yield false;
                }
                gameManager.endGame();
                yield true;
            }
            case "reset" -> {
                gameManager.resetGame();
                yield true;
            }
            default -> false;
        };
    }
}