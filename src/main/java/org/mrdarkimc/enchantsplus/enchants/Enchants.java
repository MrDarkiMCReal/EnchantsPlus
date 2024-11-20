package org.mrdarkimc.enchantsplus.enchants;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Enchants {
    public Enchants() {
        registerNewEnchantments(AUTOSMELT);
    }

    public static final Enchantment AUTOSMELT = new AutoSmelt();

    public static ItemStack enchantItemStack(ItemStack stack, IEnchant enchant, int lvl) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(enchant.getEnchantment(), lvl, true);
        List<Component> list = new ArrayList<>();
        list.add(Component.text(enchant.getDisplayName()));
        list.addAll(meta.lore() == null ? List.of() : meta.lore());
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
