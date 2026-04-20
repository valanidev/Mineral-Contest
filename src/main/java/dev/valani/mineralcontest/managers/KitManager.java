package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.kits.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitManager {

    private final Main plugin;

    public KitManager(Main plugin) {
        this.plugin = plugin;
    }

    public static final List<KitBase> KITS = List.of(
            new KitMiner(),
            new KitBarbarian(),
            new KitAgile(),
            new KitRobust()
    );

    private final Map<UUID, KitBase> playerKits = new HashMap<>();

    public void assignKit(Player player, KitBase kit) {
        removeKit(player);

        playerKits.put(player.getUniqueId(), kit);
        kit.apply(player);
    }

    public void removeKit(Player player) {
        KitBase current = playerKits.remove(player.getUniqueId());
        if (current != null) current.remove(player);
    }

    public KitBase getKit(Player player) {
        return playerKits.get(player.getUniqueId());
    }

    public boolean hasKit(Player player, KitBase kit) {
        return kit.equals(playerKits.get(player.getUniqueId()));
    }

    public boolean hasKit(Player player) {
        return playerKits.containsKey(player.getUniqueId());
    }

    public void resetAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (KitBase kit : KITS) {
                kit.remove(player);
            }
        }
        playerKits.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("§r§nKits§7: §r");
        KITS.forEach(
                kit -> builder
                        .append(kit.toString())
                        .append("§r, "));
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}