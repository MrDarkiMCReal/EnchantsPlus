package org.mrdarkimc.enchantsplus.utils;

import org.mrdarkimc.enchantsplus.enchants.interfaces.Chanceable;

import java.util.List;
import java.util.Random;

public class Randomizer {

    public static <T extends Chanceable> T chooseObject(List<T> objects) {
        double totalChance = 0.0;
        for (T obj : objects) {
            totalChance += obj.getEnchantChance();
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalChance;

        double cumulativeChance = 0.0;
        for (T obj : objects) {
            cumulativeChance += obj.getEnchantChance();
            if (randomValue < cumulativeChance) {
                return obj;
            }
        }

        return null;
    }

}