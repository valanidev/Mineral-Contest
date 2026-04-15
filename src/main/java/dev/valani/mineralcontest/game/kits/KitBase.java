package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class KitBase {

    private final String displayName;
    private final String description;
    private final Material material;

    public KitBase(String displayName, String description, Material material) {
        this.displayName = displayName;
        this.description = description;
        this.material = material;
    }

    public abstract void apply(Player player);

    public abstract void remove(Player player);

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }
}