package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ScoreManager {

    private final Main plugin;
    private final Map<Material, Integer> values = new EnumMap<>(Material.class);

    public ScoreManager(Main plugin) {
        this.plugin = plugin;
        loadScores();
    }

    private void loadScores() {
        values.clear();

        ConfigurationSection section = plugin.getConfigManager().getConfigurationSection("settings.scores");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) continue;
            values.put(material, section.getInt(key));
        }
    }

    public int calculateScore(Collection<ItemStack> items) {
        if (items == null || items.isEmpty()) return 0;
        int score = 0;
        for (ItemStack item : items) {
            if (item == null || item.getType().isAir()) continue;
            Integer value = values.get(item.getType());
            if (value == null) continue;
            score += value * item.getAmount();
        }
        return score;
    }

    public void applyScore(Team team, int score, Player from_player, boolean boosted) {
        if (team == null || score == 0) return;

        team.addScore(score);

        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if(score > 0) {
                player.sendMessage("§a§l+" + score + " points de la part de " + from_player.getName() + " !" + (boosted ? " §7(§eBOOSTED§7)" : ""));
                SoundUtil.playForPlayer(player, Sound.BLOCK_NOTE_BLOCK_PLING);
            } else {
                player.sendMessage("§c§l" + score + " points de la part de " + from_player.getName() + " !");
                SoundUtil.playForPlayer(player, Sound.ENTITY_VILLAGER_DEATH);
            }
        }
    }

    public Set<Material> getAllowedDrops() {
        return Collections.unmodifiableSet(values.keySet());
    }

    public int getValue(Material material) {
        return values.getOrDefault(material, 0);
    }
}
