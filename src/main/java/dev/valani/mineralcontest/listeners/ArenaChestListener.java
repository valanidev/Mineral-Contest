package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.commands.CommandArenaChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ArenaChestListener implements Listener {

    private final Main plugin;
    private final CommandArenaChest arenaChestCommand;

    public ArenaChestListener(Main plugin, CommandArenaChest arenaChestCommand) {
        this.plugin = plugin;
        this.arenaChestCommand = arenaChestCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Material expectedMaterial = arenaChestCommand.getCachedChestMaterial();
        if (expectedMaterial == null || clickedBlock.getType() != expectedMaterial) return;
        Location chestLoc = arenaChestCommand.getCachedChestLocation();
        if (chestLoc == null) return;
        if (!clickedBlock.getLocation().equals(chestLoc.getBlock().getLocation())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.openInventory(Bukkit.createInventory(null, 27, "Arena Chest"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isArenaChest(event.getBlock())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage("§cTu ne peux pas détruire le coffre d'arène !");
    }

    private boolean isArenaChest(Block block) {
        Location chestLoc = arenaChestCommand.getCachedChestLocation();
        if (chestLoc == null) return false;
        return block.getLocation().equals(chestLoc.getBlock().getLocation());
    }

}
