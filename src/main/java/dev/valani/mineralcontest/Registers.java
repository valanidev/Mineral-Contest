package dev.valani.mineralcontest;

import dev.valani.mineralcontest.commands.*;
import dev.valani.mineralcontest.listeners.*;
import dev.valani.mineralcontest.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;

public class Registers {

    private final Main plugin;

    public Registers(Main plugin) {
        this.plugin = plugin;
        StartEndResetCommand startEndResetCommand = new StartEndResetCommand(plugin);
        registerCommand("start", startEndResetCommand);
        registerCommand("end", startEndResetCommand);
        registerCommand("reset", startEndResetCommand);
        registerCommand("team", new TeamCommand(plugin));
        registerCommand("kit", new KitCommand(plugin));
        registerCommand("drop", new CommandDrop(plugin));
        registerCommand("t", new CommandTeamChat(plugin));
        registerCommand("ac", new ArenaChestCommand(plugin));
        registerCommand("arena", new ArenaCommand(plugin));
        registerCommand("setupteam", new SetupTeamCommand(plugin));
        registerCommand("setupkit", new SetupKitCommand(plugin));

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoinListener(plugin.getGameManager().getSbManager()), plugin);
        pm.registerEvents(new PlayerQuitListener(plugin.getGameManager().getSbManager()), plugin);
        pm.registerEvents(new TeamSelectorListener(plugin), plugin);
        pm.registerEvents(new KitSelectorListener(plugin), plugin);
        pm.registerEvents(new KitMinerListener(plugin.getGameManager().getKitManager()), plugin);
        pm.registerEvents(new KitStealerListener(plugin), plugin);
        pm.registerEvents(new KitWorkerListener(plugin), plugin);
        pm.registerEvents(new KitLuckyListener(plugin), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new EnchantmentTableListener(), plugin);
        pm.registerEvents(new ArenaChestListener(plugin), plugin);
        pm.registerEvents(new DoorListener(plugin), plugin);
        pm.registerEvents(new TeamChestListener(plugin), plugin);
        pm.registerEvents(new PlayerDeathListener(plugin), plugin);
        pm.registerEvents(new DropChestListener(plugin), plugin);
    }

    private void registerCommand(String name, Object executor) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd == null) {
            LogUtil.error("Commande /" + name + " absente dans plugin.yml");
            return;
        }

        if (executor instanceof CommandExecutor ce) cmd.setExecutor(ce);
        if (executor instanceof TabCompleter tc) cmd.setTabCompleter(tc);
    }
}
