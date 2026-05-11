package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigManager {

    private final Main plugin;
    private final String prefix;

    // --- Cache ---
    private final boolean showNametag;
    private final boolean allowFriendlyFire;
    private final boolean showNameInTab;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.prefix = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.prefix", "[MineralContest]"));
        this.showNametag      = plugin.getConfig().getBoolean("settings.teams.show_nametag_prefix");
        this.allowFriendlyFire = plugin.getConfig().getBoolean("settings.teams.allow_friendly_fire");
        this.showNameInTab    = plugin.getConfig().getBoolean("settings.teams.show_name_in_tab");
    }

    public String getString(String key) {
        return getString(key, "Missing: " + key);
    }

    public String getString(String key, String defaultValue) {
        String value = plugin.getConfig().getString(key);
        if (value == null) return defaultValue;
        return ChatColor.translateAlternateColorCodes('&', value.replace("%prefix%", prefix));
    }

    public int getInt(String key) {
        return plugin.getConfig().getInt(key);
    }

    public boolean getBoolean(String key) {
        return plugin.getConfig().getBoolean(key);
    }

    public List<Integer> getIntList(String key) {
        return plugin.getConfig().getIntegerList(key);
    }

    public ConfigurationSection getConfigurationSection(String key) {
        return plugin.getConfig().getConfigurationSection(key);
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isShowNametag() { return showNametag; }
    public boolean isAllowFriendlyFire() { return allowFriendlyFire; }
    public boolean isShowNameInTab() { return showNameInTab; }

}
