package dev.valani.mineralcontest.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ParseUtil {

    public static ChatColor parseChatColor(String value) {
        try {
            return ChatColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            LogUtil.error("ChatColor invalide en config : " + value);
            return ChatColor.WHITE;
        }
    }

    public static Material parseMaterial(String value) {
        Material mat = Material.getMaterial(value.toUpperCase());
        if (mat == null) {
            LogUtil.error("Matériau invalide en config : " + value);
            return Material.WHITE_WOOL;
        }
        return mat;
    }

}
