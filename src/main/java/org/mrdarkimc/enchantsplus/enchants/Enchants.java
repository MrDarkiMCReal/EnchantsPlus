package org.mrdarkimc.enchantsplus.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.enchantList.AutoSmelt;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Dozer;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Enchants {
    public Enchants() {
        registerNewEnchantments(AUTOSMELT);
        registerNewEnchantments(DOZER);
    }

    public static final Enchantment AUTOSMELT = new AutoSmelt();
    public static final Enchantment DOZER = new Dozer();

    public static ItemStack applyCustomEnchant(ItemStack stack, Enchantment enchant, int lvl) { //todo Iecnant заменить на Enchantmetns и везде использовать Encants.AUTOSMELT
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(enchant, lvl, true);
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        newLore.add(((IEnchant)enchant).getDisplayName() + ((IEnchant) enchant).getDisplayLevel());
        if (lore!=null) {
            newLore.addAll(lore);
        }
        meta.setLore(newLore);
        stack.setItemMeta(meta);
        return stack;
    }


    public static void registerNewEnchantments(Enchantment enchantment) {
        boolean register = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            register = false;
            throw new RuntimeException(e);
        }
    }
}
