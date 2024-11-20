package org.mrdarkimc.enchantsplus.enchants.interfaces;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;

public interface IEnchant {
    void accept(Event event);
    Enchantment getEnchantment();
    String getDisplayName();

}
