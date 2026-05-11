package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class KitManager {

    private final Main plugin;
    private final List<KitBase> kits;
    private final Map<UUID, KitBase> playerKits = new HashMap<>();

    public KitManager(Main plugin) {
        this.plugin = plugin;
        this.kits = List.of(
                new KitMiner(),
                new KitBarbarian(),
                new KitRobust(),
                new KitAgile(),
                new KitStealer(),
                new KitWorker(),
                new KitLucky()
        );
    }

    public void assignKit(Player player, KitBase kit) {
        removeKit(player);
        playerKits.put(player.getUniqueId(), kit);
        Team team = plugin.getGameManager().getTeamManager().getPlayerTeam(player).orElse(null);
        kit.apply(player, team);
    }

    public void assignRandomKit(Player player) {
        KitBase kit = kits.get(ThreadLocalRandom.current().nextInt(kits.size()));
        assignKit(player, kit);
    }

    public void removeKit(Player player) {
        KitBase current = playerKits.remove(player.getUniqueId());
        if (current != null) current.remove(player);
    }

    public void resetAll() {
        playerKits.forEach((uuid, kit) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) kit.remove(player);
        });
        playerKits.clear();
    }

    // --- Getters ---

    public List<KitBase> getKits() {
        return kits;
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
}
