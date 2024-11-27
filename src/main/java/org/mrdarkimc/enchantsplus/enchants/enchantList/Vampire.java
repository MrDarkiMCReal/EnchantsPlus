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
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IAnvilable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Vampire extends Enchantment implements IEnchant, IAnvilable {
    private String displayname = ChatColor.GRAY + "Вампиризм "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_vampire");

    public Vampire() {
        super(key);
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

                int encant_level = attacker.getInventory().getItemInMainHand().getEnchantLevel(this);
                double damagerHP = attacker.getHealth() + formula(e.getFinalDamage(), encant_level);
                double damagerMaxHP = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double finalHelth = Math.min(damagerHP, damagerMaxHP);
                attacker.setHealth(finalHelth);
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

    @NotNull
    @Override
    public String getName() {
        return displayname;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return !itemStack.hasEnchant(this) && Enchants.getTarget(itemStack).equals(this.getItemTarget());
    }

    @NotNull
    @Override
    public Component displayName(int i) {
        return Component.text("Vampire");
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @NotNull
    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.COMMON;
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @NotNull
    @Override
    public Set<EquipmentSlot> getActiveSlots() {
        Set<EquipmentSlot> set = new HashSet<>();
        set.add(EquipmentSlot.HAND);
        return set;
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }
}
