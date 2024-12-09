package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.listeners.ItemEnchantListener;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.List;
import java.util.Map;
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
        //
        ((IEnchant) customEnchant).enchantStack(stack,customEnchant,level);

        //todo вернуть если все пойдет по пизде
//        ItemMeta meta = stack.getItemMeta();
//        Enchants.setCustomLore(meta, customEnchant, level);
//        meta.addEnchant(customEnchant, level, true);
//        stack.setItemMeta(meta);
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
//        //enchantable 100% книга
//        //на стак нужно наложить энчанты
//        ItemMeta meta =  stack.getItemMeta();
//        meta.getEnchants();
//        Bukkit.getLogger().info("triggering недоделанный mergewith Item + enc book");
//        //todo скопировать из класса Enchants или с книги
//        EnchantmentStorageMeta enchantableMeta = (EnchantmentStorageMeta) enchantable.getStack().getItemMeta();
//        enchantableMeta.getStoredEnchants();
//        return true;
        boolean doreturn = false;
        ItemMeta meta = stack.getItemMeta();
        Map<Enchantment, Integer> originalEnchants = meta.getEnchants();


        EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta) enchantable.getStack().getItemMeta();
        Map<Enchantment, Integer> secondEnchants = bookmeta.getStoredEnchants();
        if (Enchants.doEnchantsConflict(originalEnchants,secondEnchants))
            return false;

        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : secondEnchants.entrySet()) {
            Enchantment enchantment = enchantmentIntegerEntry.getKey();
            int level = enchantmentIntegerEntry.getValue();
            if (enchantment instanceof IEnchant) {
                if (!enchantment.canEnchantItem(stack)) //todo ! or no! ?
                    continue;
                //enchant or increase level of custom enchant
                if (originalEnchants.containsKey(enchantment)) {
                    if (meta.getEnchantLevel(enchantment) <= level) {
                        reEnchantCustomItem(stack, enchantment, level);
                        doreturn = true;
                        continue;
                    }
                    continue;
                } else {
                    Enchants.setCustomLore(meta, enchantment, level);
                    meta.addEnchant(enchantment, level, true);
                    stack.setItemMeta(meta);
                    //Enchants.setEnchantingColor(stack); //todo вернуть
                    doreturn = true;
                    continue;
                }
            } else {
                if (!enchantment.canEnchantItem(stack)) //todo ! or no! ?
                    continue;
                //enchant or increase level of default enchant

                if (meta.getEnchants().containsKey(enchantment)) {
                    if (meta.getEnchantLevel(enchantment) <= level) {
                        level = meta.getEnchantLevel(enchantment) == level ? level+1 : level; //todo for removal if error
                        meta.addEnchant(enchantment, level, true);
                        stack.setItemMeta(meta);
                        doreturn = true;
                    } else {
                        continue;
                    }
                    continue;
                } else {
                    meta.addEnchant(enchantment, level, true);
                    stack.setItemMeta(meta);
                    doreturn = true;
                }
            }
        }
        return doreturn;
    }
    public void reEnchantCustomItem(ItemStack stack, Enchantment enchantment, int level){
        ItemMeta meta = stack.getItemMeta();
        level = meta.getEnchantLevel(enchantment) == level ? level+1 : level; //todo for removal if error
        meta.removeEnchant(enchantment);
        //level = Math.min(level, enchantment.getMaxLevel());
        meta.addEnchant(enchantment, level,true);
        meta.setLore(meta.getLore().stream().filter(line -> (!line.contains(((IEnchant) enchantment).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
        Enchants.setCustomLore(meta, enchantment, level);
        stack.setItemMeta(meta);
    }
}
