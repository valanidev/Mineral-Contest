package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SbManager {

    private final Main plugin;

    private final Map<UUID, Scoreboard> boards;

    public SbManager(Main plugin) {
        this.plugin = plugin;
        this.boards = new HashMap<>();
        startUpdater();
    }

    public void create(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective(
                "mc",
                Criteria.DUMMY,
                "§6§lMINERAL CONTEST",
                RenderType.INTEGER
        );

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
    }

    private void startUpdater() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            GameManager gm = Main.getInstance().getGameManager();

            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player, gm);
            }

        }, 0L, 20L);
    }

    public void update(Player player, GameManager gm) {

        Scoreboard board = boards.get(player.getUniqueId());

        if (board == null) {
            create(player);
            board = boards.get(player.getUniqueId());
        }

        Objective obj = board.getObjective("mc");
        if (obj == null) return;

        board.getEntries().forEach(board::resetScores);

        Optional<Team> teamOpt = gm.getTeamManager().getPlayerTeam(player);
        String teamName = teamOpt
                .map(Team::getDisplayName)
                .orElse("Aucune");

        int score = teamOpt.map(Team::getScore).orElse(0);

        KitBase kit = gm.getKitManager().getKit(player);
        String kitName = (kit != null ? kit.getDisplayName() : "Aucun");

        obj.getScore(" ").setScore(15);
        obj.getScore("§7| §r" + player.getName()).setScore(14);
        obj.getScore("§7Kit: §b" + kitName).setScore(13);
        obj.getScore("  ").setScore(12);
        obj.getScore("§7| §rPartie").setScore(11);
        obj.getScore("§7Temps restant: §b" + Utils.formatSbTimer(gm.getTimeLeftSeconds())).setScore(10);
        obj.getScore("   ").setScore(9);
        obj.getScore("§7| §rÉquipe §f" + teamName).setScore(8);
        obj.getScore("§7Points: §b" + score).setScore(7);
    }

    public void remove(Player player) {
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
        boards.remove(player.getUniqueId());
    }

    public void resetAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player);
        }
        boards.clear();
    }

}
