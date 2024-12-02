package org.mrdarkimc.enchantsplus.enchants.enchantList;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vampire extends EnchantmentWrapper implements IEnchant, TriggerChance, Reloadable, Infoable {

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_vampire");
    private String displayname = ChatColor.GRAY + "Вампиризм "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.3; //todo hardcode
    public Map<Integer, Double> levelMultiplierMap = new HashMap<>();

    public Vampire() {
        super(key);
        Reloadable.register(this);
        deserealize();
    }
    @Override
    public boolean enchantStack(ItemStack stack, Enchantment enchantment, int level) {
        ItemMeta meta = stack.getItemMeta();
        if (meta.getEnchants().containsKey(enchantment)) {
            if (meta.getEnchantLevel(enchantment) < level) {
                Enchants.reEnchantCustom(stack,enchantment,level);
                return true;
            }else return false;
        } else {
            Enchants.setCustomLore(meta, enchantment, level);
            meta.addEnchant(enchantment, level, true);
            stack.setItemMeta(meta);
            Enchants.setEnchantingColor(stack);
            return true;
        }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        String name = itemStack.getType().toString();
        return name.contains("SWORD") | name.contains("TRIDENT") | name.contains("_AXE") ; //!itemStack.hasEnchant(this) &&
    }

    public double getEnchantChance() {
        return chance;
    }

    public double getTriggerChance() {
        return triggerChance;
    }

    @Override
    public void accept(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player attacker) {
                double rand = ((double) Math.round((Math.random() * 100)) / 100);
                Debugger.chat("[vampire] Trigger chance: " + triggerChance, 4);
                Debugger.chat("[vampire] Random is: " + rand, 4);
                if (rand <= triggerChance) {
                    int encant_level = attacker.getInventory().getItemInMainHand().getEnchantLevel(this);
                    double damagerHP = attacker.getHealth() + formula(e.getFinalDamage(), encant_level);
                    Debugger.chat("Final damage: " + e.getFinalDamage(), 4);
                    Debugger.chat("Health to restore: " + formula(e.getFinalDamage(), encant_level), 4);
                    double damagerMaxHP = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double finalHelth = Math.min(damagerHP, damagerMaxHP);
                    attacker.setHealth(finalHelth);
                }
            }
        }
    }

    double formula(double finalDamage, int level) {
        return levelMultiplierMap.get(level) * finalDamage;
    }

    public void deserealize() {
        levelMultiplierMap.clear();
        ConfigurationSection section = EnchantsPlus.config.get().getConfigurationSection("enchants.vampire.percentOfDamage");
        Set<String> set = section.getKeys(false);
        for (String key : set) {
            levelMultiplierMap.put(Integer.parseInt(key), section.getDouble(key));
        }
        triggerChance = EnchantsPlus.config.get().getDouble("enchants.vampire.triggerChance");
        chance = EnchantsPlus.config.get().getDouble("enchants.vampire.ItemEnchantChance");
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
        deserealize();
    }

    @Override
    public void printInfo() {
        Debugger.chat("[vampire] Cached displayname: " + displayname,1);
        Debugger.chat("[vampire] Cached enchant chance: " + chance,1);
        Debugger.chat("[vampire] Cached trigger chance: " + triggerChance,1);
        Debugger.chat("[vampire] Cached levels: ",1);
        levelMultiplierMap.forEach((key,value) -> {
            Debugger.chat("Level: " + key + " Multiplier: " + value,4);
        });
    }
}
