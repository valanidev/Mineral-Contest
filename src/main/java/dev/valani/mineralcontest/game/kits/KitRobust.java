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

public class KitRobust extends KitBase {

    private static final float HEALTH_MULTIPLIER = 1.20f;
    private static final float KB_RESISTANCE = 0.10f;
    private static final float ATTACK_SPEED_MULTIPLIER = 0.90f;

    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_robust_max_health"),
            HEALTH_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    private static final AttributeModifier KB_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_robust_kb"),
            KB_RESISTANCE,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.ANY
    );

    private static final AttributeModifier ATTACK_SPEED_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_robust_attack_speed"),
            ATTACK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitRobust() {
        super(
                "Robuste",
                "§a✦ Vous avez §2" + MathUtil.multiplierToPercentage(HEALTH_MULTIPLIER) + "%§a de vie en plus.\n" +
                        "§a✦ Vous prenez §2" + MathUtil.multiplierToPercentage(1 - KB_RESISTANCE) + "%§a de knockback en moins.\n" +
                        "§c✖ Vous attaquez §4" + MathUtil.multiplierToPercentage(ATTACK_SPEED_MULTIPLIER) + "%§c plus lentement.",
                Material.DIAMOND_CHESTPLATE
        );
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.MAX_HEALTH), HEALTH_MODIFIER);
        applyModifier(player.getAttribute(Attribute.KNOCKBACK_RESISTANCE), KB_MODIFIER);
        applyModifier(player.getAttribute(Attribute.ATTACK_SPEED), ATTACK_SPEED_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.MAX_HEALTH), HEALTH_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.KNOCKBACK_RESISTANCE), KB_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.ATTACK_SPEED), ATTACK_SPEED_MODIFIER.getKey());
    }
}
