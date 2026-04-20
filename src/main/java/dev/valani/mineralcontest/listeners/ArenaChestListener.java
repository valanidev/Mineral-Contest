package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;

public class ArenaChestListener implements Listener {

    private static final int ANIMATION_TICKS = 100;
    private static final int ANIMATION_PERIOD = 4;

    private final Main plugin;
    private final ArenaManager arenaManager;
    private final Inventory arenaChestInventory;
    private final ItemStack redPane;
    private final ItemStack greenPane;
    private BukkitTask animationTask;
    private Player animatingPlayer;

    public ArenaChestListener(Main plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.arenaChestInventory = Bukkit.createInventory(null, 27, "§6§lArena Chest");

        this.redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redMeta = this.redPane.getItemMeta();
        if (redMeta != null) {
            redMeta.setDisplayName("§r");
            this.redPane.setItemMeta(redMeta);
        }

        this.greenPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta greenMeta = this.greenPane.getItemMeta();
        if (greenMeta != null) {
            greenMeta.setDisplayName("§r");
            this.greenPane.setItemMeta(greenMeta);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Material expectedMaterial = arenaManager.getChestMaterial();
        if (expectedMaterial == null || clickedBlock.getType() != expectedMaterial) return;
        Location chestLoc = arenaManager.getChestLocation();
        if (chestLoc == null) return;
        if (!clickedBlock.getLocation().equals(chestLoc.getBlock().getLocation())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!arenaManager.isChestAvailable()) {
            // Si un joueur est en train d'ouvrir le coffre on met un message custom, sinon il est indisponible
            String key = animatingPlayer != null ? "arena.chest_in_use" : "arena.chest_unavailable";
            player.sendMessage(plugin.getString(key));
            return;
        }

        // Si c'est un coffre on joue son animation
        if (clickedBlock.getState() instanceof Chest chest) {
            chest.open();
        }

        arenaManager.lockForAnimation();
        startAnimation(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (animatingPlayer == null) return;
        if (!event.getView().getTopInventory().equals(arenaChestInventory)) return;
        event.setCancelled(true);
    }

    private void startAnimation(Player player) {
        Location chestLoc = arenaManager.getChestLocation();
        arenaChestInventory.clear();
        for (int i = 0; i < 27; i++) {
            arenaChestInventory.setItem(i, redPane);
        }
        animatingPlayer = player;
        player.openInventory(arenaChestInventory);

        final int[] state = {0, 0}; // state[0] = elapsed, state[1] = previousGreenSlots
        animationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            state[0] += ANIMATION_PERIOD;
            int greenSlots = Math.min((int) Math.round((state[0] / (double) ANIMATION_TICKS) * 27), 27);

            if (greenSlots > state[1]) {
                float pitch = 0.5f + (greenSlots / 27.0f) * 1.5f;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, pitch);
                state[1] = greenSlots;
            }

            for (int i = 0; i < 27; i++) {
                arenaChestInventory.setItem(i, i < greenSlots ? greenPane : redPane);
            }

            if (state[0] >= ANIMATION_TICKS) {
                finishAnimation(player);
            }

            Location playerLoc = player.getLocation();
            if (playerLoc.distance(chestLoc) > 3) {
                cancelAnimation();
                player.closeInventory();
            }

        }, ANIMATION_PERIOD, ANIMATION_PERIOD);
    }

    private void finishAnimation(Player player) {
        animationTask.cancel();
        animationTask = null;
        animatingPlayer = null;
        arenaChestInventory.clear();
        arenaManager.onChestLooted();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.closeInventory();
        giveRewards(player);
        Bukkit.broadcastMessage(plugin.getPrefix() + "Le coffre d'arène a été récupéré !");
    }

    private void cancelAnimation() {
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
        }
        animationTask = null;
        animatingPlayer = null;
        arenaChestInventory.clear();
        arenaManager.unlockFromAnimation();
    }

    private void giveRewards(Player player) {
        List<Map<?, ?>> rewards = plugin.getConfig().getMapList("arena.chest_rewards");
        for (Map<?, ?> reward : rewards) {
            String materialName = (String) reward.get("material");
            int min = ((Number) reward.get("min")).intValue();
            int max = ((Number) reward.get("max")).intValue();
            Material material = Material.getMaterial(materialName);
            if (material == null) continue;
            int amount = min + (int) (Math.random() * (max - min + 1));
            player.getInventory().addItem(new ItemStack(material, amount));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(arenaChestInventory)) return;

        if (animatingPlayer != null && animatingPlayer.equals(event.getPlayer())) {
            cancelAnimation();
        }

        Location chestLoc = arenaManager.getChestLocation();
        if (chestLoc == null) return;
        if (chestLoc.getBlock().getState() instanceof Chest chest) {
            chest.close();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isArenaChest(event.getBlock())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage("§cTu ne peux pas détruire le coffre d'arène !");
    }

    private boolean isArenaChest(Block block) {
        Location chestLoc = arenaManager.getChestLocation();
        if (chestLoc == null) return false;
        return block.getLocation().equals(chestLoc.getBlock().getLocation());
    }
}
