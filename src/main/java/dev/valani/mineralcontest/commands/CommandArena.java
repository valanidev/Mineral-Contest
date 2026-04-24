package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CommandArena implements CommandExecutor {

    private final Main plugin;
    private final GameManager gameManager;

    private final Set<UUID> teleporting;

    public CommandArena(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.teleporting = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getString("plugin.only_player_command"));
            return false;
        }

        if (!gameManager.isState(GameState.STARTED)) {
            sender.sendMessage("§cLa partie n'est pas en cours.");
            return false;
        }

        Team team = gameManager.getTeamManager().getPlayerTeam(player).orElse(null);
        if (team == null) {
            sender.sendMessage("§cVous n'êtes dans aucune équipe.");
            return false;
        }

        Location arenaLoc = gameManager.getTeamManager().getTeamArenaLocation(team);
        if (arenaLoc == null) {
            sender.sendMessage("§cL'arène de votre équipe n'a pas été configurée.");
            return false;
        }

        if (!gameManager.getArenaManager().isChestAvailable()) {
            player.sendMessage("§cTu peux te téléporter uniquement quand le coffre est disponible.");
            return false;
        }

        if (teleporting.contains(player.getUniqueId())) {
            player.sendMessage("§cTéléportation déjà en cours !");
            return true;
        }
        teleporting.add(player.getUniqueId());

        Location startLoc = player.getLocation().clone();
        new BukkitRunnable() {
            int timer = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    teleporting.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (player.getLocation().distance(startLoc) > 0.2) {
                    player.sendMessage("§cTéléportation annulée (tu as bougé)");
                    teleporting.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

                if (timer <= 0) {
                    player.teleport(arenaLoc.clone().add(0.5, 0, 0.5));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    teleporting.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                player.sendMessage("§7Téléportation dans §e" + timer + " secondes§7...");
                timer--;
            }

        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }
}
