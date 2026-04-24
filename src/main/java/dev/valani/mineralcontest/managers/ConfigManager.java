package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import org.bukkit.ChatColor;

public class ConfigManager {

    private final Main plugin;

    private final String prefix;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("plugin.prefix", "[MineralContest] "));
    }

    public String getString(String key) {
        String value = plugin.getConfig().getString(key);
        if (value == null) return "Missing: " + key;
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String getStringWithPrefix(String key) {
        return prefix + getString(key);
    }

    public int getInt(String key) {
        return plugin.getConfig().getInt(key);
    }

    public String getPrefix() {
        return prefix;
    }

}
