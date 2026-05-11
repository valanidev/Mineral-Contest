package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.DoorOrientation;
import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.TeamDoor;
import dev.valani.mineralcontest.managers.TeamLocationManager;
import dev.valani.mineralcontest.utils.FormatUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SetupTeamCommand extends PlayerOnlyCommand implements TabCompleter {

    private static final List<String> ACTIONS = List.of("set", "get", "remove", "tp");
    private static final List<String> TYPES   = List.of("chest", "spawn", "arena", "door");

    private final TeamLocationManager locationManager;

    public SetupTeamCommand(Main plugin) {
        super(plugin);
        this.locationManager = plugin.getGameManager().getTeamManager().getTeamLocationManager();
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        if (args.length < 3) {
            sendUsage(player, label);
            return false;
        }

        Team team = resolveTeam(player, args[0]);
        if (team == null) return false;

        String action = args[1].toLowerCase();
        String type   = args[2].toLowerCase();

        return switch (action) {
            case "set"    -> handleSet(player, label, team, type);
            case "get"    -> handleGet(player, label, team, type);
            case "remove" -> handleRemove(player, label, team, type);
            case "tp" -> handleTp(player, label, team, type);
            default       -> { sendUsage(player, label); yield false; }
        };
    }

    private boolean handleTp(Player player, String label, Team team, String type) {
        return switch (type) {
            case "chest" -> {
                Location loc = locationManager.get(team, TeamLocationManager.LocationType.CHEST);
                if (loc == null) {
                    player.sendMessage("§cLe coffre de l'équipe " + team.getDisplayName() + " n'est pas défini.");
                    yield false;
                }
                player.teleport(loc);
                player.sendMessage("§aVous avez été téléporté au coffre de l'équipe " + team.getDisplayName() + ".");
                yield true;
            }
            case "spawn" -> {
                Location location = locationManager.get(team, TeamLocationManager.LocationType.SPAWN);
                if (location == null) {
                    player.sendMessage("§cLe spawn de l'équipe " + team.getDisplayName() + " n'est pas défini.");
                    yield false;
                }
                player.teleport(location);
                player.sendMessage("§aVous avez été téléporté au spawn de l'équipe " + team.getDisplayName() + ".");
                yield true;
            }
            case "arena" -> {
                Location location = locationManager.get(team, TeamLocationManager.LocationType.ARENA);
                if (location == null) {
                    player.sendMessage("§cL'arena de l'équipe " + team.getDisplayName() + " n'est pas définie.");
                    yield false;
                }
                player.teleport(location);
                player.sendMessage("§aVous avez été téléporté à l'arène de l'équipe " + team.getDisplayName() + ".");
                yield true;
            }
            case "door" -> {
                Location location = locationManager.getDoor(team).map(TeamDoor::getCenter).orElse(null);
                if (location == null) {
                    player.sendMessage("§cLa porte de l'équipe " + team.getDisplayName() + " n'est pas définie.");
                    yield false;
                }
                player.teleport(location);
                player.sendMessage("§aVous avez été téléporté à la porte de l'équipe " + team.getDisplayName() + ".");
                yield true;
            }
            default -> { sendUsage(player, label); yield false; }
        };
    }

    private boolean handleSet(Player player, String label, Team team, String type) {
        return switch (type) {
            case "chest" -> {
                Block block = player.getTargetBlockExact(6);
                if (block == null) {
                    player.sendMessage("§cVous devez regarder un bloc pour définir le coffre.");
                    yield false;
                }
                locationManager.set(team, TeamLocationManager.LocationType.CHEST, block.getLocation());
                player.sendMessage("§aCoffre de l'équipe " + team.getDisplayName() + " §adéfini en §e"
                        + FormatUtil.formatLocationShort(block.getLocation()) + "§a.");
                yield true;
            }
            case "spawn" -> {
                locationManager.set(team, TeamLocationManager.LocationType.SPAWN, player.getLocation());
                player.sendMessage("§aSpawn de l'équipe " + team.getDisplayName() + " §adéfini en §e"
                        + FormatUtil.formatLocationLong(player.getLocation()) + "§a.");
                yield true;
            }
            case "arena" -> {
                locationManager.set(team, TeamLocationManager.LocationType.ARENA, player.getLocation());
                player.sendMessage("§aArène de l'équipe " + team.getDisplayName() + " §adéfinie en §e"
                        + FormatUtil.formatLocationLong(player.getLocation()) + "§a.");
                yield true;
            }
            case "door" -> {
                Block block = player.getTargetBlockExact(6);
                if (block == null) {
                    player.sendMessage("§cVous devez regarder le bloc central de la porte.");
                    yield false;
                }
                DoorOrientation orientation = DoorOrientation.fromPlayerFacing(player.getFacing());
                locationManager.setDoor(team, block.getLocation(), orientation);
                player.sendMessage("§aPorte de l'équipe " + team.getDisplayName() + " §adéfinie en §e"
                        + FormatUtil.formatLocationShort(block.getLocation())
                        + " §a(orientation: §e" + orientation.name() + "§a).");
                yield true;
            }
            default -> { sendUsage(player, label); yield false; }
        };
    }

    private boolean handleGet(Player player, String label, Team team, String type) {
        return switch (type) {
            case "chest" -> {
                Location loc = locationManager.get(team, TeamLocationManager.LocationType.CHEST);
                if (loc == null) { player.sendMessage("§cAucun coffre défini pour l'équipe " + team.getDisplayName() + "§c."); yield false; }
                player.sendMessage("§aCoffre de l'équipe " + team.getDisplayName() + " §aen "
                        + team.getColor() + FormatUtil.formatLocationShort(loc));
                yield true;
            }
            case "spawn" -> {
                Location loc = locationManager.get(team, TeamLocationManager.LocationType.SPAWN);
                if (loc == null) { player.sendMessage("§cAucun spawn défini pour l'équipe " + team.getDisplayName() + "§c."); yield false; }
                player.sendMessage("§aSpawn de l'équipe " + team.getDisplayName() + " §aen "
                        + team.getColor() + FormatUtil.formatLocationLong(loc));
                yield true;
            }
            case "arena" -> {
                Location loc = locationManager.get(team, TeamLocationManager.LocationType.ARENA);
                if (loc == null) { player.sendMessage("§cAucune arène définie pour l'équipe " + team.getDisplayName() + "§c."); yield false; }
                player.sendMessage("§aArène de l'équipe " + team.getDisplayName() + " §aen "
                        + team.getColor() + FormatUtil.formatLocationLong(loc));
                yield true;
            }
            case "door" -> {
                locationManager.getDoor(team).ifPresentOrElse(
                        door -> player.sendMessage("§aPorte de l'équipe " + team.getDisplayName() + " §aen §e"
                                + FormatUtil.formatLocationShort(door.getCenter())
                                + " §a(§e" + door.getOrientation().name() + "§a)."),
                        () -> player.sendMessage("§cAucune porte définie pour l'équipe " + team.getDisplayName() + "§c.")
                );
                yield true;
            }
            default -> { sendUsage(player, label); yield false; }
        };
    }

    private boolean handleRemove(Player player, String label, Team team, String type) {
        return switch (type) {
            case "chest" -> {
                locationManager.remove(team, TeamLocationManager.LocationType.CHEST);
                player.sendMessage("§aCoffre de l'équipe " + team.getDisplayName() + " §asupprimé.");
                yield true;
            }
            case "spawn" -> {
                locationManager.remove(team, TeamLocationManager.LocationType.SPAWN);
                player.sendMessage("§aSpawn de l'équipe " + team.getDisplayName() + " §asupprimé.");
                yield true;
            }
            case "arena" -> {
                locationManager.remove(team, TeamLocationManager.LocationType.ARENA);
                player.sendMessage("§aArène de l'équipe " + team.getDisplayName() + " §asupprimée.");
                yield true;
            }
            case "door" -> {
                locationManager.removeDoor(team);
                player.sendMessage("§aPorte de l'équipe " + team.getDisplayName() + " §asupprimée.");
                yield true;
            }
            default -> { sendUsage(player, label); yield false; }
        };
    }

    private Team resolveTeam(Player player, String name) {
        return plugin.getGameManager().getTeamManager().getTeams().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    player.sendMessage("§cL'équipe §e" + name + " §cn'existe pas.");
                    return null;
                });
    }

    private void sendUsage(Player player, String label) {
        player.sendMessage("§cUsage: §e/" + label + " <team> <set|get|remove> <chest|spawn|arena|door>");
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        return switch (args.length) {
            case 1 -> plugin.getGameManager().getTeamManager().getTeams().stream()
                    .map(Team::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
            case 2 -> ACTIONS.stream()
                    .filter(a -> a.startsWith(args[1].toLowerCase())).toList();
            case 3 -> TYPES.stream()
                    .filter(t -> t.startsWith(args[2].toLowerCase())).toList();
            default -> List.of();
        };
    }
}