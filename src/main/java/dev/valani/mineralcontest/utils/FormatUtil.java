package dev.valani.mineralcontest.utils;

import org.bukkit.Location;

public class FormatUtil {

    public static String formatTimeLong(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "0 secondes";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours)
                    .append(" heure")
                    .append(hours > 1 ? "s" : "");
        }
        if (minutes > 0) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(minutes)
                    .append(" minute")
                    .append(minutes > 1 ? "s" : "");
        }
        if (seconds > 0) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(seconds)
                    .append(" seconde")
                    .append(seconds > 1 ? "s" : "");
        }

        return sb.toString();
    }

    public static String formatTimeShort(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "00:00";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%02d:%02d", minutes, seconds);
    }

    public static String formatLocationShort(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static String formatLocationLong(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " (" + location.getYaw() + ", " + location.getPitch() + ")";
    }

}
