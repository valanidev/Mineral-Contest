package dev.valani.mineralcontest.commands;

import dev.valani.mineralcontest.Main;
import dev.valani.mineralcontest.managers.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandGenerate extends PlayerOnlyCommand {

    private final SchematicManager schematicManager;

    public CommandGenerate(Main plugin, SchematicManager schematicManager) {
        super(plugin);
        this.schematicManager = schematicManager;
    }

    @Override
    protected boolean onPlayerCommand(Player player, Command cmd, String label, String[] args) {
        Bukkit.broadcastMessage("§aGénération de l'arène en cours, veuillez patienter...");

        schematicManager.generate(world -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(world.getSpawnLocation());
            }
            Bukkit.broadcastMessage("§aL'arène est prête !");
        });

        return true;
    }
}