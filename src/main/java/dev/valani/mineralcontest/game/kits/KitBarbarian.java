package dev.valani.mineralcontest.game.kits;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class KitBarbarian extends KitBase {

    private final float BASE_WALK_SPEED = 0.2f;
    private final float ATTACK_MULTIPLIER = 1.2f;
    private final float WALK_SPEED_MULTIPLIER = 0.95f;

    public KitBarbarian() {
        super(
                "Barbare",
                "§a+ Augmente les dégats aux entités de 20%.\n§c- Réduit la vitesse de marche de 5%.",
                Material.IRON_SWORD
        );
    }

    @Override
    public void apply(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(attackDamage.getDefaultValue() * ATTACK_MULTIPLIER);
        }
        player.setWalkSpeed(BASE_WALK_SPEED * WALK_SPEED_MULTIPLIER);
    }

    @Override
    public void remove(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(attackDamage.getDefaultValue());
        }
        player.setWalkSpeed(BASE_WALK_SPEED);
    }
}
