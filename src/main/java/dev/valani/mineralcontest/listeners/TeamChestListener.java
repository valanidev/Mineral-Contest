package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.game.GameState;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.ScoreManager;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamChestListener implements Listener {

    private final GameManager gameManager;
    private final TeamManager teamManager;
    private final ScoreManager scoreManager;
    private final Set<Material> allowedDrops;

    private final Map<Team, Inventory> teamInventories;

    public TeamChestListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.teamManager = gameManager.getTeamManager();
        this.scoreManager = gameManager.getScoreManager();
        this.teamInventories = new HashMap<>();

        allowedDrops = gameManager.getDropScores().keySet();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Location loc = clickedBlock.getLocation();
        if (!teamManager.isTeamChest(loc)) return;

        Player player = event.getPlayer();

        if (!gameManager.isState(GameState.STARTED)) {
            event.setCancelled(true);
            player.sendMessage("§cLa partie n'a pas encore commencé.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);
        if (playerTeam == null) {
            event.setCancelled(true);
            player.sendMessage("§cTu n'as pas d'équipe.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        Location chestLoc = gameManager.getTeamManager().getTeamChestLocation(playerTeam);
        if (chestLoc == null) return;
        if (!clickedBlock.getLocation().equals(chestLoc.getBlock().getLocation())) {
            player.sendMessage("§cTu ne peux pas ouvrir le coffre des autres équipes.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        Inventory inv = createInventory(playerTeam);
        if (inv == null) return;

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return;
        Inventory teamInventory = getExistingInventory(team);
        if (teamInventory == null) return;
        if (!event.getInventory().equals(teamInventory)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        event.setCancelled(!allowedDrops.contains(item.getType()));
    }

    @EventHandler
    public void onTeamChestClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Team team = teamManager.getPlayerTeam(player).orElse(null);
        if (team == null) return;
        Inventory teamInventory = getExistingInventory(team);
        if (teamInventory == null) return;
        if (!event.getInventory().equals(teamInventory)) return;

        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null) {
                drops.add(item);
            }
        }
        teamInventory.clear();

        int score = scoreManager.calculateScore(drops);
        Utils.consoleDebug("§e" + score + "§a points ajoutés pour l'équipe §r" + team.getDisplayName() + "§a: §r" + drops.toString());
        scoreManager.applyScore(team, score);

        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 1.0f);
    }

    private Inventory getExistingInventory(Team team) {
        return teamInventories.get(team);
    }

    private Inventory createInventory(Team team) {
        return teamInventories.computeIfAbsent(team, t -> {
            String name = t.getColor() + "§lCoffre " + t.getName();
            return Bukkit.createInventory(null, 27, name);
        });
    }

    @EventHandler
    public void onTeamChestBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!teamManager.isTeamChest(loc)) return;
        event.setCancelled(true);
    }
}
