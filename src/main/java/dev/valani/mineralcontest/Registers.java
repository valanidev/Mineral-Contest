package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.*;
import dev.valani.mineralcontest.listeners.*;
import dev.valani.mineralcontest.managers.GameManager;
import dev.valani.mineralcontest.managers.SchematicManager;
import dev.valani.mineralcontest.menus.KitSelectorMenu;
import dev.valani.mineralcontest.menus.TeamSelectorMenu;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;

public class Registers {

    public Registers(Main plugin, GameManager gameManager) {
        PluginManager pm = Bukkit.getPluginManager();

        SchematicManager schematicManager = new SchematicManager(plugin);
        plugin.saveResource("arena.schem", false);
        registerCommand("generate", plugin, new CommandGenerate(plugin, schematicManager));

        registerCommand("c", plugin, new CommandChat(plugin, gameManager));
        registerCommand("team", plugin, new CommandTeam(plugin, gameManager));
        registerCommand("kit", plugin, new CommandKit(plugin, gameManager));
        registerCommand("arena", plugin, new CommandArena(plugin));
        registerCommand("arenachest", plugin, new CommandArenaChest(plugin));
        registerCommand("admin", plugin, new CommandAdmin(plugin));
        registerCommand("mineralcontest", plugin, new CommandMineralContest(plugin));
        registerCommand("start", plugin, new CommandStart(plugin));
        registerCommand("end", plugin, new CommandEnd(plugin));
        registerCommand("reset", plugin, new CommandReset(plugin));

        pm.registerEvents(new TeamSelectorListener(plugin, gameManager), plugin);
        pm.registerEvents(new ArenaChestListener(plugin), plugin);
        pm.registerEvents(new KitSelectorListener(plugin, gameManager), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new PreGameListener(gameManager), plugin);
        pm.registerEvents(new PlayerTakeDamageListener(gameManager), plugin);
        pm.registerEvents(new EnchantmentTableListener(), plugin);
        pm.registerEvents(new PlayerJoinListener(gameManager), plugin);
        pm.registerEvents(new PlayerQuitListener(gameManager), plugin);
        pm.registerEvents(new TeamChestListener(gameManager), plugin);
        pm.registerEvents(new DoorListener(gameManager), plugin);
        pm.registerEvents(new VillagerRightClickListener(gameManager), plugin);
        pm.registerEvents(new PlayerDeathListener(plugin), plugin);
        pm.registerEvents(new MinerKitListener(gameManager.getKitManager()), plugin);
    }

    private void registerCommand(String name, Main plugin, Object executor) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd == null) {
            Utils.consoleError("Command /" + name + " not found in plugin.yml");
            return;
        }

        if (executor instanceof CommandExecutor ce) cmd.setExecutor(ce);
        if (executor instanceof TabCompleter tc) cmd.setTabCompleter(tc);
    }

}
