package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Objects;

public class KitAgile extends KitBase {

    private static final float MOVEMENT_SPEED_MULTIPLIER = 1.2f;
    private static final float FALL_DAMAGE_MULTIPLIER    = 0.0f;
    private static final float MAX_HEALTH_MULTIPLIER     = 0.8f;

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_agile_speed")),
            MOVEMENT_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier FALL_DAMAGE_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_agile_fall_damage")),
            FALL_DAMAGE_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier MAX_HEALTH_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_agile_max_health")),
            MAX_HEALTH_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitAgile() {
        super(
                "Agile",
                "§a+ Vous vous déplacez 20% plus vite\n§a+ Vous ne prenez aucun dégât de chute.\n§c- Vous avez 2 coeurs en moins.",
                Material.FEATHER);
    }

    @Override
    public void apply(Player player) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER);
            speed.addModifier(SPEED_MODIFIER);
        }
        AttributeInstance fallDamage = player.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER);
        if (fallDamage != null) {
            fallDamage.removeModifier(FALL_DAMAGE_MODIFIER);
            fallDamage.addModifier(FALL_DAMAGE_MODIFIER);
        }
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
            maxHealth.addModifier(MAX_HEALTH_MODIFIER);
        }
    }

    @Override
    public void remove(Player player) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER);
        }
        AttributeInstance fallDamage = player.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER);
        if (fallDamage != null) {
            fallDamage.removeModifier(FALL_DAMAGE_MODIFIER);
        }
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER);
        }
    }
}
