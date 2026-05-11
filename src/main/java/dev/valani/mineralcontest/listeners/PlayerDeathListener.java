package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.utils.FormatUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerDeathListener implements Listener {

    private final Main plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final KitManager kitManager;
    private final Set<Material> allowedDrops;

    private final Set<Player> respawningPlayers;
    private final int respawn_delay;

    public PlayerDeathListener(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.teamManager = gameManager.getTeamManager();
        this.kitManager = gameManager.getKitManager();
        this.respawningPlayers = new HashSet<>();
        this.allowedDrops = gameManager.getScoreManager().getAllowedDrops();

        this.respawn_delay = plugin.getConfigManager().getInt("settings.respawn_delay");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        List<ItemStack> items = event.getDrops();

        boolean containsDrop = false;
        for(ItemStack item : items) {
            if(item.getType() == Material.AIR) continue;
            if(allowedDrops.contains(item.getType())) {
                containsDrop = true;
                break;
            }
        }
        items.removeIf(item -> !allowedDrops.contains(item.getType()));
        player.sendMessage("§cVous êtes mort" + (containsDrop ? " et avez perdu vos minerais." : "."));

        ChatColor playerColor = teamManager.getPlayerTeam(player).map(Team::getColor).orElse(ChatColor.WHITE);
        if (killer != null) {
            ChatColor killerColor = teamManager.getPlayerTeam(killer).map(Team::getColor).orElse(ChatColor.WHITE);
            event.setDeathMessage(playerColor + player.getName() + " §6a été tué par " + killerColor + killer.getName());
        } else {
            event.setDeathMessage(playerColor + player.getName() + " §6est mort.");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!gameManager.isInGame()) return;

        Player player = event.getPlayer();
        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return;

        Location spawnLoc = teamManager.getTeamSpawnLocation(team);
        if (spawnLoc != null) event.setRespawnLocation(spawnLoc);

        KitBase kit = kitManager.getKit(player);
        if (kit == null) return;

        respawningPlayers.add(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                respawningPlayers.remove(player);
                return;
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(respawn_delay, 0));

            startCountdown(player, respawn_delay);
        }, 1L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!respawningPlayers.contains(player)) return;
        Location to = event.getTo();
        if(to == null) return;

        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            event.setCancelled(true);
        }
    }

    private void startCountdown(Player player, int secondsLeft) {
        if (secondsLeft <= 0) {
            finishRespawn(player);
            return;
        }

        player.sendTitle(
                "§cRespawn dans...",
                "§e" + FormatUtil.formatTimeLong(secondsLeft),
                0, 25, 0
        );

        Bukkit.getScheduler().runTaskLater(plugin,
                () -> {
                    if (!player.isOnline()) {
                        respawningPlayers.remove(player);
                        return;
                    }
                    startCountdown(player, secondsLeft - 1);
                },
                20L
        );
    }

    private void finishRespawn(Player player) {
        respawningPlayers.remove(player);

        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.BLINDNESS);

        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return;

        KitBase kit = kitManager.getKit(player);
        if (kit != null) kit.apply(player, team);

        player.sendTitle("§aPrêt !", "", 0, 20, 10);
    }

}
