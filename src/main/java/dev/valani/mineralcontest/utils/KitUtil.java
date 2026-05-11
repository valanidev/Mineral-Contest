package dev.valani.mineralcontest.utils;

import dev.valani.mineralcontest.game.Team;
import dev.valani.mineralcontest.game.kits.KitBarbarian;
import dev.valani.mineralcontest.game.kits.KitBase;
import dev.valani.mineralcontest.game.kits.KitMiner;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitUtil {

    public static final int[] MINER_BLOCKED_SLOTS = {0, 8, 9, 18, 27, 17, 26, 35};
    private static final ItemStack BLOCKED_ITEM = buildBlockedItem();

    public static void applyKit(KitBase kit, Player player, Team team) {
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if(maxHealthAttr != null) player.setHealth(maxHealthAttr.getValue());

        if(kit instanceof KitMiner) {
            for(int slot : MINER_BLOCKED_SLOTS)
                player.getInventory().setItem(slot, BLOCKED_ITEM);
            ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
            player.getInventory().setItem(2, pickaxe);
        } else if (kit instanceof KitBarbarian) {
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            assert meta != null;
            meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0), true);
            meta.setDisplayName("§dPotion de barbare");
            potion.setItemMeta(meta);
            player.getInventory().addItem(potion);
        }

        ItemStack helmet = createTeamHelmet(team);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack axe = new ItemStack(Material.STONE_AXE);
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 32);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.getInventory().addItem(sword, axe, food);
    }

    private static ItemStack createTeamHelmet(Team team) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        if (team == null) return helmet;
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        if (meta == null) return helmet;
        meta.setColor(chatColorToColor(team.getColor()));
        helmet.setItemMeta(meta);
        return helmet;
    }

    public static Color chatColorToColor(ChatColor chatColor) {
        return switch (chatColor) {
            case AQUA -> Color.AQUA;
            case BLACK -> Color.BLACK;
            case BLUE, DARK_AQUA, DARK_BLUE -> Color.BLUE;
            case DARK_GRAY, GRAY -> Color.GRAY;
            case DARK_GREEN, GREEN -> Color.GREEN;
            case DARK_PURPLE, LIGHT_PURPLE -> Color.PURPLE;
            case DARK_RED, RED -> Color.RED;
            case GOLD, YELLOW -> Color.YELLOW;
            case WHITE -> Color.WHITE;
            default -> null;
        };
    }

    private static ItemStack buildBlockedItem() {
        return new ItemBuilder(Material.BARRIER).setDisplayName("§8Slot bloqué").build();
    }

    public static boolean isBlockedSlot(int slot) {
        for(int blocked : MINER_BLOCKED_SLOTS) {
            if (blocked == slot) return true;
        }
        return false;
    }

    public static ItemStack getBlockedItem() {
        return BLOCKED_ITEM;
    }

}
