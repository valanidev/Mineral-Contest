package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.game.Team;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
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

    public abstract void apply(Player player, Team team);

    public abstract void remove(Player player);

    protected void applyModifier(AttributeInstance instance, AttributeModifier modifier) {
        if (instance == null) return;

        removeModifier(instance, modifier.getKey());
        instance.addModifier(modifier);
    }

    protected void removeModifier(AttributeInstance instance, NamespacedKey key) {
        if (instance == null) return;

        instance.getModifiers().stream()
                .filter(mod -> mod.getKey().equals(key))
                .forEach(instance::removeModifier);
    }

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
