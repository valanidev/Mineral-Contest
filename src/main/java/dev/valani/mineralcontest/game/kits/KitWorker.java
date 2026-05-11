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

public class KitWorker extends KitBase{

    public static final float SCORE_MULTIPLIER = 1.25f;
    private static final float MAX_HEALTH_MULTIPLIER = 0.70f;

    private static final AttributeModifier MAX_HEALTH_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_worker_max_health"),
            MAX_HEALTH_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitWorker() {
        super("Travailleur",
                "§a✦ Vous rapportez §2" + MathUtil.multiplierToPercentage(SCORE_MULTIPLIER) + "%§a de points en plus à votre équipe.\n" +
                "§a✦ Vous avez un effet de régénération quand vous ne bougez pas.\n" +
                "§c✖ Vous avez §4" + MathUtil.multiplierToPercentage(MAX_HEALTH_MULTIPLIER) + "%§c de vie en moins.",
                Material.GOLD_INGOT);
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.MAX_HEALTH), MAX_HEALTH_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.MAX_HEALTH), MAX_HEALTH_MODIFIER.getKey());
    }
}
