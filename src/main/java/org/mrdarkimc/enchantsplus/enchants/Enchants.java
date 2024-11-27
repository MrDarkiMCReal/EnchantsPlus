package org.mrdarkimc.enchantsplus.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.enchantList.*;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Enchants {
    public Enchants() {
        registerNewEnchantments(AUTOSMELT);
        registerNewEnchantments(DOZER);
        registerNewEnchantments(MAGNET);
        registerNewEnchantments(VAMPIRE);
        registerNewEnchantments(POISON);

        //
    }

    public static final List<Enchantment> customEnchants = new ArrayList<>();
    public static List<IEnchant> getEnchants(){
        return customEnchants.stream().map((enchantment -> (IEnchant)enchantment)).collect(Collectors.toList());
    }

    public static final Enchantment AUTOSMELT = new AutoSmelt();
    public static final Enchantment DOZER = new Dozer();
    public static final Enchantment MAGNET = new Magnet();
    public static final Enchantment VAMPIRE = new Vampire();
    public static final Enchantment POISON = new Poison();

    public static ItemStack applyCustomEnchant(ItemStack stack, Enchantment enchant, int lvl) { //todo Iecnant заменить на Enchantmetns и везде использовать Encants.AUTOSMELT
        ItemMeta meta = stack.getItemMeta();
        if (meta.getEnchants().containsKey(enchant)){
            if (stack.getEnchantLevel(enchant) < lvl){
                meta.removeEnchant(enchant);
                meta.setLore(meta.getLore().stream().filter(line -> (!line.startsWith(((IEnchant) enchant).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
                setCustomLore(meta,enchant,lvl);
                return stack;
            }
            return stack;
        }
         else {
//        if (!getTarget(stack).equals(enchant.getItemTarget()))
//            return stack;
            setCustomLore(meta, enchant, lvl);
            meta.addEnchant(enchant, lvl, true);
            stack.setItemMeta(meta);
            return stack;
        }
    }
    private static void setCustomLore(ItemMeta meta, Enchantment enchant, int lvl){
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        newLore.add(((IEnchant) enchant).getDisplayName() + Enchants.getDisplayLevel(lvl));
        if (lore != null) {
            newLore.addAll(lore);
        }
        meta.setLore(newLore);
    }
    public static EnchantmentTarget getTarget(ItemStack stack){

        return switch (stack.getType()) {
            case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, TRIDENT, WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE ->
                    EnchantmentTarget.WEAPON;
            case WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE ->
                    EnchantmentTarget.TOOL;
            case LEATHER_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, LEATHER_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS, LEATHER_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS, LEATHER_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE ->
                    EnchantmentTarget.ARMOR;
            default -> EnchantmentTarget.WEAPON; //todo fix this dump shit
        };
    }


    public static String getDisplayLevel(int level) { //todo hardcode
        switch (level) {
            case 1:
                return "I";

            case 2:
                return "II";

            case 3:
                return "III";

            case 4:
                return "IV";

            case 5:
                return "V";

            case 6:
                return "VI";

            case 7:
                return "VII";

            case 8:
                return "VIII";

            case 9:
                return "IX";

            case 10:
                return "I0";

            default:
                return String.valueOf(level);

        }
    }


    public static void registerNewEnchantments(Enchantment enchantment) {
        boolean register = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
            customEnchants.add(enchantment);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            register = false;
            throw new RuntimeException(e);
        }
    }
}
