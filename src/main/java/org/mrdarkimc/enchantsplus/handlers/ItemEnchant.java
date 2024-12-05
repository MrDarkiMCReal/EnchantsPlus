package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.listeners.ItemEnchantListener;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ItemEnchant implements Enchantable{
    ItemStack stack;

    public ItemEnchant(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void addRandomEnchant() {
        EnchantmentTarget target = Enchants.getTarget(stack);
        List<IEnchant> filteredEnchants = Enchants.getEnchants().stream()
                .filter(enchant -> enchant.getEnchantment().getItemTarget().equals(target))
                .filter(enchant -> enchant.getEnchantment().canEnchantItem(stack))
                .collect(Collectors.toList());
        Enchantment customEnchant = Randomizer.chooseObjectGeneral(filteredEnchants).getEnchantment();
        int level = customEnchant.getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,customEnchant.getMaxLevel()+1) : 1;
        ItemMeta meta = stack.getItemMeta();
        Enchants.setCustomLore(meta, customEnchant, level);
        meta.addEnchant(customEnchant, level, true);
        stack.setItemMeta(meta);
    }

    @Override
    public void removeEnchant() {

    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public double getEnchantChance() {
        return ItemEnchantListener.globalChance;
    }

    @Override
    public boolean mergeWith(Enchantable enchantable) {
        //enchantable 100% книга
        ItemMeta meta =  stack.getItemMeta();
        meta.getEnchants();
        Bukkit.getLogger().info("triggering недоделанный mergewith Item + enc book");
        //todo скопировать из класса Enchants или с книги
        return true;
    }
}
