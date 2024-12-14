package org.mrdarkimc.enchantsplus.enchants.enchantList;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.*;
import java.util.stream.Collectors;

public class Evasion extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {
    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_evasion");
    private String displayname = ChatColor.GRAY + "Уклонение "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.05;
    public Map<Integer,Double> levelModifierMap = new HashMap<>();
    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    public int maxlevel = 2; //todo hardcode
    private int maxTotalLEvel = 1;
    public Evasion() {
        super(key);
        deserealize();
        Reloadable.register(this);
    }
    @Override
    public int getMaxLevel() {
        return maxlevel;
    }
    @Override
    public double getEnchantChance() {
        return chance;
    }

    @Override
    public void accept(Event event) {
    if (event instanceof EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;
        if (e.getEntity() instanceof Player player) {
            
                double rand = ((double) Math.round((Math.random() * 100)) / 100);
                if (rand <= triggerChance) {
                    e.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    if (e.getDamager() instanceof Player damager)
                        damager.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                }
        }
    }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            //meta.getStoredEnchants().forEach((k,v) -> k.getItemTarget().equals(this.getItemTarget()));
            Set<Enchantment> encs = meta.getStoredEnchants().keySet();
            Set<EnchantmentTarget> targets = encs.stream().map(Enchantment::getItemTarget).collect(Collectors.toSet());
            return targets.contains(EnchantmentTarget.ARMOR) ||  targets.contains(EnchantmentTarget.WEARABLE) ||targets.contains(EnchantmentTarget.ARMOR_HEAD)|| targets.contains(EnchantmentTarget.ARMOR_TORSO)|| targets.contains(EnchantmentTarget.ARMOR_LEGS) || targets.contains(EnchantmentTarget.ARMOR_FEET);
        }
        String name = itemStack.getType().toString();
        return name.contains("_HELMET") | name.contains("_CHESTPLATE") | name.contains("_LEGGINGS") | name.contains("_BOOTS") ; //!itemStack.hasEnchant(this) &&
    }
    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("str");
    }

    @Override
    public boolean enchantStack(ItemStack stack, Enchantment enchantment, int level) {
        ItemMeta meta = stack.getItemMeta();
        if (meta.getEnchants().containsKey(enchantment)) {
            if (meta.getEnchantLevel(enchantment) <= level) {
                Enchants.reEnchantCustom(stack,enchantment,level);
                return true;
            }else return false;
        } else {
            Enchants.setCustomLore(meta, enchantment, level);
            meta.addEnchant(enchantment, level, true);
            stack.setItemMeta(meta);
            //Enchants.setEnchantingColor(stack);
            return true;
        }
    }

    @Override
    public int getmaxTotalLevel() {
        return maxTotalLEvel;
    }

    @Override
    public void printInfo() {
        Debugger.chat("[evasion] Cached displayname: " + displayname,1);
        Debugger.chat("[evasion] Cached enchant chance: " + chance,1);
        Debugger.chat("[evasion] Cached trigger chance: " + triggerChance,1);
        levelModifierMap.forEach((key,value) -> {
            Debugger.chat("Level: " + key + " Multiplier: " + value,4);
        });
        Debugger.chat("[evasion] Cached blockedEnchants");
        blockedEnchantsments.forEach(l -> {
            Debugger.chat("[evasion] Cached Blocked enchant:" + l,4);
        });
    }
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR;
    }
    public void deserealize(){
        //todo deserealize
        FileConfiguration config = EnchantsPlus.config.get();
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants.evasion.displayname")));
        chance = config.getDouble("enchants.evasion.ItemEnchantChance");
//        levelModifierMap.clear();
//        Set<String> set = config.getConfigurationSection("enchants.evasion.levelModifiers").getKeys(false);
//        set.forEach(s -> {
//            levelModifierMap.put(Integer.parseInt(s),config.getDouble("enchants.evasion.levelModifiers."+s));
//        });
        maxlevel = EnchantsPlus.config.get().getInt("enchants.evasion.maxNaturalLevel");
        maxTotalLEvel = EnchantsPlus.config.get().getInt("enchants.evasion.maxTotalLevel");
        blockedEnchantsments.clear();
        if (EnchantsPlus.config.get().contains("enchants.evasion.conflictsWith") ){
            List<String> list = EnchantsPlus.config.get().getStringList("enchants.healthboost.conflictsWith");
            list.forEach(s -> blockedEnchantsments.add(Enchantment.getByKey(NamespacedKey.fromString(s))));
        }
        //levelModifierMap.put(1,2.5); //todo hardcode
        //levelModifierMap.put(2,5.0); //todo hardcode

    }
    @Override
    public void reload() {
        deserealize();
    }
}
