package dev.valani.mineralcontest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        print(getString("plugin.enabled"));

        new Registers(this);
    }

    @Override
    public void onDisable() {
        print(getString("plugin.disabled"));
    }

    public static Main getPlugin() {
        return plugin;
    }

    public void print(String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + message);
    }

    public String getPrefix() {
        return getString("plugin.prefix");
    }

    public String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key, "Unknown"));
    }

    public int getInt(String key) {
        return getConfig().getInt(key);
    }

    public List<String> getStringList(String key) {
        return getConfig().getStringList(key).stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
    }
}
