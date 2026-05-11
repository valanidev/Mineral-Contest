package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.game.kits.KitWorker;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.ScoreManager;
import dev.valani.mineralcontest.managers.TeamManager;
import dev.valani.mineralcontest.utils.LogUtil;
import dev.valani.mineralcontest.utils.SoundUtil;
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

    public TeamChestListener(Main plugin) {
        this.gameManager = plugin.getGameManager();
        this.teamManager = gameManager.getTeamManager();
        this.scoreManager = gameManager.getScoreManager();
        this.teamInventories = new HashMap<>();

        this.allowedDrops = scoreManager.getAllowedDrops();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        Location loc = clickedBlock.getLocation();
        if (!teamManager.getTeamLocationManager().isTeamChest(loc)) return;

        Player player = event.getPlayer();

        if (!gameManager.isInGame()) {
            event.setCancelled(true);
            player.sendMessage("§cLa partie n'a pas encore commencé.");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        Team playerTeam = teamManager.getPlayerTeam(player).orElse(null);
        if (playerTeam == null) {
            event.setCancelled(true);
            player.sendMessage("§cTu n'as pas d'équipe.");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        Location chestLoc = gameManager.getTeamManager().getTeamChestLocation(playerTeam);
        if (chestLoc == null) return;
        if (!clickedBlock.getLocation().equals(chestLoc.getBlock().getLocation())) {
            player.sendMessage("§cTu ne peux pas ouvrir le coffre des autres équipes.");
            SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_NO);
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        Inventory inv = createInventory(playerTeam);
        if (inv == null) return;

        player.openInventory(inv);
        SoundUtil.playForPlayer(player, Sound.BLOCK_ENDER_CHEST_OPEN);
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

        Collection<ItemStack> positiveDrops = drops.stream()
                .filter(item -> scoreManager.getValue(item.getType()) > 0)
                .toList();
        Collection<ItemStack> negativeDrops = drops.stream()
                .filter(item -> scoreManager.getValue(item.getType()) < 0)
                .toList();

        int teamScore = scoreManager.calculateScore(positiveDrops);
        int otherScore = scoreManager.calculateScore(negativeDrops);

        KitBase kit = gameManager.getKitManager().getKit(player);
        boolean boosted = false;
        if(kit != null) {
            if(kit instanceof KitWorker) {
                boosted = true;
                teamScore = (int) (teamScore * KitWorker.SCORE_MULTIPLIER);
                otherScore = (int) (otherScore * KitWorker.SCORE_MULTIPLIER);
            }
        }

        if(otherScore < 0) {
            List<Team> targets = teamManager.getOtherTeams(team);
            if(targets.isEmpty()) {
                LogUtil.log("§cAucune équipe disponible pour retirer des points.");
                return;
            }
            int finalOtherScore = otherScore;
            boolean finalBoosted = boosted;
            targets.forEach(target -> scoreManager.applyScore(target, finalOtherScore, player, finalBoosted));

            for(UUID teamPlayerUuid : team.getMembers()) {
                Player teamPlayer = Bukkit.getPlayer(teamPlayerUuid);
                if(teamPlayer != null && !player.equals(teamPlayer)) {
                    teamPlayer.sendMessage(
                            team.getColor() + "§l" + player.getName() + "§a§l a fait perdre §e§l" + otherScore
                                    + " §a§lpoints aux autres équipes !" + (boosted ? " §7§l(§e§lBOOSTED§7§l)" : ""));
                }
            }
            player.sendMessage("§a§lTu as fait perdre §e§l" + otherScore + " §a§lpoints aux autres équipes !" + (boosted ? " §7§l(§e§lBOOSTED§7§l)" : ""));

            LogUtil.log("§e" + otherScore + "§a points retirés pour les équipes: §r" + negativeDrops);
        }
        if(teamScore > 0) {
            scoreManager.applyScore(team, teamScore, player, boosted);
            LogUtil.log("§e" + teamScore + "§a points ajoutés pour l'équipe §r" + team.getDisplayName() + "§a: §r" + positiveDrops);
        }

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
        if (!teamManager.getTeamLocationManager().isTeamChest(loc)) return;
        event.setCancelled(true);
    }
}
