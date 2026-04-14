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

    public Team(String name, ChatColor color, Material material, int maxPlayers) {
        this.name = name;
        this.color = color;
        this.material = material;
        this.maxPlayers = maxPlayers;
        this.members = new ArrayList<>();
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

    public List<UUID> getMembers() {
        return members;
    }

    public boolean isFull() {
        return members.size() >= maxPlayers;
    }

    public boolean hasMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean addMember(Player player) {
        if (isFull() || hasMember(player.getUniqueId())) return false;
        members.add(player.getUniqueId());
        return true;
    }

    public boolean removeMember(Player player) {
        if (!hasMember(player.getUniqueId())) return false;
        members.remove(player.getUniqueId());
        return true;
    }

    public void clear() {
        members.clear();
    }

    public String getDisplayName() {
        return color + name;
    }
}
