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

public class KitBarbarian extends KitBase {

    private static final float ATTACK_MULTIPLIER = 1.10f;
    private static final float SPEED_MULTIPLIER = 0.9f;

    private static final AttributeModifier ATTACK_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_barbarian_attack"),
            ATTACK_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_barbarian_speed"),
            SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitBarbarian() {
        super(
                "Barbare",
                "§a✦ Augmente les dégâts aux entités de §2" + MathUtil.multiplierToPercentage(ATTACK_MULTIPLIER) + "%§a.\n" +
                "§a✦ Potion de régénération (10s) à chaque respawn.\n" +
                "§c✖ Réduit la vitesse de marche de §4" + MathUtil.multiplierToPercentage(SPEED_MULTIPLIER) + "%§c.",
                Material.NETHERITE_SWORD
        );
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.ATTACK_DAMAGE), ATTACK_MODIFIER);
        applyModifier(player.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.ATTACK_DAMAGE), ATTACK_MODIFIER.getKey());
        removeModifier(player.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER.getKey());
    }
}
