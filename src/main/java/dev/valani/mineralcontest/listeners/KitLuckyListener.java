package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.KitLucky;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class KitLuckyListener implements Listener {

    private final GameManager gameManager;

    private final Set<Material> luckyBlocks = Set.of(
            Material.STONE, Material.DEEPSLATE,
            Material.GRANITE, Material.DIORITE, Material.ANDESITE
    );

    public KitLuckyListener(Main plugin) {
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!(gameManager.getKitManager().getKit(player) instanceof KitLucky)) return;
        if (!luckyBlocks.contains(event.getBlock().getType())) return;
        if (ThreadLocalRandom.current().nextDouble(100) >= KitLucky.CHANCE_TO_GET_MINERAL) return;

        Material reward;

        int roll = ThreadLocalRandom.current().nextInt(101);
        if (roll <= 10) { // 10%
            reward = Material.EMERALD;
        } else if (roll <= 30) { // 20%
            reward = Material.DIAMOND;
        } else if (roll <= 60) { // 30%
            reward = Material.GOLD_INGOT;
        } else { // 40%
            reward = Material.IRON_INGOT;
        }

        event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                new ItemStack(reward)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof  Player attacker)) return;
        if(!(event.getEntity() instanceof Player)) return;
        if(!(gameManager.getKitManager().getKit(attacker) instanceof KitLucky)) return;

        double doubleChance = KitLucky.CHANCE_TO_DOUBLE_DAMAGE;
        double counterDamage = KitLucky.CHANCE_TO_TAKE_DAMAGE;

        if(ThreadLocalRandom.current().nextDouble(100) <= doubleChance) {
            attacker.sendMessage("§cVous avez infligé un dégât doublé !");
            event.setDamage(event.getDamage() * 2);
        }
        if(ThreadLocalRandom.current().nextDouble(100) <= counterDamage) {
            double damage = event.getDamage();
            attacker.sendMessage("§cVous avez pris un contre-coup !");
            attacker.damage(damage);
        }
    }
}
