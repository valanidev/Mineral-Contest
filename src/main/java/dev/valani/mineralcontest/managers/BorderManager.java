package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.utils.FormatUtil;
import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class BorderManager {

    private final int shrink_duration;
    private static final int ANNOUNCE_BEFORE = 60;

    private final Main plugin;
    private final WorldBorder border;

    private final double initialRadius;

    private final List<BorderStage> stages = new ArrayList<>();
    private final Set<Integer> announced = new HashSet<>();

    public BorderManager(Main plugin, World world) {
        this.plugin = plugin;
        this.border = world.getWorldBorder();
        this.initialRadius = plugin.getConfigManager()
                .getInt("settings.world_border.initial_size");
        this.shrink_duration = plugin.getConfigManager()
                .getInt("settings.world_border.shrink_duration");

        stop();
    }

    public void start() {
        stop();

        int totalTime = plugin.getGameManager()
                .getTimeManager()
                .getTimeLeft();

        loadStages(totalTime);

        if (stages.isEmpty()) {
            LogUtil.error("Aucun stage de world border valide.");
            return;
        }

        LogUtil.log("World border démarrée : " + stages.size() + " étape(s).");
    }

    public void stop() {
        border.setCenter(0, 0);
        border.setSize(Math.max(1, toDiameter(initialRadius)));
        announced.clear();
    }

    public void tick(int timeLeft) {
        for (Iterator<BorderStage> iterator = stages.iterator(); iterator.hasNext();) {
            BorderStage stage = iterator.next();

            int announceAt = stage.triggerAtSeconds() + ANNOUNCE_BEFORE;

            if (timeLeft == announceAt && announced.add(stage.triggerAtSeconds())) {
                Bukkit.broadcastMessage("§6⚠ §eLa world border rétrécira à §6"
                        + (int) stage.targetRadius()
                        + " §eblocs dans §c"
                        + FormatUtil.formatTimeLong(ANNOUNCE_BEFORE)
                        + "§e !");
            }

            if (timeLeft == stage.triggerAtSeconds()) {
                startShrink(stage.targetRadius());

                Bukkit.broadcastMessage("§c⚠ §eLa world border rétrécit à §6"
                        + (int) stage.targetRadius()
                        + " §eblocs !");

                iterator.remove();
            }
        }
    }

    private void startShrink(double targetRadius) {
        double targetDiameter = toDiameter(targetRadius);

        double startDiameter = border.getSize();
        double delta = startDiameter - targetDiameter;
        double step = delta / (shrink_duration * 20.0);

        int totalTicks = shrink_duration * 20;
        int[] elapsed = {0};

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            elapsed[0]++;

            if (elapsed[0] >= totalTicks) {
                border.setSize(targetDiameter);
                task.cancel();
                return;
            }

            border.setSize(border.getSize() - step);
        }, 0L, 1L);
    }

    private void loadStages(int totalTime) {
        stages.clear();

        ConfigurationSection section = plugin.getConfigManager()
                .getConfigurationSection("settings.world_border.stages");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                int triggerAt = Integer.parseInt(key);

                if (triggerAt > totalTime) {
                    LogUtil.log("Stage ignoré (> durée totale) : " + key);
                    continue;
                }

                double radius = section.getDouble(key);

                stages.add(new BorderStage(triggerAt, radius));
            } catch (NumberFormatException e) {
                LogUtil.error("Stage invalide : " + key);
            }
        }

        stages.sort(Comparator.comparingInt(BorderStage::triggerAtSeconds).reversed());
    }

    private double toDiameter(double radius) {
        return radius * 2.0;
    }

    public record BorderStage(int triggerAtSeconds, double targetRadius) {
    }
}