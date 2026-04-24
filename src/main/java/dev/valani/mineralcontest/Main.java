package dev.valani.mineralcontest;

import dev.valani.mineralcontest.managers.ConfigManager;
import dev.valani.mineralcontest.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager(this, configManager);
        new Registers(this, gameManager);

        Bukkit.getConsoleSender().sendMessage(configManager.getStringWithPrefix("plugin.enabled"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(configManager.getStringWithPrefix("plugin.disabled"));
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
