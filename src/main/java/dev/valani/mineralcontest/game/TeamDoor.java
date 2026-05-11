package dev.valani.mineralcontest.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class TeamDoor {

    /*
     * Pattern 3x3 :
     * x o x     x = concrete  (true)
     * o x o     o = glass     (false)
     * x o x
     */
    private static final boolean[][] PATTERN = {
            {true,  false, true },
            {false, true,  false},
            {true,  false, true }
    };

    private final Location       center;
    private final DoorOrientation orientation;
    private final List<DoorBlock> doorBlocks; // calculé une seule fois
    private boolean isOpen = false;

    public TeamDoor(Location center, DoorOrientation orientation, Material concrete, Material glass) {
        this.center      = snapToBlock(center);
        this.orientation = orientation;
        this.doorBlocks  = buildBlocks(concrete, glass);
    }

    // --- État ---

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
        return doorBlocks.stream()
                .anyMatch(db -> db.block().getLocation().equals(target));
    }

    // --- Getters ---

    public boolean isOpen()                  { return isOpen; }
    public Location getCenter()              { return center.clone(); }
    public DoorOrientation getOrientation()  { return orientation; }
    public List<DoorBlock> getDoorBlocks()   { return doorBlocks; }

    // --- Construction des blocs ---

    private List<DoorBlock> buildBlocks(Material concrete, Material glass) {
        var list = new java.util.ArrayList<DoorBlock>(9);
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                Material mat = PATTERN[row + 1][col + 1] ? concrete : glass;
                list.add(new DoorBlock(resolveBlock(row, col), mat));
            }
        }
        return List.copyOf(list);
    }

    private Block resolveBlock(int row, int col) {
        int x = center.getBlockX(), y = center.getBlockY(), z = center.getBlockZ();
        return switch (orientation) {
            case NORTH_SOUTH -> center.getWorld().getBlockAt(x + col, y + row, z);
            case EAST_WEST   -> center.getWorld().getBlockAt(x, y + row, z + col);
        };
    }

    private static Location snapToBlock(Location loc) {
        Location s = loc.clone();
        s.setX(loc.getBlockX());
        s.setY(loc.getBlockY());
        s.setZ(loc.getBlockZ());
        return s;
    }

    public record DoorBlock(Block block, Material material) {}
}