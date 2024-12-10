package org.mrdarkimc.enchantsplus.enchants;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.enchantList.*;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Enchants implements Reloadable {
    public Enchants() {
        deserealize();
        registerNewEnchantments(AUTOSMELT);
        registerNewEnchantments(DOZER);
        registerNewEnchantments(MAGNET);
        registerNewEnchantments(VAMPIRE);
        registerNewEnchantments(POISON);
        registerNewEnchantments(HEALTHBOOST);
        registerNewEnchantments(EVASION);
        registerNewEnchantments(FARARROW);
        //
    }

    public static final List<Enchantment> customEnchants = new ArrayList<>();

    public static List<IEnchant> getEnchants() {
        return customEnchants.stream().map((enchantment -> (IEnchant) enchantment)).collect(Collectors.toList());
    }

    public static final Enchantment AUTOSMELT = new AutoSmelt();
    public static final Enchantment DOZER = new Dozer();
    public static final Enchantment MAGNET = new Magnet();
    public static final Enchantment VAMPIRE = new Vampire();
    public static final Enchantment POISON = new Poison();
    public static final Enchantment HEALTHBOOST = new HealthBoost();
    public static final Enchantment EVASION = new Evasion();
    public static final Enchantment FARARROW = new FarArrow();

    public static ItemStack applyCustomEnchant(ItemStack stack, Map<Enchantment, Integer> enchats) {
        if (stack.getType().equals(Material.BOOK)) {
            return doEnchantBook(stack, enchats);
        }
        ItemMeta customMeta = stack.getItemMeta();
        ItemMeta meta = stack.getItemMeta();
        for (Enchantment enchantment : enchats.keySet()) {
            if (enchantment instanceof IEnchant) {
                //enchant or increase level of custom enchant

                if (customMeta.getEnchants().containsKey(enchantment)) {
                    if (customMeta.getEnchantLevel(enchantment) < enchats.get(enchantment)) {
                            reEnchantCustom(stack,enchantment,enchats.get(enchantment));
                        return stack;
                    }
                    return null;
                } else {
                    setCustomLore(customMeta, enchantment, enchats.get(enchantment));
                    customMeta.addEnchant(enchantment, enchats.get(enchantment), true);
                    stack.setItemMeta(customMeta);
                    //setEnchantingColor(stack);
                    return stack;
                }
            } else {
                //enchant or increase level of default enchant

                if (meta.getEnchants().containsKey(enchantment)) {
                    if (stack.getEnchantLevel(enchantment) < enchats.get(enchantment)) {
                        meta.addEnchant(enchantment, enchats.get(enchantment), true);
                    }else {
                        return null;
                    }
                    return stack; //todo написал так
                } else {
                    meta.addEnchant(enchantment, enchats.get(enchantment), true);
                }


            }
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static boolean doEnchantsConflict(Map<Enchantment, Integer> original, Map<Enchantment, Integer> mergewithMe){
        for (Enchantment enc : original.keySet()) {
            Debugger.chat("Handling: " + enc,2);
            for (Enchantment secEnc : mergewithMe.keySet()) {
                Debugger.chat("Trying to check conflict with: " + secEnc,2);
                if (enc.conflictsWith(secEnc)) {
                    Debugger.chat("Theyre conflicting. Returning true");
                    return true;
                }
            }
        }
        return false;
    }
    public static void setEnchantingColor(ItemStack cloned){
        Component displayName = cloned.getItemMeta().displayName();

        if (displayName == null) {

            displayName = Component.translatable(cloned.getTranslationKey());
            displayName = displayName
                    .color(TextColor.color(5636095))
                    .decoration(TextDecoration.ITALIC,false);
        } else {
            displayName = displayName
                    .color(TextColor.color(5636095))
                    //.style(displayName.style().color(TextColor.color(5636095)));
                    .decoration(TextDecoration.ITALIC,false);
        }

        ItemMeta meta1 = cloned.getItemMeta();
        meta1.displayName(displayName);
        cloned.setItemMeta(meta1);
    }
    public static void reEnchantCustom(ItemStack stack, Enchantment enchantment, int level){
        //todo if lvl == equals = +1 к лвл
        Debugger.chat("Reenchanting item" + stack,3);
        ItemMeta meta = stack.getItemMeta();
        level = meta.getEnchantLevel(enchantment) == level ? level+1 : level; //todo for removal if error
        meta.removeEnchant(enchantment);
        meta.addEnchant(enchantment,level,true);
        meta.setLore(meta.getLore().stream().filter(line -> (!line.contains(((IEnchant) enchantment).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
        setCustomLore(meta, enchantment, level);
        stack.setItemMeta(meta);
        //setEnchantingColor(stack);
    }
//            ItemMeta meta = stack.getItemMeta();
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            ItemMeta meta = stack.getItemMeta();
//            if (!(enchantment instanceof IEnchant)) {
//                    meta.addEnchant(enchantment, enchats.get(enchantment), true);
//
//            }
//
//            if (meta.getEnchants().containsKey(enchantment)) {
//                if (stack.getEnchantLevel(enchantment) < enchats.get(enchantment)) {
//                    meta.removeEnchant(enchantment);
//                    meta.setLore(meta.getLore().stream().filter(line -> (!line.startsWith(((IEnchant) enchantment).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
//                    setCustomLore(meta, enchantment, enchats.get(enchantment));
//                    return stack;
//                }
//                return stack;
//            } else {
////        if (!getTarget(stack).equals(enchant.getItemTarget()))
////            return stack;
//                setCustomLore(meta, enchantment, enchats.get(enchantment));
//                meta.addEnchant(enchantment, enchats.get(enchantment), true);
//                stack.setItemMeta(meta);
//                return stack;
//            }
//        }
//return stack; //todo написал не думая
//    }


    public static void setCustomLore(ItemMeta meta, Enchantment enchant, int lvl) {
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        newLore.add(((IEnchant) enchant).getDisplayName() + Enchants.getDisplayLevel(lvl));
        if (lore != null) {
            newLore.addAll(lore);
        }
        meta.setLore(newLore);
    }
    public static ItemStack doEnchantBook(ItemStack stack,Map<Enchantment, Integer> enchants) {
            stack.setType(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)stack.getItemMeta();
        for (Enchantment enchantment : enchants.keySet()) {
            if (enchantment instanceof IEnchant) {
                setCustomLore(meta, enchantment, enchants.get(enchantment));
            }
            meta.addEnchant(enchantment, enchants.get(enchantment), true);
        }

            stack.setItemMeta(meta);
            return stack;

    }

    public static EnchantmentTarget getTarget(ItemStack stack) {

        return switch (stack.getType()) {
            case WOODEN_SWORD, STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD, TRIDENT, WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE ->
                    EnchantmentTarget.WEAPON;
            case WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL, WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE ->
                    EnchantmentTarget.TOOL;
            case LEATHER_HELMET, IRON_HELMET, GOLDEN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, LEATHER_BOOTS, IRON_BOOTS, GOLDEN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS, LEATHER_LEGGINGS, IRON_LEGGINGS, GOLDEN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS, LEATHER_CHESTPLATE, IRON_CHESTPLATE, GOLDEN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE ->
                    EnchantmentTarget.ARMOR;
            case BOW, CROSSBOW -> EnchantmentTarget.BOW_AND_CROSSBOW;
            default -> EnchantmentTarget.WEAPON; //todo fix this dump shit
        };
    }

    public static Map<Integer, String> levelDisplay = new HashMap<>();

    public static String getDisplayLevel(int level) { //todo hardcode
        return levelDisplay.get(level);
//        switch (level) {
//            case 1:
//                return "I";
//
//            case 2:
//                return "II";
//
//            case 3:
//                return "III";
//
//            case 4:
//                return "IV";
//
//            case 5:
//                return "V";
//
//            case 6:
//                return "VI";
//
//            case 7:
//                return "VII";
//
//            case 8:
//                return "VIII";
//
//            case 9:
//                return "IX";
//
//            case 10:
//                return "I0";
//
//            default:
//                return String.valueOf(level);
//
//        }
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

    public void deserealize() {
        FileConfiguration file = EnchantsPlus.config.get();
        levelDisplay.clear();
        try {
            for (int i = 1; i <= 10; i++) {
                String value = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(file.getString("global.levels." + i)));
                levelDisplay.put(i, value);
            }
        }catch (NullPointerException e){
            Bukkit.getLogger().info(ChatColor.RED + "Ошибка в настройках уровня. Уровней должно быть минимум 10");
        }


    }

    @Override
    public void reload() {
        deserealize();
    }
}
