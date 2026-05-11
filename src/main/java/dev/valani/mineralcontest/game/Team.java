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
    private final Material concrete;
    private final Material glass;
    private final int maxPlayers;
    private final List<UUID> members = new ArrayList<>();
    private int score = 0;

    public Team(String name, ChatColor color, Material material, int maxPlayers) {
        this.name = name;
        this.color = color;
        this.material = material;
        this.maxPlayers = maxPlayers;
        this.concrete = resolveConcrete(color);
        this.glass = resolveGlass(color);
    }

    // --- Membres ---
    public void addMember(Player player) {
        if (isFull() || hasMember(player)) return;
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        members.remove(player.getUniqueId());
    }

    public boolean hasMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public boolean isFull() {
        return members.size() >= maxPlayers;
    }

    public void clear() {
        members.clear();
    }

    // --- Score ---
    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public Material getConcrete() {
        return concrete;
    }

    public Material getGlass() {
        return glass;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getScore() {
        return score;
    }

    public int size() {
        return members.size();
    }

    public List<UUID> getMembers() {
        return List.copyOf(members);
    }

    public String getDisplayName() {
        return color + name;
    }

    @Override
    public String toString() {
        return getDisplayName() + " (" + size() + "/" + maxPlayers + ")";
    }

    // --- Résolution des matériaux de porte ---
    private static Material resolveConcrete(ChatColor color) {
        return switch (color) {
            case RED -> Material.RED_CONCRETE;
            case BLUE -> Material.BLUE_CONCRETE;
            case GREEN -> Material.GREEN_CONCRETE;
            case YELLOW -> Material.YELLOW_CONCRETE;
            case AQUA -> Material.CYAN_CONCRETE;
            case LIGHT_PURPLE -> Material.PINK_CONCRETE;
            case BLACK -> Material.BLACK_CONCRETE;
            default -> Material.WHITE_CONCRETE;
        };
    }

    private static Material resolveGlass(ChatColor color) {
        return switch (color) {
            case RED -> Material.RED_STAINED_GLASS;
            case BLUE -> Material.BLUE_STAINED_GLASS;
            case GREEN -> Material.GREEN_STAINED_GLASS;
            case YELLOW -> Material.YELLOW_STAINED_GLASS;
            case AQUA -> Material.CYAN_STAINED_GLASS;
            case LIGHT_PURPLE -> Material.PINK_STAINED_GLASS;
            case BLACK -> Material.BLACK_STAINED_GLASS;
            default -> Material.WHITE_STAINED_GLASS;
        };
    }
}