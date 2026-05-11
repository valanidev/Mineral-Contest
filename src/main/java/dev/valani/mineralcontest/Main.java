package dev.valani.mineralcontest;

import dev.valani.mineralcontest.managers.ConfigManager;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager(this);

        new Registers(this);

        LogUtil.log(configManager.getString("messages.enabled"));
    }

    @Override
    public void onDisable() {
        gameManager.resetGame();
        LogUtil.log(configManager.getString("messages.disabled"));
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}