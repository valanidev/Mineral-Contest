package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class PlayerDeathListener implements Listener {

    private final GameManager gameManager;
    private final List<Material> allowedDrops = List.of(Material.DIAMOND, Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD);

    public PlayerDeathListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        List<ItemStack> items = event.getDrops();
        items.removeIf(item -> !allowedDrops.contains(item.getType()));
        player.sendMessage("§cVous êtes mort et avez perdu vos minerais...");

        ChatColor playerColor = gameManager.getPlayerTeam(player).map(Team::getColor).orElse(ChatColor.WHITE);
        if (killer != null) {
            ChatColor killerColor = gameManager.getPlayerTeam(killer).map(Team::getColor).orElse(ChatColor.WHITE);
            event.setDeathMessage(playerColor + player.getName() + " §6a été tué par " + killerColor + killer.getName());
        } else {
            event.setDeathMessage(playerColor + player.getName() + " §6est mort.");
        }
    }

}
