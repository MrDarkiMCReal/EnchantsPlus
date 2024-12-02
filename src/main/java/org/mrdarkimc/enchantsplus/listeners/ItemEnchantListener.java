package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ItemEnchantListener implements Listener, Reloadable {
    public ItemEnchantListener() {
        deserealize();
    }

    public void deserealize(){
        this.globalChance =  EnchantsPlus.config.get().getDouble("global.customEnchantChance");
    }

    @Override
    public void reload() {
        deserealize();
    }
    public double globalChance = 0.3;
    @EventHandler
    void onEnchant(EnchantItemEvent event) {
        ItemStack stack = event.getItem();
        if (event.getItem().getType().equals(Material.BOOK))
            return;
        //0.356*100 = 35.6 = 36
        double rand = ((double) Math.round((Math.random() * 100)) /100);
        Debugger.chat("Random is: " + rand,1);
        Debugger.chat("Global chance: " + globalChance,1);
        if (rand <= globalChance) {
            Debugger.chat("Calling doEnchant method",1);
            doEnchant(stack, Enchants.getTarget(stack)); //Enchants.getTarget(stack)
//            EnchantmentTarget target = event.getEnchantsToAdd().keySet().stream().findFirst().get().getItemTarget();
//            IEnchant customEnchant = Randomizer.chooseObject(Enchants.getEnchants().stream().filter((enchant -> enchant.getEnchantment().getItemTarget().equals(target))).collect(Collectors.toList()));
//            int level = customEnchant.getEnchantment().getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,customEnchant.getEnchantment().getMaxLevel()) : 1;
//
//            Map<Enchantment,Integer> enchants = event.getEnchantsToAdd();
//            enchants.put(customEnchant.getEnchantment(),level);
//            Enchants.applyCustomEnchant(stack,enchants);
//            //event.setCancelled(true);

        }

    }
    private void doEnchant(ItemStack stack, EnchantmentTarget target){
        for (Enchantment customEnchant : Enchants.customEnchants) {
            //Bukkit.getLogger().info(ChatColor.RED + "customEnchants contains: " + customEnchant);
        }
        //Bukkit.getLogger().info("Filtering enchants for target: " + target);
        List<IEnchant> filteredEnchants = Enchants.getEnchants().stream()
                .filter(enchant -> enchant.getEnchantment().getItemTarget().equals(target))
                .filter(enchant -> enchant.getEnchantment().canEnchantItem(stack))
                .collect(Collectors.toList());
        //Bukkit.getLogger().info("Filtered enchants: " + filteredEnchants.size());
        for (IEnchant filteredEnchant : filteredEnchants) {
            //Bukkit.getLogger().info("Filtered enchant: " +filteredEnchant);
        }
        Enchantment customEnchant = Randomizer.chooseObjectGeneral(filteredEnchants).getEnchantment();
        for (Enchantment customEnchant2 : Enchants.customEnchants) {
            //Bukkit.getLogger().info(ChatColor.RED + "customEnchants contains: " + customEnchant2);
        }
        //Bukkit.getLogger().info(ChatColor.YELLOW + "Enchantment: " + customEnchant);
        //Bukkit.getLogger().info(ChatColor.YELLOW + "Enchant max level: " + customEnchant.getMaxLevel());
        int level = customEnchant.getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,customEnchant.getMaxLevel()+1) : 1;
        //Bukkit.getLogger().info(ChatColor.YELLOW + "Choosing level: " + level);
        Enchants.applyCustomEnchant(stack, Map.of(customEnchant,level));

    }
}
