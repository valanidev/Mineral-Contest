package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import org.bukkit.ChatColor;

public class ConfigManager {

    private final Main plugin = Main.getInstance();

    public String getString(String key) {
        String value = plugin.getConfig().getString(key);
        if (value == null) return "Missing: " + key;
        return ChatColor.translateAlternateColorCodes('&', value);
    }

}
