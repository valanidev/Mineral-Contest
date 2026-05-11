package dev.valani.mineralcontest.utils;

import org.bukkit.Location;

public class MathUtil {

    public static int multiplierToPercentage(float multiplier) {
        return Math.abs(Math.round((multiplier - 1F) * 100F));
    }

}
