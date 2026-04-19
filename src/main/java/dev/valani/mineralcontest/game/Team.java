package dev.valani.mineralcontest.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private final String name;
    private final ChatColor color;
    private final Material material;
    private final int maxPlayers;
    private final List<UUID> members;
    private int score;

    public Team(String name, ChatColor color, Material material, int maxPlayers) {
        this.name = name;
        this.color = color;
        this.material = material;
        this.maxPlayers = maxPlayers;
        this.members = new ArrayList<>();
        score = 0;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score += score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public boolean isFull() {
        return members.size() >= maxPlayers;
    }

    public boolean hasMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public boolean addMember(Player player) {
        if (isFull() || hasMember(player)) return false;
        return members.add(player.getUniqueId());
    }

    public boolean removeMember(Player player) {
        return members.remove(player.getUniqueId());
    }

    public void clear() {
        members.clear();
    }

    public int size() {
        return members.size();
    }

    public String getDisplayName() {
        return color + name;
    }

    @Override
    public String toString() {
        return getDisplayName() + " (" + size() + "/" + getMaxPlayers() + ")";
    }
}
