package org.mrdarkimc.enchantsplus.enchants.enchantList;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;

public class Dozer2 extends EnchantmentWrapper {
    public Dozer2(@NotNull NamespacedKey key) {
        super(key);
    }
    private String displayname = ChatColor.GRAY + "Будьдозер "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_dozer2");

    public Dozer2() {
        super(key);
    }
    private static double chance = 0.3; //todo hardcode
    public double getEnchantChance(){
        return chance;
    }
}
