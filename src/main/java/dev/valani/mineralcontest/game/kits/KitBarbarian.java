package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class KitBarbarian extends KitBase {

    private final double attackMultiplier = 1.2;
    float baseAttackDamage = 2.0f;
    float walkSpeedMultiplier = 0.95f;

    public KitBarbarian() {
        super(
                "Barbare",
                "§a+ Augmente les dégats aux entités.\n§c- Réduit la vitesse de marche.",
                Material.IRON_SWORD
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        attackDamage.setBaseValue(attackDamage.getDefaultValue() * attackMultiplier);
        player.setWalkSpeed(player.getWalkSpeed() * walkSpeedMultiplier);
    }

    @Override
    public void remove(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        attackDamage.setBaseValue(attackDamage.getDefaultValue());
        player.setWalkSpeed(player.getWalkSpeed() / walkSpeedMultiplier);
    }
}
