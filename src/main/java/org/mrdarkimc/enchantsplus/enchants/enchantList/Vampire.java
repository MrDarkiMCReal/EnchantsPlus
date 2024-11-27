package org.mrdarkimc.enchantsplus.enchants.enchantList;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Vampire extends EnchantmentWrapper implements IEnchant, TriggerChance, Reloadable {
    private String displayname = ChatColor.GRAY + "Вампиризм "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_vampire");

    public Vampire() {
        super(key);
        Reloadable.register(this);
    }
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.3; //todo hardcode
    public double getEnchantChance(){
        return chance;
    }
    public double getTriggerChance(){
        return triggerChance;
    }

    @Override
    public void accept(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player attacker) {
                if (Math.random() > triggerChance) {
                    int encant_level = attacker.getInventory().getItemInMainHand().getEnchantLevel(this);
                    double damagerHP = attacker.getHealth() + formula(e.getFinalDamage(), encant_level);
                    double damagerMaxHP = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double finalHelth = Math.min(damagerHP, damagerMaxHP);
                    attacker.setHealth(finalHelth);
                }
            }
        }
    }

    double formula(double finalDamage, int level) {
        switch (level) {
            case 1:
                return finalDamage * 0.15;
            case 2:
                return finalDamage * 0.3;
            case 3:
                return finalDamage * 0.45;
            default: return finalDamage * 0.45;
        }
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("pen", "pej2");
    }


    @Override
    public int getMaxLevel() {
        return 3;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @Override
    public void reload() {

    }
}
