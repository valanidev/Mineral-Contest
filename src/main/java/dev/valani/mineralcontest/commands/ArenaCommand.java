package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArenaCommand extends PlayerOnlyCommand {

    private final GameManager gameManager;
    private final Set<UUID> teleporting;

    public ArenaCommand(Main plugin) {
        super(plugin);
        this.gameManager = plugin.getGameManager();
        this.teleporting = new HashSet<>();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (!gameManager.isInGame()) {
            player.sendMessage("§cLa partie n'est pas en cours.");
            return false;
        }

        Team team = gameManager.getTeamManager().getPlayerTeam(player).orElse(null);
        if (team == null) {
            player.sendMessage("§cVous n'êtes dans aucune équipe.");
            return false;
        }

        Location arenaLoc = gameManager.getTeamManager().getTeamArenaLocation(team);
        if (arenaLoc == null) {
            player.sendMessage("§cL'arène de votre équipe n'a pas été configurée.");
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

        List<UUID> members = team.getMembers();

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

                SoundUtil.playForPlayer(player, Sound.BLOCK_NOTE_BLOCK_PLING);

                if (timer <= 0) {
                    for (UUID uuid : members) {
                        Player member = Bukkit.getPlayer(uuid);
                        if (member != null) {
                            member.teleport(arenaLoc);
                            SoundUtil.playForPlayer(member, Sound.ENTITY_ENDERMAN_TELEPORT);
                        }
                    }
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
