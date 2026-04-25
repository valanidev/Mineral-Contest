package dev.valani.mineralcontest.utils;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.game.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Objects;

public class Utils {

    public static ChatColor parseChatColor(String value) {
        try {
            return ChatColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage("§cCouleur invalide en config : " + value);
            return ChatColor.WHITE;
        }
    }

    public static Material parseMaterial(String value) {
        Material mat = Material.getMaterial(value.toUpperCase());
        if (mat == null) {
            Bukkit.getConsoleSender().sendMessage("§cMatériau invalide en config : " + value);
            return Material.WHITE_WOOL;
        }
        return mat;
    }

    public static String formatLocation(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                + " (" + Objects.requireNonNull(loc.getWorld()).getName() + ")";
    }

    public static int roundToMultipleOf9(int n) {
        return (int) Math.ceil(Math.max(n, 1) / 9.0) * 9;
    }

    public static Color translateChatColorToColor(ChatColor chatColor) {
        return switch (chatColor) {
            case AQUA -> Color.AQUA;
            case BLACK -> Color.BLACK;
            case BLUE, DARK_AQUA, DARK_BLUE -> Color.BLUE;
            case DARK_GRAY, GRAY -> Color.GRAY;
            case DARK_GREEN, GREEN -> Color.GREEN;
            case DARK_PURPLE, LIGHT_PURPLE -> Color.PURPLE;
            case DARK_RED, RED -> Color.RED;
            case GOLD, YELLOW -> Color.YELLOW;
            case WHITE -> Color.WHITE;
            default -> null;
        };
    }

    public static String formatTime(int seconds) {
        if (seconds >= 60) {
            int minutes = seconds / 60;
            int sec = seconds % 60;

            if (sec == 0) {
                return minutes + " minute" + (minutes > 1 ? "s" : "");
            }
            return minutes + "m " + sec + "s";
        }
        return seconds + " seconde" + (seconds > 1 ? "s" : "");
    }

    public static String formatSbTimer(int seconds) {
        if (seconds < 0) seconds = 0;

        int minutes = seconds / 60;
        int sec = seconds % 60;

        return String.format("%02d:%02d", minutes, sec);
    }

//    public static Team getTeam(Player player) {
//        return Main.getInstance()
//                .getGameManager()
//                .getTeamManager()
//                .getPlayerTeam(player)
//                .orElse(null);
//    }

    public static ItemStack buildHelmet(Team team) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        if (team == null) return helmet;
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        if (meta == null) return helmet;
        meta.setColor(Utils.translateChatColorToColor(team.getColor()));
        helmet.setItemMeta(meta);
        return helmet;
    }

    public static ItemStack createTeamHelmet(Player player) {
        // TODO: fix team getter
//        Team team = Utils.getTeam(player);
        Team team = null;
        if (team == null) return new ItemStack(Material.LEATHER_HELMET);
        return Utils.buildHelmet(team);
    }

    public static void applyItems(Player player) {
        ItemStack helmet = createTeamHelmet(player);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 16);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(8, food);
    }

    public static void playSoundForAll(Sound sound, float volume, float pitch) {
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), sound, volume, pitch));
    }

    public static void consoleDebug(String message) {
        Bukkit.getConsoleSender().sendMessage("§1| §3DEBUG §b→ §r" + message);
    }

    public static void consoleError(String message) {
        Bukkit.getConsoleSender().sendMessage("§8| §4ERROR §c→ §4" + message);
    }
}
