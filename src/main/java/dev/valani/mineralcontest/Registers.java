package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.commands.CommandMineralContest;
import dev.valani.mineralcontest.listeners.ArenaChestListener;
import dev.valani.mineralcontest.listeners.PlayerJoinListener;
import dev.valani.mineralcontest.listeners.TeamSelectorListener;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class Registers {

    private final Main plugin;
    private final PluginManager pm;

    public Registers(Main plugin) {
        this.plugin = plugin;
        this.pm = Bukkit.getPluginManager();
        GameManager gameManager = new GameManager(plugin);

        TeamSelectorMenu selectorMenu = new TeamSelectorMenu(gameManager);
        FileManager arenaFile = new FileManager(plugin, "arena.yml");

        CommandArenaChest arenaChestCmd = new CommandArenaChest(plugin, arenaFile);
        CommandMineralContest mineralContestCmd = new CommandMineralContest(plugin, gameManager, selectorMenu);
        plugin.getCommand("mineralcontest").setExecutor(mineralContestCmd);
        plugin.getCommand("arenachest").setExecutor(arenaChestCmd);

        pm.registerEvents(new PlayerJoinListener(), plugin);
        pm.registerEvents(new ArenaChestListener(plugin, arenaChestCmd), plugin);
        pm.registerEvents(new TeamSelectorListener(gameManager, selectorMenu), plugin);
    }

}
