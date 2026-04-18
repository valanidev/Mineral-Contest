package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Objects;

public class KitBarbarian extends KitBase {

    private static final float ATTACK_MULTIPLIER = 1.15f;
    private static final float WALK_SPEED_MULTIPLIER = 0.9f;

    private static final AttributeModifier ATTACK_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_barbarian_attack")),
            ATTACK_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_barbarian_speed")),
            WALK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitBarbarian() {
        super(
                "Barbare",
                "§a+ Augmente les dégats aux entités de 15%.\n§c- Réduit la vitesse de marche de 10%.",
                Material.IRON_SWORD
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attack != null) {
            attack.removeModifier(ATTACK_MODIFIER);
            attack.addModifier(ATTACK_MODIFIER);
        }
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER);
            speed.addModifier(SPEED_MODIFIER);
        }
    }

    @Override
    public void remove(Player player) {
        AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attack != null) {
            attack.removeModifier(ATTACK_MODIFIER);
        }
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER);
        }
    }
}
