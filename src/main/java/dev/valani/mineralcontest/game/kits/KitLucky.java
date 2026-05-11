package dev.valani.mineralcontest.game.kits;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.utils.KitUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitLucky extends KitBase{

    public static final float CHANCE_TO_GET_MINERAL = 5f;
    public static final float CHANCE_TO_DOUBLE_DAMAGE = 8f;
    public static final float CHANCE_TO_TAKE_DAMAGE = 4f;

    public KitLucky(){
        super("Chanceux",
                "§a✦ §2" + Math.round(CHANCE_TO_GET_MINERAL) + "% §ade chance d'obtenir un minerai en cassant de la pierre.\n" +
                        "§a✦ §2" + Math.round(CHANCE_TO_DOUBLE_DAMAGE) + "% §ade chance de doubler les dégâts aux joueurs.\n" +
                        "§c✖ §4" + Math.round(CHANCE_TO_TAKE_DAMAGE) + "% §cde chance de prendre un dégât quand vous en infligez.",
                Material.EMERALD);
    }

    @Override
    public void apply(Player player, Team team) {
        KitUtil.applyKit(this, player, team);
    }

    @Override
    public void remove(Player player) {}
}
