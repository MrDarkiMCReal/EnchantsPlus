package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.inventory.ItemStack;

public interface Enchantable {
    void addRandomEnchant();
    void removeEnchant();
    ItemStack getStack();
    double getEnchantChance();
    boolean mergeWith(Enchantable enchantable);
}
