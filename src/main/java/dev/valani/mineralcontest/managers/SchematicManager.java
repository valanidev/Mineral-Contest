package dev.valani.mineralcontest.managers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.valani.mineralcontest.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

public class SchematicManager {

    private final Main plugin;
    private final String WORLD_NAME;
    private static final String SCHEMATIC_NAME = "arena.schem";

    // Position X/Z du collage — centre de l'arène
    private final int PASTE_X;
    private final int PASTE_Z;

    private World arenaWorld = null;

    public SchematicManager(Main plugin) {
        this.plugin = plugin;
        this.WORLD_NAME = plugin.getConfigManager().getString("world.name");
        this.PASTE_X = plugin.getConfigManager().getInt("world.default_spawn.x");
        this.PASTE_Z = plugin.getConfigManager().getInt("world.default_spawn.z");
    }

    public void generate(Consumer<World> callback) {
        deleteWorldIfExists();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getLogger().info("[SchematicManager] Création du monde...");
                World world = createWorld();
                if (world == null) {
                    plugin.getLogger().severe("[SchematicManager] Monde null après création.");
                    return;
                }
                plugin.getLogger().info("[SchematicManager] Monde créé : " + world.getName());

                int pasteY = world.getHighestBlockYAt(PASTE_X, PASTE_Z);
                plugin.getLogger().info("[SchematicManager] Hauteur de collage : Y=" + pasteY);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        plugin.getLogger().info("[SchematicManager] Collage du schematic...");
                        pasteSchematic(world, pasteY);
                        plugin.getLogger().info("[SchematicManager] Schematic collé.");

                        world.setSpawnLocation(PASTE_X, pasteY + 1, PASTE_Z);
                        arenaWorld = world;
                        callback.accept(world);
                    } catch (Exception e) {
                        plugin.getLogger().severe("[SchematicManager] Erreur au collage : " + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                plugin.getLogger().severe("[SchematicManager] Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Crée un monde classique sans structures
    private World createWorld() throws InterruptedException {
        World[] result = {null};

        Bukkit.getScheduler().runTask(plugin, () -> {
            WorldCreator creator = new WorldCreator(WORLD_NAME);
            creator.type(WorldType.NORMAL);
            creator.generateStructures(false);
            result[0] = creator.createWorld();
        });

        // Attend la création (max 30 secondes)
        int attempts = 0;
        while (result[0] == null && attempts < 600) {
            Thread.sleep(50);
            attempts++;
        }

        return result[0];
    }

    private void pasteSchematic(World world, int y) throws Exception {
        File file = new File(plugin.getDataFolder(), SCHEMATIC_NAME);
        if (!file.exists()) {
            plugin.getLogger().severe("[SchematicManager] Schematic introuvable : " + file.getPath());
            return;
        }

        var format = ClipboardFormats.findByFile(file);
        if (format == null) {
            plugin.getLogger().severe("[SchematicManager] Format non reconnu.");
            return;
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            Clipboard clipboard = reader.read();

            try (EditSession session = WorldEdit.getInstance()
                    .newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(world))
                    .build()) {

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(session)
                        .to(BlockVector3.at(PASTE_X, y, PASTE_Z))
                        .ignoreAirBlocks(false)
                        .build();

                Operations.complete(operation);
            }
        }
    }

    // Supprime le monde s'il existe déjà pour repartir propre
    private void deleteWorldIfExists() {
        World existing = Bukkit.getWorld(WORLD_NAME);
        if (existing == null) return;

        // Téléporte les joueurs hors du monde avant suppression
        World fallback = Bukkit.getWorlds().get(0);
        existing.getPlayers().forEach(p -> p.teleport(fallback.getSpawnLocation()));

        Bukkit.unloadWorld(existing, false);

        deleteFolder(new File(Bukkit.getWorldContainer(), WORLD_NAME));
    }

    private void deleteFolder(File folder) {
        if (!folder.exists()) return;
        File[] files = folder.listFiles();
        if (files != null) for (File f : files) {
            if (f.isDirectory()) deleteFolder(f);
            else f.delete();
        }
        folder.delete();
    }

    public World getArenaWorld() {
        return arenaWorld;
    }

    public String getWorldName() {
        return WORLD_NAME;
    }

    public boolean isReady() {
        return arenaWorld != null;
    }
}