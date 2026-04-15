package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.game.kits.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitManager {

    public static final List<KitBase> KITS = List.of(
            new KitMiner(),
            new KitBarbarian()
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

    public void resetAll() {
        playerKits.forEach((uuid, kit) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) kit.remove(player);
        });
        playerKits.clear();
    }
}