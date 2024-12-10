package org.mrdarkimc.enchantsplus.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Chanceable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {

    public static <T extends Chanceable> T chooseObjectGeneral(List<T> objects) {
        double totalChance = 0.0;
        for (T obj : objects) {
            totalChance += obj.getEnchantChance();
        }
        totalChance = (double) (Math.round(totalChance * 100)) /100;
        Debugger.chat("Summ of chances: " + totalChance,7);

        //Random random = new Random();
        double rand = ((double) Math.round(((Math.random()* totalChance) * 100)) /100);
        //double randomValue = random.nextDouble() * totalChance;
        Debugger.chat("Random Number: " + rand,7);
        double cumulativeChance = 0.0;
        for (T obj : objects) {
            double chance = obj.getEnchantChance();
            Debugger.chat("Object: " + obj + " And chance: " + chance,7);
            cumulativeChance += chance;
            if (rand <= cumulativeChance) {
                Debugger.chat("Choosing: " + obj.toString(),7);
                return obj;
            }
        }
        Debugger.chat("Nothing found with those chance. Returning full random",7);
        return objects.get(ThreadLocalRandom.current().nextInt(0, objects.size())); //todo инкейс у нас будет NPE
    }
    public static Enchantment chooseObject(List<Enchantment> list){
        //Bukkit.getLogger().info(ChatColor.AQUA + "List size: " + list.size());
        for (Enchantment enchantment : list) {
            //Bukkit.getLogger().info(ChatColor.AQUA + "Contains: " + enchantment);
        }
        int rand = ThreadLocalRandom.current().nextInt(0,list.size());
        //todo эта хуйня не привязана к шансам на дроп зачарования
//        double totalchance = 0;
//        for (Enchantment enchantment : list) {
//            totalchance = totalchance + ((IEnchant) enchantment).getEnchantChance(); //0.6
//        }
        return list.get(rand);

    }

}