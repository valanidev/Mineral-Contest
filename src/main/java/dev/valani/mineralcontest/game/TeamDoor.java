package dev.valani.mineralcontest.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class TeamDoor {

    private static final boolean[][] PATTERN = {
            {true, false, true},
            {false, true, false},
            {true, false, true}
    };

    private final Location center;
    private final DoorOrientation orientation;
    private final List<DoorBlock> doorBlocks;
    private boolean isOpen = false;

    public TeamDoor(Location center, DoorOrientation orientation, Material concrete, Material glass) {
        this.center = snapToBlock(center);
        this.orientation = orientation;
        this.doorBlocks = buildDoorBlocks(concrete, glass);
    }

    // --- Etat ---

    public void open() {
        if (isOpen) return;
        isOpen = true;
        doorBlocks.forEach(db -> db.block().setType(Material.AIR));
    }

    public void close() {
        if (!isOpen) return;
        isOpen = false;
        doorBlocks.forEach(db -> db.block().setType(db.material()));
    }

    public void place() {
        isOpen = false;
        doorBlocks.forEach(db -> db.block().setType(db.material()));
    }

    public boolean contains(Block block) {
        Location target = block.getLocation();
        return doorBlocks.stream().anyMatch(db -> db.block().getLocation().equals(target));
    }

    // --- Getters ---
    public boolean isOpen() {
        return isOpen;
    }

    public Location getCenter() {
        return center.clone();
    }

    public DoorOrientation getOrientation() {
        return orientation;
    }

    public List<DoorBlock> getDoorBlocks() {
        return doorBlocks;
    }

    // --- Calcul des blocs (une seule fois à la construction) ---
    private List<DoorBlock> buildDoorBlocks(Material concrete, Material glass) {
        var blocks = new java.util.ArrayList<DoorBlock>(9);
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                Material mat = PATTERN[row + 1][col + 1] ? concrete : glass;
                blocks.add(new DoorBlock(resolveBlock(row, col), mat));
            }
        }
        return List.copyOf(blocks);
    }

    private Block resolveBlock(int row, int col) {
        int x = center.getBlockX(), y = center.getBlockY(), z = center.getBlockZ();
        World world = center.getWorld();
        if (world == null) return null;
        return switch (orientation) {
            case NORTH_SOUTH -> world.getBlockAt(x + col, y + row, z);
            case EAST_WEST -> world.getBlockAt(x, y + row, z + col);
        };
    }

    private static Location snapToBlock(Location loc) {
        Location snapped = loc.clone();
        snapped.setX(loc.getBlockX());
        snapped.setY(loc.getBlockY());
        snapped.setZ(loc.getBlockZ());
        return snapped;
    }

    public record DoorBlock(Block block, Material material) {
    }
}