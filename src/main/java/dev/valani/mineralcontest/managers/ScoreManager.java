package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreManager {

    private final GameManager gameManager;

    public ScoreManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public int calculateScore(Collection<ItemStack> items) {
        int total = 0;
        for (ItemStack item : items) {
            if (item == null) continue;
            int value = gameManager.getDropScores().getOrDefault(item.getType(), 0);
            total += value * item.getAmount();
        }
        return total;
    }

    public void applyScore(Team team, int score) {
        if (team == null || score <= 0) return;

        team.addScore(score);

        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.sendMessage("§a§l+" + score + " points");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
        }
    }
}
