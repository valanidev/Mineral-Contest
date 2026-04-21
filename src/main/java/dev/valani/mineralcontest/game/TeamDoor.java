package dev.valani.mineralcontest.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class TeamDoor {

    private static final boolean[][] PATTERN = {
            {true, false, true},
            {false, true, false},
            {true, false, true}
    };

    private final Location center;
    private final DoorOrientation orientation;
    private final Material concrete;
    private final Material glass;
    private boolean isOpen = false;

    public TeamDoor(Location center, DoorOrientation orientation, Material concrete, Material glass) {
        // Correction : clone + arrondi manuel au lieu de toBlockLocation()
        this.center = center.clone();
        this.center.setX(center.getBlockX());
        this.center.setY(center.getBlockY());
        this.center.setZ(center.getBlockZ());
        this.orientation = orientation;
        this.concrete = concrete;
        this.glass = glass;
    }

    public List<DoorBlock> getDoorBlocks() {
        List<DoorBlock> blocks = new ArrayList<>();
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                boolean isConcrete = PATTERN[row + 1][col + 1];
                blocks.add(new DoorBlock(getBlock(row, col), isConcrete ? concrete : glass));
            }
        }
        return blocks;
    }

    private Block getBlock(int row, int col) {
        int x = center.getBlockX();
        int y = center.getBlockY();
        int z = center.getBlockZ();

        // row = axe vertical (Y), col = axe horizontal selon l'orientation
        return switch (orientation) {
            case NORTH_SOUTH -> center.getWorld().getBlockAt(x + col, y + row, z);
            case EAST_WEST -> center.getWorld().getBlockAt(x, y + row, z + col);
        };
    }

    public void open() {
        if (isOpen) return;
        isOpen = true;
        getDoorBlocks().forEach(db -> db.block().setType(Material.AIR));
    }

    public void close() {
        if (!isOpen) return;
        isOpen = false;
        getDoorBlocks().forEach(db -> db.block().setType(db.material()));
    }

    public void place() {
        isOpen = false;
        getDoorBlocks().forEach(db -> db.block().setType(db.material()));
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Location getCenter() {
        return center;
    }

    public DoorOrientation getOrientation() {
        return orientation;
    }

    public boolean contains(Block block) {
        return getDoorBlocks().stream()
                .anyMatch(db -> db.block().getLocation().equals(block.getLocation()));
    }

    public record DoorBlock(Block block, Material material) {
    }
}