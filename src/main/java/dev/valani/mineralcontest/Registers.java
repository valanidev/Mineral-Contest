package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.commands.CommandMineralContest;
import dev.valani.mineralcontest.listeners.ArenaChestListener;
import dev.valani.mineralcontest.listeners.PlayerJoinListener;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class Registers {

    private final Main plugin;
    private final PluginManager pm;

    private final CommandArenaChest arenaChestCmd;

    public Registers(Main plugin) {
        this.plugin = plugin;
        this.pm = Bukkit.getPluginManager();
        FileManager arenaFile = new FileManager(plugin, "arena.yml");
        this.arenaChestCmd = new CommandArenaChest(plugin, arenaFile);

        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        plugin.getCommand("mineralcontest").setExecutor(new CommandMineralContest());
        plugin.getCommand("arenachest").setExecutor(arenaChestCmd);
    }

    private void registerEvents() {
        pm.registerEvents(new PlayerJoinListener(), plugin);
        pm.registerEvents(new ArenaChestListener(plugin, arenaChestCmd), plugin);
    }

}
