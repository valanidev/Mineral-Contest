package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManager {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    public FileManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        createFile(fileName);
    }

    private void createFile(String fileName) {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtil.error("Erreur FileManager (" + file.getName() + ") : " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            LogUtil.error("Erreur FileManager (" + file.getName() + ") : " + e.getMessage());
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void delete() {
        if (file.exists()) {
            file.delete();
        }
    }

    public File getFile() {
        return file;
    }
}