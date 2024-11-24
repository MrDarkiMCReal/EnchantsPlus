package org.mrdarkimc.enchantsplus.anvilHandler;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AnvilRecipes {
    public AnvilRecipes() {
        registerAutoSmeltRecipe();
    }

    public void registerAutoSmeltRecipe(){
        AnvilRecipe recipe = new AnvilRecipe(new ItemStack(Material.DIAMOND_SWORD), Arrays.asList(new ItemStack(Material.EMERALD)), new ItemStack(Material.DIAMOND_SWORD), false);
        recipe.setFunction(e -> {
            ItemStack item = e.getInventory().getItem(0).clone();
            ItemMeta meta = item.getItemMeta();
            if (meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                int level = meta.getEnchantLevel(Enchantment.DAMAGE_ALL);
                if (level < 5) {
                    meta.removeEnchant(Enchantment.DAMAGE_ALL);
                    meta.addEnchant(Enchantment.DAMAGE_ALL, level+1, false);
                    item.setItemMeta(meta);
                    return item;
                }
                return new ItemStack(Material.AIR);
            }
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
            item.setItemMeta(meta);
            return item;
        });
        AnvilRecipeListener.addRecipe(recipe);
    }
}
