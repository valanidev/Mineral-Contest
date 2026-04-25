package dev.valani.mineralcontest.game;

import org.bukkit.block.BlockFace;

public enum DoorOrientation {
    NORTH_SOUTH,
    EAST_WEST;

    public static DoorOrientation fromPlayerFacing(BlockFace face) {
        return switch (face) {
            case EAST, WEST -> EAST_WEST;
            default -> NORTH_SOUTH;
        };
    }
}