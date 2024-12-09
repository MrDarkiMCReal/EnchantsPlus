package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.handlers.Enchantable;
import org.mrdarkimc.enchantsplus.handlers.Handler;
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
        globalChance =  EnchantsPlus.config.get().getDouble("global.customEnchantChance");
    }

    @Override
    public void reload() {
        deserealize();
    }
    public static double globalChance = 0.3;
    @EventHandler
    void onEnchant(EnchantItemEvent event) {
        ItemStack stack = event.getItem();
        Enchantable enc = Handler.newinstance(stack);
        double rand = ((double) Math.round((Math.random() * 100)) /100);


        ///todo handling book
        if (event.getItem().getType().equals(Material.BOOK)){
            if (rand <= enc.getEnchantChance()) {
                Inventory topInventory = event.getView().getTopInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        enc.addRandomEnchant();
                        topInventory.setItem(0, enc.getStack());
                    }
                }.runTask(EnchantsPlus.getInstance());
                return;
            }
            return;
        }



        //todo handling others
        if (rand <= enc.getEnchantChance()) {
            enc.addRandomEnchant();
        }
        Debugger.chat("Random is: " + rand,1);
        Debugger.chat("Global chance: " + globalChance,1);
//        if (rand <= enc.getEnchantChance()) {
//            //отменить эвент
//            //забрать эксп
//            //зачаровать
//            //
//            Debugger.chat("Calling doEnchant method",1);
//            doEnchant(stack, Enchants.getTarget(stack)); //Enchants.getTarget(stack)
//
////            int levelcost = event.getExpLevelCost();
////
////
////            Player player = event.getEnchanter();
////            enc.addRandomEnchant();
////            player.setLevel(player.getLevel()-levelcost);
//
//        }

    }
    private void doEnchant(ItemStack stack, EnchantmentTarget target){
        List<IEnchant> filteredEnchants = Enchants.getEnchants().stream()
                .filter(enchant -> enchant.getEnchantment().getItemTarget().equals(target))
                .filter(enchant -> enchant.getEnchantment().canEnchantItem(stack))
                .collect(Collectors.toList());
        Enchantment customEnchant = Randomizer.chooseObjectGeneral(filteredEnchants).getEnchantment();
        int level = customEnchant.getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,customEnchant.getMaxLevel()+1) : 1;
        Enchants.applyCustomEnchant(stack, Map.of(customEnchant,level));

    }
}
