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
        GameManager gameManager = plugin.getGameManager();

        TeamSelectorMenu teamSelectorMenu = new TeamSelectorMenu(gameManager.getTeamManager());
        KitSelectorMenu kitSelectorMenu = new KitSelectorMenu(gameManager.getKitManager());

        plugin.getCommand("arenachest").setExecutor(new CommandArenaChest(plugin));
        plugin.getCommand("team").setExecutor(new CommandTeam(plugin, teamSelectorMenu));
        plugin.getCommand("kit").setExecutor(new CommandKit(plugin, kitSelectorMenu));
        plugin.getCommand("c").setExecutor(new CommandChat(plugin));
        plugin.getCommand("arena").setExecutor(new CommandArena(plugin));

        CommandAdmin adminCmd = new CommandAdmin(plugin);
        plugin.getCommand("admin").setExecutor(adminCmd);
        plugin.getCommand("admin").setTabCompleter(adminCmd);

        plugin.getCommand("mineralcontest").setExecutor(new CommandMineralContest(plugin));
        plugin.getCommand("start").setExecutor(new CommandStart(plugin));
        plugin.getCommand("end").setExecutor(new CommandEnd(plugin));
        plugin.getCommand("reset").setExecutor(new CommandReset(plugin));

        pm.registerEvents(new TeamSelectorListener(plugin, teamSelectorMenu), plugin);
        pm.registerEvents(new ArenaChestListener(plugin), plugin);
        pm.registerEvents(new KitSelectorListener(plugin, kitSelectorMenu), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new PreGameListener(gameManager), plugin);
        pm.registerEvents(new PlayerTakeDamageListener(gameManager.getTeamManager()), plugin);
        pm.registerEvents(new EnchantmentTableListener(), plugin);
        pm.registerEvents(new PlayerJoinListener(gameManager), plugin);
        pm.registerEvents(new PlayerQuitListener(gameManager), plugin);
        pm.registerEvents(new TeamChestListener(gameManager), plugin);
        pm.registerEvents(new DoorListener(gameManager, gameManager.getTeamManager()), plugin);
        pm.registerEvents(new VillagerRightClickListener(gameManager), plugin);
        pm.registerEvents(new PlayerDeathListener(plugin), plugin);
        pm.registerEvents(new MinerKitListener(gameManager.getKitManager()), plugin);
    }

}
