package dev.valani.mineralcontest.listeners;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.KitWorker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitWorkerListener implements Listener {

    private final Main plugin;

    // Dernière position connue de chaque joueur
    private final Map<UUID, Location> lastPositions = new HashMap<>();
    // Joueurs qui ont actuellement la regen
    private final Map<UUID, Boolean> regenActive = new HashMap<>();

    public KitWorkerListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkPlayers, 0L, 20L);
    }

    private void checkPlayers() {
        if(!plugin.getGameManager().isInGame()) {
            lastPositions.clear();
            regenActive.clear();
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(plugin.getGameManager().getKitManager().getKit(player) instanceof KitWorker)) {
                removeRegen(player);
                lastPositions.remove(player.getUniqueId());
                continue;
            }

            Location current = player.getLocation();
            Location last    = lastPositions.get(player.getUniqueId());

            boolean isStill = last != null
                    && current.getBlockX() == last.getBlockX()
                    && current.getBlockY() == last.getBlockY()
                    && current.getBlockZ() == last.getBlockZ();

            if (isStill) {
                applyRegen(player);
            } else {
                removeRegen(player);
            }

            lastPositions.put(player.getUniqueId(), current.clone());
        }
    }

    private void applyRegen(Player player) {
        if (Boolean.TRUE.equals(regenActive.get(player.getUniqueId()))) return;

        regenActive.put(player.getUniqueId(), true);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                Integer.MAX_VALUE,
                1,
                false,
                false,
                true
        ));
    }

    private void removeRegen(Player player) {
        if (!Boolean.TRUE.equals(regenActive.get(player.getUniqueId()))) return;

        regenActive.put(player.getUniqueId(), false);
        player.removePotionEffect(PotionEffectType.REGENERATION);
    }
}