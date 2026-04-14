package dev.valani.mineralcontest.managers;

import dev.valani.mineralcontest.Main;

public class GameManager {
    private final Main plugin;
    private GameState state;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.state = GameState.WAITING;
    }

    public GameState getState() { return state; }
    public boolean isState(GameState s) { return state == s; }

    public boolean start() {
        if(!isState(GameState.WAITING)) {
            plugin.consoleError("La partie a déjà commencé.");
            return false;
        }
        this.state = GameState.STARTED;
        return true;
    }

    public boolean stop() {
        if(!isState(GameState.STARTED)) {
            plugin.consoleError("La partie n'a pas encore commencé.");
            return false;
        }
        this.state = GameState.ENDED;
        return true;
    }
}
