package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamChestListener implements Listener {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final List<Material> allowedDrops;

    private final Map<Team, Inventory> teamInventories;
    private Block clickedBlock;

    public TeamChestListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.teamManager = gameManager.getTeamManager();
        this.teamInventories = new HashMap<>();

        allowedDrops = new ArrayList<>(Arrays.asList(Material.DIAMOND, Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Location chestLoc = gameManager.getTeamManager().getTeamChestLocation(teamManager.getPlayerTeam(event.getPlayer()).orElse(null));
        if (chestLoc == null) return;
        if (!clickedBlock.getLocation().equals(chestLoc.getBlock().getLocation())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        Inventory inv = getTeamInventory(player);
        if (inv == null) return;

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Inventory teamInventory = getTeamInventory(player);
        if (teamInventory == null) return;
        if (!event.getInventory().equals(teamInventory)) return;

        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory teamInventory = getTeamInventory(player);
        if (teamInventory == null) return;
        if (!event.getInventory().equals(teamInventory)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        event.setCancelled(!allowedDrops.contains(item.getType()));
    }

    @EventHandler
    public void onTeamChestClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Inventory teamInventory = getTeamInventory(player);
        if (teamInventory == null) return;
        if (!event.getInventory().equals(teamInventory)) return;

        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null) {
                drops.add(item);
            }
        }
        teamInventory.clear();
        int score = countScore(drops, teamManager.getPlayerTeam(player).orElse(null));
        if (score == 0) return;

        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return;
        team.addScore(score);

        for (UUID uuid : team.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            p.sendMessage("§a§l+" + score + " points");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }

    }

    private Inventory getTeamInventory(Player player) {
        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return null;

        return teamInventories.computeIfAbsent(team, t -> {
            String name = t.getColor() + "§lCoffre " + t.getName();
            return Bukkit.createInventory(null, 27, name);
        });
    }

    private final Map<Material, Integer> dropScores = Map.of(
            Material.EMERALD, 300,
            Material.DIAMOND, 200,
            Material.IRON_INGOT, 20,
            Material.GOLD_INGOT, 40
    );

    public int countScore(List<ItemStack> drops, Team team) {
        int total = 0;
        for (ItemStack item : drops) {
            if (item == null) continue;
            if (!allowedDrops.contains(item.getType())) continue;
            total += dropScores.getOrDefault(item.getType(), 0) * item.getAmount();
        }
        return total;
    }
}
