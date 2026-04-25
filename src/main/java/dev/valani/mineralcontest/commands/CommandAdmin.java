package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.DoorOrientation;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandAdmin implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final GameManager gameManager;

    private static final Map<String, Map<String, List<String>>> STRUCTURE = Map.of(
            "team", Map.of(
                    "chest", List.of("get", "set", "remove"),
                    "arena", List.of("get", "set", "remove", "tp"),
                    "spawn", List.of("get", "set", "remove", "tp"),
                    "score", List.of("get", "set", "reset"),
                    "door", List.of("get", "set", "remove")
            ),
            "force", Map.of(
                    "arena", List.of(),
                    "drop", List.of()
            ),
            "player", Map.of(
                    "team", List.of("set"),
                    "kit", List.of("set")
            )
    );

    public CommandAdmin(Main plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1)
            return filter(STRUCTURE.keySet().stream().toList(), args[0]);

        String root = args[0].toLowerCase();
        if (!STRUCTURE.containsKey(root)) return List.of();

        if (args.length == 2)
            return filter(STRUCTURE.get(root).keySet().stream().toList(), args[1]);

        String sub = args[1].toLowerCase();
        if (!STRUCTURE.get(root).containsKey(sub)) return List.of();

        if (args.length == 3) {
            if (root.equals("team"))
                return filter(STRUCTURE.get(root).get(sub), args[2]);

            // /admin player <sub> <action> → arg3 = action
            if (root.equals("player"))
                return filter(STRUCTURE.get(root).get(sub), args[2]);
        }

        if (args.length == 4) {
            if (root.equals("team"))
                return filterTeams(args[3]);

            if (root.equals("player")) {
                if (sub.equals("team")) return filterTeams(args[3]);
                if (sub.equals("kit")) return filterKits(args[3]);
            }
        }

        if (args.length == 5 && root.equals("team") && sub.equals("score") && args[2].equalsIgnoreCase("set"))
            return List.of("<valeur>");

        return List.of();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getString("plugin.only_player_command"));
            return false;
        }

        if (args.length < 2) {
            sendUsage(player, label);
            return false;
        }

        return switch (args[0].toLowerCase()) {
            case "team" -> handleTeam(player, label, args);
            case "force" -> handleForce(player, args);
            case "player" -> handlePlayer(player, label, args);
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeam(Player player, String label, String[] args) {
        // /admin team <sub> <action> <team> [valeur]
        if (args.length < 4) {
            player.sendMessage("§cUsage: /" + label + " team <chest|arena|spawn|score|door> <action> <team>");
            return false;
        }

        String sub = args[1].toLowerCase();
        String action = args[2].toLowerCase();
        Team team = findTeam(args[3]);

        if (team == null) {
            player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_team"));
            return false;
        }

        return switch (sub) {
            case "chest" -> handleTeamChest(player, action, team);
            case "arena" -> handleTeamArena(player, action, team);
            case "spawn" -> handleTeamSpawn(player, action, team);
            case "score" -> handleTeamScore(player, label, action, team, args);
            case "door" -> handleTeamDoor(player, action, team);
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleForce(Player player, String[] args) {
        // /admin force <arena|drop>
        return switch (args[1].toLowerCase()) {
            case "arena" -> {
                gameManager.getArenaManager().makeAvailableWithForce(true);
                player.sendMessage("§aSpawn du coffre d'arène forcé.");
                yield true;
            }
            case "drop" -> {
                gameManager.getDropManager().spawnDrop();
                player.sendMessage("§aSpawn du coffre de drop forcé.");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handlePlayer(Player player, String label, String[] args) {
        // /admin player <team|kit> set <valeur> <player>
        if (args.length < 5) {
            player.sendMessage("§cUsage: /" + label + " player <team|kit> set <valeur> <player>");
            return false;
        }

        String sub = args[1].toLowerCase();
        String action = args[2].toLowerCase();
        String targetPlayerName = args[4];

        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage("§cJoueur cible introuvable : " + targetPlayerName);
            return false;
        }

        if (!action.equals("set")) {
            player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
            return false;
        }

        return switch (sub) {
            case "team" -> {
                Team team = findTeam(args[3]);
                if (team == null) {
                    player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_team"));
                    yield false;
                }
                gameManager.getTeamManager().joinTeam(targetPlayer, team);
                targetPlayer.sendMessage("§aÉquipe définie à §f" + team.getDisplayName() + "§a.");
                player.sendMessage("§aTeam définie à §f" + team.getDisplayName() + "§a pour " + targetPlayerName + ".");
                yield true;
            }
            case "kit" -> {
                KitBase kit = findKit(args[3]);
                if (kit == null) {
                    player.sendMessage("§cKit introuvable : " + args[3]);
                    yield false;
                }
                gameManager.getKitManager().assignKit(targetPlayer, kit);
                targetPlayer.sendMessage("§aKit défini à §f" + kit.getDisplayName() + "§a.");
                player.sendMessage("§aKit défini à §f" + kit.getDisplayName() + "§a pour " + targetPlayerName + ".");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeamChest(Player player, String action, Team team) {
        return switch (action) {
            case "set" -> {
                Block target = player.getTargetBlockExact(6);
                if (target == null) {
                    player.sendMessage("§cAucun bloc ciblé.");
                    yield false;
                }
                gameManager.getTeamManager().setTeamChest(target.getLocation(), team);
                player.sendMessage("§aCoffre de " + team.getDisplayName() + " §adéfini en §e" + Utils.formatLocation(target.getLocation()) + "§a.");
                yield true;
            }
            case "remove" -> {
                if (gameManager.getTeamManager().getTeamChestLocation(team) == null) {
                    player.sendMessage("§cAucun coffre défini pour " + team.getDisplayName() + "§c.");
                    yield false;
                }
                gameManager.getTeamManager().removeTeamChest(team);
                player.sendMessage("§aCoffre de " + team.getDisplayName() + " §asupprimé.");
                yield true;
            }
            case "get" -> {
                Location loc = gameManager.getTeamManager().getTeamChestLocation(team);
                player.sendMessage(loc == null
                        ? "§cAucun coffre défini pour " + team.getDisplayName() + "§c."
                        : "§aCoffre de " + team.getDisplayName() + " §aen §e" + Utils.formatLocation(loc) + "§a.");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeamArena(Player player, String action, Team team) {
        return switch (action) {
            case "set" -> {
                gameManager.getTeamManager().setTeamArena(player.getLocation(), team);
                player.sendMessage("§aArène de " + team.getDisplayName() + " §adéfinie en §e" + Utils.formatLocation(player.getLocation()) + "§a.");
                yield true;
            }
            case "remove" -> {
                if (gameManager.getTeamManager().getTeamArenaLocation(team) == null) {
                    player.sendMessage("§cAucune arène définie pour " + team.getDisplayName() + "§c.");
                    yield false;
                }
                gameManager.getTeamManager().removeTeamArena(team);
                player.sendMessage("§aArène de " + team.getDisplayName() + " §asupprimée.");
                yield true;
            }
            case "get" -> {
                Location loc = gameManager.getTeamManager().getTeamArenaLocation(team);
                player.sendMessage(loc == null
                        ? "§cAucune arène définie pour " + team.getDisplayName() + "§c."
                        : "§aArène de l'équipe " + team.getDisplayName() + " §aen §e" + Utils.formatLocation(loc) + "§a.");
                yield true;
            }
            case "tp" -> {
                Location loc = gameManager.getTeamManager().getTeamArenaLocation(team);
                if (loc == null) {
                    player.sendMessage("§cAucune arène définie pour " + team.getDisplayName() + "§c.");
                    yield false;
                }
                player.teleport(loc);
                player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                player.sendMessage("§aTéléporté vers l'arène de l'équipe " + team.getDisplayName() + "§a.");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeamSpawn(Player player, String action, Team team) {
        return switch (action) {
            case "set" -> {
                gameManager.getTeamManager().setTeamSpawn(player.getLocation(), team);
                player.sendMessage("§aSpawn de " + team.getDisplayName() + " §adéfini en §e" + Utils.formatLocation(player.getLocation()) + "§a.");
                yield true;
            }
            case "remove" -> {
                gameManager.getTeamManager().removeTeamSpawn(team);
                player.sendMessage("§aSpawn de " + team.getDisplayName() + " §asupprimé.");
                yield true;
            }
            case "get" -> {
                Location loc = gameManager.getTeamManager().getTeamSpawnLocation(team);
                player.sendMessage(loc == null
                        ? "§cAucun spawn défini pour " + team.getDisplayName() + "§c."
                        : "§aSpawn de " + team.getDisplayName() + " §aen §e" + Utils.formatLocation(loc) + "§a.");
                yield true;
            }
            case "tp" -> {
                Location loc = gameManager.getTeamManager().getTeamSpawnLocation(team);
                if (loc == null) {
                    player.sendMessage("§cAucun spawn défini pour " + team.getDisplayName() + "§c.");
                    yield false;
                }
                player.teleport(loc);
                player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                player.sendMessage("§aTéléporté vers le spawn de " + team.getDisplayName() + "§a.");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeamScore(Player player, String label, String action, Team team, String[] args) {
        return switch (action) {
            case "get" -> {
                player.sendMessage("§aScore de " + team.getDisplayName() + " §a: §e" + team.getScore());
                yield true;
            }
            case "set" -> {
                if (args.length < 5) {
                    player.sendMessage("§cUsage: /" + label + " team score set <team> <valeur>");
                    yield false;
                }
                try {
                    int value = Integer.parseInt(args[4]);
                    team.setScore(value);
                    player.sendMessage("§aScore de " + team.getDisplayName() + " §adéfini à §e" + value + "§a.");
                    yield true;
                } catch (NumberFormatException e) {
                    player.sendMessage("§cValeur invalide : " + args[4]);
                    yield false;
                }
            }
            case "reset" -> {
                team.setScore(0);
                player.sendMessage("§aScore de " + team.getDisplayName() + " §areinitialisé.");
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private boolean handleTeamDoor(Player player, String action, Team team) {
        return switch (action) {
            case "set" -> {
                Block target = player.getTargetBlockExact(6);
                if (target == null) {
                    player.sendMessage("§cAucun bloc ciblé.");
                    yield false;
                }
                DoorOrientation orientation = DoorOrientation.fromPlayerFacing(player.getFacing());
                gameManager.getDoorManager().setDoor(team, target.getLocation(), orientation);
                player.sendMessage("§aPorte de " + team.getDisplayName() + " §adéfinie en §e"
                        + Utils.formatLocation(target.getLocation())
                        + " §a(§e" + orientation.name() + "§a).");
                yield true;
            }
            case "remove" -> {
                gameManager.getDoorManager().removeDoor(team);
                player.sendMessage("§aPorte de " + team.getDisplayName() + " §asupprimée.");
                yield true;
            }
            case "get" -> {
                gameManager.getDoorManager().getDoor(team).ifPresentOrElse(
                        door -> player.sendMessage("§aPorte de " + team.getDisplayName()
                                + " §aen §e" + Utils.formatLocation(door.getCenter())
                                + " §a(§e" + door.getOrientation().name() + "§a)."),
                        () -> player.sendMessage("§cAucune porte définie pour " + team.getDisplayName() + "§c.")
                );
                yield true;
            }
            default -> {
                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
                yield false;
            }
        };
    }

    private Team findTeam(String name) {
        return gameManager.getTeamManager().getTeams().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private KitBase findKit(String name) {
        return KitManager.KITS.stream()
                .filter(k -> k.getDisplayName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private List<String> filter(List<String> list, String input) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    private List<String> filterTeams(String input) {
        return gameManager.getTeamManager().getTeams().stream()
                .map(Team::getName)
                .filter(n -> n.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    private List<String> filterKits(String input) {
        return KitManager.KITS.stream()
                .map(KitBase::getDisplayName)
                .filter(n -> n.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }

    private void sendUsage(Player player, String label) {
        player.sendMessage("§cUsage:");
        player.sendMessage("§e/" + label + " team <chest|arena|spawn|score|door> <get|set|remove|tp> <team>");
        player.sendMessage("§e/" + label + " force <arena|drop>");
        player.sendMessage("§e/" + label + " player <team|kit> set <valeur>");
    }
//
//    @Override
//    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
//
//        if (args.length == 1) {
//            return commandStructure.keySet().stream()
//                    .filter(s -> s.startsWith(args[0].toLowerCase()))
//                    .toList();
//        }
//
//        if (args.length == 2) {
//            List<String> actions = commandStructure.get(args[0].toLowerCase());
//            if (actions == null) return List.of();
//
//            return actions.stream()
//                    .filter(s -> s.startsWith(args[1].toLowerCase()))
//                    .toList();
//        }
//
//        if (args.length == 3) {
//            return teamNames.stream()
//                    .filter(t -> t.toLowerCase().startsWith(args[2].toLowerCase()))
//                    .toList();
//        }
//
//        if (args.length == 4 && args[0].equalsIgnoreCase("score") && args[1].equalsIgnoreCase("set")) {
//            return List.of("<valeur>");
//        }
//
//        return List.of();
//    }
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        if (!(sender instanceof Player player)) {
//            sender.sendMessage(plugin.getConfigManager().getString("plugin.only_player_command"));
//            return false;
//        }
//        if (args.length <= 1) {
//            player.sendMessage("§cUsage: /" + label + " <chest|arena> <set|remove|get> <team>");
//            return false;
//        }
//
//        if (!commandStructure.containsKey(args[0].toLowerCase())) {
//            player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
//            return false;
//        }
//        if (!commandStructure.get(args[0].toLowerCase()).contains(args[1].toLowerCase())) {
//            player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_args"));
//            return false;
//        }
//
//        switch (args[0].toLowerCase()) {
//            case "ac" -> {
//                if (args[1].equalsIgnoreCase("force")) {
//                    player.sendMessage("§aSpawn du coffre d'arène forcé.");
//                    gameManager.getArenaManager().makeAvailableWithForce(true);
//                }
//            }
//            case "drop" -> {
//                if (args[1].equalsIgnoreCase("force")) {
//                    player.sendMessage("§aSpawn du coffre de drop forcé.");
//                    gameManager.getDropManager().spawnDrop();
//                }
//            }
//        }
//
//        if (args.length >= 3) {
//            Team team = teams.stream().filter(t -> t.getName().equalsIgnoreCase(args[2])).findFirst().orElse(null);
//            if (team == null) {
//                player.sendMessage(plugin.getConfigManager().getString("plugin.no_such_team"));
//                return false;
//            }
//
//            switch (args[0].toLowerCase()) {
//                case "chest" -> {
//                    switch (args[1].toLowerCase()) {
//                        case "set" -> {
//                            Block targetBlock = player.getTargetBlockExact(6);
//                            if (targetBlock == null) {
//                                player.sendMessage("§cAucun bloc valide trouvé");
//                                return false;
//                            }
//                            Location targetLocation = targetBlock.getLocation();
//                            player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aa été placé en §e" + Utils.formatLocation(targetLocation) + "§a.");
//                            gameManager.getTeamManager().setTeamChest(targetLocation, team);
//                        }
//                        case "remove" -> {
//                            Location chestLocation = gameManager.getTeamManager().getTeamChestLocation(team);
//                            if (chestLocation == null) {
//                                player.sendMessage("§cLe coffre de l'équipe " + team.getDisplayName() + " §cn'est pas défini.");
//                                return false;
//                            }
//                            player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aa été retiré.");
//                            gameManager.getTeamManager().removeTeamChest(team);
//                        }
//                        case "get" -> {
//                            Location chestLocation = gameManager.getTeamManager().getTeamChestLocation(team);
//                            if (chestLocation == null)
//                                player.sendMessage("§cLe coffre de l'équipe " + team.getDisplayName() + " §cn'est pas défini.");
//                            else
//                                player.sendMessage("§aLe coffre de l'équipe " + team.getDisplayName() + " §aest situé en §e" + Utils.formatLocation(chestLocation) + "§a.");
//                        }
//                    }
//                }
//                case "arena" -> {
//                    switch (args[1].toLowerCase()) {
//                        case "set" -> {
//                            Location targetLocation = player.getLocation();
//                            player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aa été placé en §e" + Utils.formatLocation(targetLocation) + "§a.");
//                            gameManager.getTeamManager().setTeamArena(targetLocation, team);
//                        }
//                        case "remove" -> {
//                            Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
//                            if (arenaLocation == null) {
//                                player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
//                                return false;
//                            }
//                            player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aa été retirée.");
//                            gameManager.getTeamManager().removeTeamArena(team);
//                        }
//                        case "get" -> {
//                            Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
//                            if (arenaLocation == null)
//                                player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
//                            else
//                                player.sendMessage("§aLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §aest située en §e" + Utils.formatLocation(arenaLocation) + "§a.");
//                        }
//                        case "tp" -> {
//                            Location arenaLocation = gameManager.getTeamManager().getTeamArenaLocation(team);
//                            if (arenaLocation == null)
//                                player.sendMessage("§cLa téléportation d'arène de l'équipe " + team.getDisplayName() + " §cn'est pas définie.");
//                            else {
//                                player.teleport(arenaLocation);
//                                player.sendMessage("§aVous avez été téléporté vers le point d'arène de l'équipe " + team.getDisplayName() + "§a.");
//                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
//                            }
//                        }
//                    }
//                }
//                case "score" -> {
//                    switch (args[1].toLowerCase()) {
//                        case "get" -> {
//                            int score = team.getScore();
//                            player.sendMessage("§aScore de l'équipe " + team.getDisplayName() + " §a: §e" + score);
//                        }
//                        case "set" -> {
//                            if (args.length < 4) {
//                                player.sendMessage("§cUsage: /" + label + " score set <team> <valeur>");
//                                return false;
//                            }
//
//                            int value;
//                            try {
//                                value = Integer.parseInt(args[3]);
//                            } catch (NumberFormatException e) {
//                                player.sendMessage("§cValeur invalide.");
//                                return false;
//                            }
//
//                            team.setScore(value);
//                            player.sendMessage("§aScore de l'équipe " + team.getDisplayName() + " §adéfini à §e" + value + "§a.");
//                        }
//                    }
//                }
//                case "door" -> {
//                    switch (args[1].toLowerCase()) {
//                        case "set" -> {
//                            DoorOrientation orientation = DoorOrientation.fromPlayerFacing(
//                                    player.getFacing()
//                            );
//                            Block target = player.getTargetBlockExact(6);
//                            if (target == null) {
//                                player.sendMessage("§cAucun bloc ciblé.");
//                                return false;
//                            }
//                            gameManager.getDoorManager().setDoor(team, target.getLocation(), orientation);
//                            player.sendMessage("§aPorte de l'équipe " + team.getDisplayName()
//                                    + " §adéfinie en §e" + Utils.formatLocation(target.getLocation())
//                                    + " §a(orientation: §e" + orientation.name() + "§a).");
//                        }
//                        case "remove" -> {
//                            gameManager.getDoorManager().removeDoor(team);
//                            player.sendMessage("§aPorte de l'équipe " + team.getDisplayName() + " §asupprimée.");
//                        }
//                        case "get" -> {
//                            gameManager.getDoorManager().getDoor(team).ifPresentOrElse(
//                                    door -> player.sendMessage("§aPorte de l'équipe " + team.getDisplayName()
//                                            + " §aen §e" + Utils.formatLocation(door.getCenter())
//                                            + " §a(§e" + door.getOrientation().name() + "§a)."),
//                                    () -> player.sendMessage("§cAucune porte définie pour " + team.getDisplayName() + "§c.")
//                            );
//                        }
//                    }
//                }
//            }
//        }
//
//        return true;
//    }
}
