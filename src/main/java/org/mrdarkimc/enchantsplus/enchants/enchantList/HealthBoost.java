package org.mrdarkimc.enchantsplus.enchants.enchantList;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.List;

public class HealthBoost implements IEnchant, Reloadable {
    public HealthBoost() {
        Reloadable.register(this);
    }

    @Override
    public void reload() {

    }

    @Override
    public double getEnchantChance() {
        return 0;
    }
    public void HealthBoostItem(ItemStack stack, int level) {
        if (stack == null || !stack.hasItemMeta()) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }


        if (meta.getAttributeModifiers() != null) {
            AttributeModifier healthBoost = new AttributeModifier(
                    "Health Boost",
                    level * 2.0,
                    AttributeModifier.Operation.ADD_NUMBER
            );


            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthBoost);
        }

        stack.setItemMeta(meta);
    }

    @Override
    public void accept(Event event) {

    }

    @Override
    public Enchantment getEnchantment() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public List<String> getCustomLore() {
        return null;
    }
}
