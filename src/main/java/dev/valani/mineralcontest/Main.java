package dev.valani.mineralcontest;

import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private static Main instance;
    private String prefix;
    private Registers registers;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        prefix = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("plugin.prefix", "[MineralContest] "));
        Bukkit.getConsoleSender().sendMessage(getPrefix() + getString("plugin.enabled"));

        gameManager = new GameManager(this);
        registers = new Registers(this);
    }

    @Override
    public void onDisable() {
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


    public static Main getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
