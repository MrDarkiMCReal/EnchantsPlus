package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.concurrent.ThreadLocalRandom;

public class NormalBook implements Enchantable{
    ItemStack stack;
    double enchantchance = 0.2; //todo hardcode
    public NormalBook(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void addRandomEnchant() {
        stack.setType(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
//        IEnchant enchant = Enchants.getEnchants()
//                .stream()
//                .findAny()
//                .get();
        IEnchant enchant = Randomizer.chooseObjectGeneral(Enchants.getEnchants());
        int randomLevel = enchant.getEnchantment().getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,enchant.getEnchantment().getMaxLevel()+1) : 1;
        meta.addEnchant(enchant.getEnchantment(),randomLevel,true);
        Enchants.setCustomLore(meta,enchant.getEnchantment(),randomLevel);
        stack.setItemMeta(meta);
    }
    @Override
    public double getEnchantChance(){
        return enchantchance;
    }

    @Override
    public void removeEnchant() {

    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean mergeWith(Enchantable enchantable) {
        return false;
    }
}
