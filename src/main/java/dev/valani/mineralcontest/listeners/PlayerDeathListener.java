package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    private final Main plugin;
    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final KitManager kitManager;
    private final List<Material> allowedDrops = List.of(Material.DIAMOND, Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD);

    public PlayerDeathListener(Main plugin, GameManager gameManager, TeamManager teamManager, KitManager kitManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.teamManager = teamManager;
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        List<ItemStack> items = event.getDrops();
        items.removeIf(item -> !allowedDrops.contains(item.getType()));
        player.sendMessage("§cVous êtes mort et avez perdu vos minerais...");

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
        if (!gameManager.isState(GameState.STARTED)) return;
        Player player = event.getPlayer();
        KitBase kit = kitManager.getKit(player);
        player.sendMessage("t'es mort big noob");
        player.sendMessage(kit.toString());
        if (kit == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> kit.apply(player), 1L);
    }

}
