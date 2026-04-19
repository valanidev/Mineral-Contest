package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.CommandArenaChest;
import dev.valani.mineralcontest.commands.CommandKit;
import dev.valani.mineralcontest.commands.CommandMineralContest;
import dev.valani.mineralcontest.commands.CommandTeam;
import dev.valani.mineralcontest.listeners.*;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.KitManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class Registers {

    public Registers(Main plugin) {
        PluginManager pm = Bukkit.getPluginManager();

        GameManager gameManager = new GameManager(plugin);

        TeamSelectorMenu teamSelectorMenu = new TeamSelectorMenu(gameManager.getTeamManager());
        KitSelectorMenu kitSelectorMenu = new KitSelectorMenu(gameManager.getKitManager());

        plugin.getCommand("arenachest").setExecutor(new CommandArenaChest(plugin, gameManager.getArenaManager()));
        plugin.getCommand("team").setExecutor(new CommandTeam(plugin, teamSelectorMenu, gameManager));
        plugin.getCommand("kit").setExecutor(new CommandKit(plugin, kitSelectorMenu, gameManager));

        pm.registerEvents(new TeamSelectorListener(gameManager, gameManager.getTeamManager(), teamSelectorMenu), plugin);
        pm.registerEvents(new ArenaChestListener(plugin, gameManager.getArenaManager()), plugin);
        pm.registerEvents(new KitSelectorListener(gameManager, gameManager.getKitManager(), kitSelectorMenu), plugin);

//        plugin.getCommand("mineralcontest").setExecutor(new CommandMineralContest(plugin, gameManager));
//
//        pm.registerEvents(new PlayerTakeDamageListener(gameManager, gameManager.getTeamManager()), plugin);
//        pm.registerEvents(new PlayerDeathListener(plugin, gameManager, gameManager.getTeamManager(), kitManager), plugin);
//
//        pm.registerEvents(new ChatListener(gameManager.getTeamManager()), plugin);
//        pm.registerEvents(new MinerKitListener(kitManager), plugin);
    }

}
