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

public class KitStealer extends KitBase {

    private static final float SNEAK_SPEED_MULTIPLIER = 1.80f;

    public static final float PICKPOCKET_CHANCE = 0.12f;
    public static final float COUNTER_PICKPOCKET_CHANCE = 0.04f;

    private static final AttributeModifier SNEAK_SPEED_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_stealer_sneak_speed"),
            SNEAK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitStealer() {
        super(
                "Pickpocket",
                "§a✦ Allez §2" + MathUtil.multiplierToPercentage(SNEAK_SPEED_MULTIPLIER) + "%§a plus vite en sneak.\n" +
                        "§a✦ §2" + Math.round(PICKPOCKET_CHANCE * 100) + "%§a de chance de voler des minerais à chaque coup infligé.\n" +
                        "§c✖ §4" + Math.round(COUNTER_PICKPOCKET_CHANCE * 100) + "%§c de chance d'en perdre à chaque coup encaissé.",
                Material.WITHER_SKELETON_SKULL
        );
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.SNEAKING_SPEED), SNEAK_SPEED_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.SNEAKING_SPEED), SNEAK_SPEED_MODIFIER.getKey());
    }
}
