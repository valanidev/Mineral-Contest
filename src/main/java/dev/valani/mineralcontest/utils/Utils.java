package dev.valani.mineralcontest.utils;

import org.bukkit.*;

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
        return formatLocation(loc, false);
    }

    public static String formatLocation(Location loc, boolean withYawPitch) {
        if (withYawPitch)
            return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                    + " (" + Objects.requireNonNull(loc.getWorld()).getName() + ") "
                    + "| " + loc.getYaw() + ", " + loc.getPitch();
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
            case BLUE -> Color.BLUE;
            case DARK_AQUA -> Color.BLUE;
            case DARK_BLUE -> Color.BLUE;
            case DARK_GRAY -> Color.GRAY;
            case DARK_GREEN -> Color.GREEN;
            case DARK_PURPLE -> Color.PURPLE;
            case DARK_RED -> Color.RED;
            case GOLD -> Color.YELLOW;
            case GRAY -> Color.GRAY;
            case GREEN -> Color.GREEN;
            case LIGHT_PURPLE -> Color.PURPLE;
            case RED -> Color.RED;
            case WHITE -> Color.WHITE;
            case YELLOW -> Color.YELLOW;
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
}
