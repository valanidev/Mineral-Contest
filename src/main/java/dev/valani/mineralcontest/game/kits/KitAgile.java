package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.KitUtil;
import dev.valani.mineralcontest.utils.MathUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

public class KitAgile extends KitBase{

    private static final float MOVEMENT_SPEED_MULTIPLIER = 1.2f;
    private static final float FALL_DAMAGE_MULTIPLIER = 0.0f;
    private static final float MAX_HEALTH_MULTIPLIER = 0.8f;

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_agile_speed"),
            MOVEMENT_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier FALL_DAMAGE_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_agile_fall_damage"),
            FALL_DAMAGE_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier MAX_HEALTH_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_agile_max_health"),
            MAX_HEALTH_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitAgile() {
        super(
                "Agile",
                "§a✦ Vous vous déplacez §2" + MathUtil.multiplierToPercentage(MOVEMENT_SPEED_MULTIPLIER) + "%§a plus vite\n" +
                        "§a✦ Vous ne prenez aucun dégât de chute.\n" +
                        "§c✖ Vous avez §4" + MathUtil.multiplierToPercentage(MAX_HEALTH_MULTIPLIER) + "%§c de vie en moins.",
                Material.FEATHER);
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER);
        applyModifier(player.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER), FALL_DAMAGE_MODIFIER);
        applyModifier(player.getAttribute(Attribute.MAX_HEALTH), MAX_HEALTH_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.FALL_DAMAGE_MULTIPLIER), FALL_DAMAGE_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.MAX_HEALTH), MAX_HEALTH_MODIFIER.getKey());
    }

}
