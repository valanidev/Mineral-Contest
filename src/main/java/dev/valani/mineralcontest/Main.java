package dev.valani.mineralcontest;

import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private String prefix;
    private Registers registers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        prefix = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("plugin.prefix", "[MineralContest] "));
        Bukkit.getConsoleSender().sendMessage(getPrefix() + getString("plugin.enabled"));

        registers = new Registers(this);
    }

    @Override
    public void onDisable() {
//        if (registers != null) registers.getKitManager().resetAll();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + getString("plugin.disabled"));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getString(String key) {
        String value = getConfig().getString(key);
        if (value == null) {
            Bukkit.getConsoleSender().sendMessage("§cNo config value for " + key);
            return "§cMissing key '" + key + "'...";
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public int getInt(String key) {
        return getConfig().getInt(key);
    }

    public List<String> getStringList(String key) {
        return getConfig().getStringList(key).stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .toList();
    }
}
