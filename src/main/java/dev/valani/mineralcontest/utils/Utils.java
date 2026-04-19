package dev.valani.mineralcontest.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

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
}
