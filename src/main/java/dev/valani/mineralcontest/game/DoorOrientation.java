package dev.valani.mineralcontest.game;

import org.bukkit.block.BlockFace;

public enum DoorOrientation {
    NORTH_SOUTH, // blocs s'étendent sur X et Y, face nord/sud
    EAST_WEST;   // blocs s'étendent sur Z et Y, face est/ouest

    public static DoorOrientation fromPlayerFacing(BlockFace face) {
        return switch (face) {
            case EAST, WEST -> EAST_WEST;
            default -> NORTH_SOUTH;
        };
    }
}