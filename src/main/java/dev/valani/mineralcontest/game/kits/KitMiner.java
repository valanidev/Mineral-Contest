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

public class KitMiner extends KitBase{

    private static final float BLOCK_BREAK_SPEED_MULTIPLIER = 1.10f;

    private static final AttributeModifier BLOCK_BREAK_SPEED_MODIFIER = new AttributeModifier(
            NamespacedKey.fromString("mineralcontest:kit_miner_mining_speed"),
            BLOCK_BREAK_SPEED_MULTIPLIER - 1,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.ANY
    );

    public KitMiner() {
        super(
                "Mineur",
                "§a✦ Cuit automatiquement les minerais.\n" +
                "§a✦ Minez §2" + MathUtil.multiplierToPercentage(BLOCK_BREAK_SPEED_MULTIPLIER) + "%§a plus rapidement les blocs.\n" +
                "§c✖ Retire §4" + KitUtil.MINER_BLOCKED_SLOTS.length + "§c slots d'inventaire.",
                Material.IRON_PICKAXE
        );
    }

    @Override
    public void apply(Player player, Team team) {
        applyModifier(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), BLOCK_BREAK_SPEED_MODIFIER);
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {
        removeModifier(player.getAttribute(Attribute.BLOCK_BREAK_SPEED), BLOCK_BREAK_SPEED_MODIFIER.getKey());
    }
}
