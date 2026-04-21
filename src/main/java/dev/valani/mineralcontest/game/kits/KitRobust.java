package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class KitRobust extends KitBase {

    private static final float HEALTH_MULTIPLIER = 1.50f;
    private static final float KB_RESISTANCE = 0.10f;
    private static final float ATTACK_SPEED_MULTIPLIER = 0.90f;

    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_robust_max_health")),
            HEALTH_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    private static final AttributeModifier KB_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_robust_kb")),
            KB_RESISTANCE,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.ANY
    );

    private static final AttributeModifier ATTACK_SPEED_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_robust_attack_speed")),
            ATTACK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitRobust() {
        super(
                "Robuste",
                "§a+ Vous avez 5 coeurs supplémentaires.\n§a+ Vous prenez 10% de knockback en moins.\n§c- Vous attaquez 10% plus lentement.",
                Material.DIAMOND_CHESTPLATE
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
        applyModifier(health, HEALTH_MODIFIER);
        AttributeInstance kb = player.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        applyModifier(kb, KB_MODIFIER);
        AttributeInstance attackSpeed = player.getAttribute(Attribute.ATTACK_SPEED);
        applyModifier(attackSpeed, ATTACK_SPEED_MODIFIER);
        Utils.applyItems(player);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.MAX_HEALTH), HEALTH_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.KNOCKBACK_RESISTANCE), KB_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.ATTACK_SPEED), ATTACK_SPEED_MODIFIER.getKey());
        player.getInventory().clear();
    }
}
