package org.mrdarkimc.enchantsplus.handlers;

import org.bukkit.inventory.ItemStack;

public class Handler  {
    public static Enchantable newinstance(ItemStack stack){
    switch (stack.getType()){
        case BOOK:
            return new NormalBook(stack);
        case ENCHANTED_BOOK:
            return new EnchantedBook(stack);
        default:
            return new ItemEnchant(stack);
    }
    }
}
