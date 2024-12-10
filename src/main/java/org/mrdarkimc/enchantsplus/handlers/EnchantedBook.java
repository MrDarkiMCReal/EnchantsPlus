package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantedBook implements Enchantable {
    ItemStack stack;

    public EnchantedBook(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void addRandomEnchant() {

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
        return 0; //todo not used
    }

    @Override
    public boolean mergeWith(Enchantable enchantable) {
        //enchantable это 100% книга с зачарованием
        boolean doreturn = false;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
        Map<Enchantment, Integer> originalEnchants = meta.getStoredEnchants();


        EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) enchantable.getStack().getItemMeta();
        Map<Enchantment, Integer> secondEnchants = meta2.getStoredEnchants();

        if (Enchants.doEnchantsConflict(originalEnchants,secondEnchants)) {
            Debugger.chat("Enchants conflict. Returning back");
            return false;
        }

        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : secondEnchants.entrySet()) {
            Enchantment enchantment = enchantmentIntegerEntry.getKey();
            int level = enchantmentIntegerEntry.getValue();
            if (enchantment instanceof IEnchant) {
                if (!enchantment.canEnchantItem(stack)) //todo ! or no! ?
                    continue;
                //enchant or increase level of custom enchant
                if (originalEnchants.containsKey(enchantment)) {
                    if (meta.getStoredEnchantLevel(enchantment) <= level) {
                        reEnchantCustomBook(stack, enchantment, level);
                        doreturn = true;
                        continue;
                    }
                    continue;
                } else {
                    Enchants.setCustomLore(meta, enchantment, level);
                    meta.addStoredEnchant(enchantment, level, true);
                    stack.setItemMeta(meta);
                    //Enchants.setEnchantingColor(stack);
                    doreturn = true;
                    break;
                }
            } else {
                if (!enchantment.canEnchantItem(stack)) //todo ! or no! ?
                    continue;
                //enchant or increase level of default enchant

                if (meta.getStoredEnchants().containsKey(enchantment)) {
                    if (meta.getEnchantLevel(enchantment) <= level) {
                        level = meta.getEnchantLevel(enchantment) == level ? level+1 : level; //todo for removal if error
                        meta.addStoredEnchant(enchantment, level, true);
                        doreturn = true;
                    } else {
                        break;
                    }
                    break;
                } else {
                    meta.addEnchant(enchantment, level, true);
                    doreturn = true;
                }
            }
        }
        return doreturn; //todo заменить на более детальный true/false
    }
    public void reEnchantCustomBook(ItemStack stack, Enchantment enchantment, int level){
        EnchantmentStorageMeta meta =  (EnchantmentStorageMeta) stack.getItemMeta();
        level = meta.getStoredEnchantLevel(enchantment) == level ? level+1 : level; //todo for removal if error
        meta.removeStoredEnchant(enchantment);
        level = Math.min(level, enchantment.getMaxLevel());
        meta.addStoredEnchant(enchantment, level,true);
        meta.setLore(meta.getLore().stream().filter(line -> (!line.contains(((IEnchant) enchantment).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
        Enchants.setCustomLore(meta, enchantment, level);
        stack.setItemMeta(meta);
    }
}
