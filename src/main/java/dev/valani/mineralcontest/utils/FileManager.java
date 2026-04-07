package dev.valani.mineralcontest.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    /**
     * Crée un fichier YAML dans le dossier du plugin.
     * @param plugin Instance du plugin
     * @param fileName Nom du fichier, ex: "arena.yml"
     */
    public FileManager(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        createFile(fileName);
    }

    private void createFile(String fileName) {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // crée le dossier plugin si absent
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    /** Retourne le FileConfiguration pour lire ou écrire dedans */
    public FileConfiguration getConfig() {
        return config;
    }

    /** Sauvegarde le fichier */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Recharge le fichier depuis le disque */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /** Supprime le fichier */
    public void delete() {
        if (file.exists()) {
            file.delete();
        }
    }

    /** Retourne le fichier physique */
    public File getFile() {
        return file;
    }
}