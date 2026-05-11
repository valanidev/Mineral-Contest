package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class SbManager {

    private final Main plugin;
    private final GameManager gameManager;

    private final Map<UUID, Scoreboard> boards;

    private final Map<UUID, Integer> lastTimeLeft = new HashMap<>();
    private final Map<UUID, Integer> lastScore = new HashMap<>();
    private final Map<UUID, String> lastKit = new HashMap<>();
    private final Map<UUID, String> lastTeam = new HashMap<>();

    public SbManager(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.boards = new HashMap<>();
        startUpdater();
    }

    private void registerTeams(Scoreboard board) {
        for(Team team : gameManager.getTeamManager().getTeams()) {
            String teamName = team.getName().toLowerCase();
            org.bukkit.scoreboard.Team sbTeam = board.getTeam(teamName);
            if(sbTeam == null) {
                sbTeam = board.registerNewTeam(teamName);
            }

            sbTeam.setColor(team.getColor());
            sbTeam.setOption(
                org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,
                org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM
            );
            boolean show_prefix = plugin.getConfigManager().isShowNametag();
            boolean friendly_fire = plugin.getConfigManager().isAllowFriendlyFire();
            if(show_prefix) sbTeam.setPrefix(team.getDisplayName() + " ");
            sbTeam.setAllowFriendlyFire(friendly_fire);

            for(UUID uuid : team.getMembers()) {
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(uuid);
                sbTeam.addEntry(offPlayer.getName());
            }

        }
    }

    public void create(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();

        String name = plugin.getConfigManager().getString("messages.scoreboard_name");
        Objective obj = board.registerNewObjective(
                "mineral",
                Criteria.DUMMY,
                name,
                RenderType.INTEGER
        );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Objective health = board.registerNewObjective(
                "health",
                Criteria.HEALTH,
                "§c❤",
                RenderType.HEARTS
        );
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);

        registerTeams(board);

        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);

        update(player);
    }

    private void startUpdater() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (!boards.containsKey(player.getUniqueId())) {
                create(player);
            }

            update(player);
        }), 0L, 20L);
    }

    public void update(Player player) {
        Scoreboard board = boards.get(player.getUniqueId());
        if (board == null) return;

        Objective obj = board.getObjective("mineral");
        if (obj == null) return;

        int timeLeft = gameManager.getTimeManager().getTimeLeft();
        Optional<Team> teamOpt = gameManager.getTeamManager().getPlayerTeam(player);
        String teamName = teamOpt.map(Team::getDisplayName).orElse("Aucune");
        int score = teamOpt.map(Team::getScore).orElse(0);
        KitBase kit = gameManager.getKitManager().getKit(player);
        String kitName = (kit != null ? kit.getDisplayName() : "Aucun");

        UUID uuid = player.getUniqueId();
        String previousKit = lastKit.get(uuid);
        String previousTeam = lastTeam.get(uuid);
        if (timeLeft == lastTimeLeft.getOrDefault(uuid, -1)
                && score == lastScore.getOrDefault(uuid, -1)
                && Objects.equals(previousKit, kitName)
                && Objects.equals(previousTeam, teamName)) return;

        lastTimeLeft.put(uuid, timeLeft);
        lastScore.put(uuid, score);
        lastKit.put(uuid, teamName);
        lastTeam.put(uuid, kitName);

        board.getEntries().forEach(entry -> {
            Score s = obj.getScore(entry);
            if (s.isScoreSet()) board.resetScores(entry);
        });

        obj.getScore(" ").setScore(9);
        obj.getScore("§7| §r" + player.getName()).setScore(8);
        obj.getScore("§7Kit: §b" + kitName).setScore(7);
        obj.getScore("  ").setScore(6);
        obj.getScore("§7| §rPartie").setScore(5);
        obj.getScore("§7Temps restant: §b" + FormatUtil.formatTimeShort(timeLeft)).setScore(4);
        obj.getScore("   ").setScore(3);
        obj.getScore("§7| §rÉquipe §f" + teamName).setScore(2);
        obj.getScore("§7Points: §b" + score).setScore(1);
    }

    public void remove(Player player) {
        lastTimeLeft.remove(player.getUniqueId());
        lastScore.remove(player.getUniqueId());
        lastKit.remove(player.getUniqueId());
        lastTeam.remove(player.getUniqueId());
        boards.remove(player.getUniqueId());

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        player.setScoreboard(manager.getNewScoreboard());
    }

    public void refreshTeam(Team team) {
        for (Scoreboard board : boards.values()) {
            String teamName = team.getName().toLowerCase();
            org.bukkit.scoreboard.Team sbTeam = board.getTeam(teamName);
            if (sbTeam == null) sbTeam = board.registerNewTeam(teamName);

            sbTeam.setOption(
                    org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,
                    org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM
            );

            sbTeam.getEntries().forEach(sbTeam::removeEntry);
            for (UUID uuid : team.getMembers()) {
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if (name != null) sbTeam.addEntry(name);
            }
        }
    }

    public void resetAll() {
        Bukkit.getOnlinePlayers().forEach(this::remove);
        boards.clear();
    }

}
