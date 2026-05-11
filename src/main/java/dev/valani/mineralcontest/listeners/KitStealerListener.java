package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.game.kits.KitStealer;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KitStealerListener implements Listener {

    private final Main plugin;
    private final KitManager kitManager;

    public KitStealerListener(Main plugin) {
        this.plugin = plugin;
        this.kitManager = plugin.getGameManager().getKitManager();
    }

    @EventHandler
    public void onPlayerInteract(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        KitBase attackerKit = kitManager.getKit(attacker);
        KitBase victimKit = kitManager.getKit(victim);

        boolean attackerIsStealer = attackerKit instanceof KitStealer;
        boolean victimIsStealer = victimKit instanceof KitStealer;

        if (!attackerIsStealer && !victimIsStealer) return;

        Player thief;
        Player target;
        float probability;

        thief = attacker;
        target = victim;
        if (attackerIsStealer) {
            probability = KitStealer.PICKPOCKET_CHANCE;
        } else {
            probability = KitStealer.COUNTER_PICKPOCKET_CHANCE;
        }

        if (ThreadLocalRandom.current().nextFloat() > probability) return;

        Set<Material> allowedDrops = plugin.getGameManager()
                .getScoreManager()
                .getAllowedDrops();

        List<ItemStack> stealableItems = Arrays.stream(target.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> allowedDrops.contains(item.getType()))
                .collect(Collectors.groupingBy(
                        ItemStack::getType,
                        Collectors.summingInt(ItemStack::getAmount)
                ))
                .entrySet()
                .stream()
                .map(entry -> new ItemStack(entry.getKey(), entry.getValue()))
                .toList();

        if (stealableItems.isEmpty()) {
            return;
        }

        ItemStack selected = stealableItems.get(
                ThreadLocalRandom.current().nextInt(stealableItems.size())
        );

        int maxStealable = Math.min(selected.getAmount(), 24);
        int stolenAmount = ThreadLocalRandom.current().nextInt(1, maxStealable + 1);

        ItemStack stolenItem = new ItemStack(selected.getType(), stolenAmount);
        thief.getInventory().addItem(stolenItem);

        int remaining = stolenAmount;
        for (ItemStack item : target.getInventory().getContents()) {
            if (item == null || item.getType() != selected.getType()) continue;

            int remove = Math.min(item.getAmount(), remaining);
            item.setAmount(item.getAmount() - remove);
            remaining -= remove;

            if (item.getAmount() <= 0) {
                item.setType(Material.AIR);
            }

            if (remaining <= 0) {
                break;
            }
        }

        String itemName = selected.getType().name().toLowerCase().replace("_", " ");

        thief.sendMessage("§aVous avez volé §2"
                + itemName
                + " §a(§2x" + stolenAmount + "§a) à §2"
                + target.getName() + "§a !");

        target.sendMessage("§4"
                + thief.getName()
                + "§c vous a volé §4"
                + itemName
                + " §c(§4x" + stolenAmount + "§c) !");

        SoundUtil.playForPlayer(thief, Sound.ENTITY_ITEM_PICKUP);
        SoundUtil.playForPlayer(target, Sound.ENTITY_ITEM_BREAK);
    }
}
