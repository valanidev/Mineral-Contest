package dev.valani.mineralcontest.utils;

import org.bukkit.Bukkit;

public class LogUtil {

    private static final String prefix = "[MineralContest] ";

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("§9" + prefix + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage("§4" + prefix + message);
    }

}
