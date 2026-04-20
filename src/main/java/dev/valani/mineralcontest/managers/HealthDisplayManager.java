package dev.valani.mineralcontest.managers;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class HealthDisplayManager {

    private final ScoreboardManager manager;
    private final Scoreboard scoreboard;

    public HealthDisplayManager() {
        manager = Bukkit.getScoreboardManager();
        if (manager == null) throw new IllegalStateException("ScoreboardManager is null");

        this.scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                "health",
                Criteria.HEALTH,
                "§c❤",
                RenderType.HEARTS
        );

        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void applyToPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void removeFromPlayer(Player player) {
        player.setScoreboard(manager.getMainScoreboard());
    }

    public void applyToAll() {
        Bukkit.getOnlinePlayers().forEach(this::applyToPlayer);
    }

    public void removeFromAll() {
        Bukkit.getOnlinePlayers().forEach(this::removeFromPlayer);
    }
}