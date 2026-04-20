package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.*;
import dev.valani.mineralcontest.listeners.*;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
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
        plugin.getCommand("c").setExecutor(new CommandChat(plugin, gameManager.getTeamManager()));
        plugin.getCommand("arena").setExecutor(new CommandArena(plugin, gameManager));

        CommandAdmin adminCmd = new CommandAdmin(plugin, gameManager);
        plugin.getCommand("admin").setExecutor(adminCmd);
        plugin.getCommand("admin").setTabCompleter(adminCmd);

        pm.registerEvents(new TeamSelectorListener(gameManager, gameManager.getTeamManager(), teamSelectorMenu), plugin);
        pm.registerEvents(new ArenaChestListener(plugin, gameManager.getArenaManager()), plugin);
        pm.registerEvents(new KitSelectorListener(gameManager, gameManager.getKitManager(), kitSelectorMenu), plugin);
        pm.registerEvents(new ChatListener(gameManager.getTeamManager()), plugin);
        pm.registerEvents(new PreGameListener(gameManager), plugin);
        pm.registerEvents(new PlayerTakeDamageListener(gameManager.getTeamManager()), plugin);
        pm.registerEvents(new EnchantmentTableListener(), plugin);
        pm.registerEvents(new PlayerJoinListener(), plugin);
        pm.registerEvents(new TeamChestListener(gameManager), plugin);

        plugin.getCommand("mineralcontest").setExecutor(new CommandMineralContest(plugin, gameManager));
        plugin.getCommand("start").setExecutor(new CommandStart(plugin, gameManager));
        plugin.getCommand("end").setExecutor(new CommandEnd(plugin, gameManager));
        plugin.getCommand("reset").setExecutor(new CommandReset(plugin, gameManager));
//        pm.registerEvents(new PlayerDeathListener(plugin, gameManager, gameManager.getTeamManager(), gameManager.getKitManager()), plugin);
        pm.registerEvents(new MinerKitListener(gameManager.getKitManager()), plugin);
    }

}
