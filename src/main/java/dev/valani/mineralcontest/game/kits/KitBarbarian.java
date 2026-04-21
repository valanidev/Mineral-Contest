package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class KitBarbarian extends KitBase {

    private static final float ATTACK_MULTIPLIER = 1.15f;
    private static final float SPEED_MULTIPLIER = 0.9f;

    private static final AttributeModifier ATTACK_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_barbarian_attack")),
            ATTACK_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            Objects.requireNonNull(NamespacedKey.fromString("mineralcontest:kit_barbarian_speed")),
            SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitBarbarian() {
        super(
                "Barbare",
                "§a+ Augmente les dégâts aux entités de 15%.\n§c- Réduit la vitesse de marche de 10%.",
                Material.IRON_SWORD
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance attack = player.getAttribute(Attribute.ATTACK_DAMAGE);
        applyModifier(attack, ATTACK_MODIFIER);
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        applyModifier(speed, SPEED_MODIFIER);
        Utils.applyItems(player);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.ATTACK_DAMAGE), ATTACK_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER.getKey());
        player.getInventory().clear();
    }
}
