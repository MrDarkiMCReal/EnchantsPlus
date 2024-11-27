package org.mrdarkimc.enchantsplus.enchants.interfaces;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.List;

public interface IEnchant extends Chanceable {
    void accept(Event event);
    Enchantment getEnchantment();
    String getDisplayName();
    List<String> getCustomLore();

}
