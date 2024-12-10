package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
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

public class FarArrow extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {
    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_fararrow");
    private String displayname = ChatColor.GRAY + "Дальнобойность "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    public Map<Integer,Double> levelModifierMap = new HashMap<>();
    public int damageLimit = 100;
    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    public int maxlevel = 2; //todo hardcode
    public int maxTotalLEvel = 1;
    public FarArrow() {
        super(key);
        deserealize();
        Reloadable.register(this);
    }
    @Override
    public int getMaxLevel() {
        return maxlevel;
    }
    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BOW_AND_CROSSBOW;
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
            Debugger.chat("Damager: " + e.getDamager(),2);
            Debugger.chat("Entity: " + e.getEntity(),2);

            if (e.getEntity() instanceof Player victim) {
                if (e.getDamager() instanceof Projectile projectile) {
                    //if (projectile.hasMetadata("fararrowlevel")) {
                        int level = (Integer) projectile.getMetadata("fararrowlevel").get(0).value();
                   // }
                    Debugger.chat("Shooter: " + projectile.getShooter(),2);
                    Debugger.chat("Level: " + level,2);
                    if (projectile.getShooter() instanceof Player damager) {
                        Location victimLocation = victim.getLocation();
                        Location damagerLocation = damager.getLocation();
                        double distance = victimLocation.distance(damagerLocation);
                        Debugger.chat("Distance: " + distance,2);
                        Debugger.chat("Original final damage: " + e.getFinalDamage(),2);
                        double levelmodifer = levelModifierMap.get(level);
                        Debugger.chat("Level modifier: " + levelmodifer,2);
                        double multiplier = distance * levelmodifer;
                        Debugger.chat("Multiplier: " + multiplier,2);
                        double minimalDamage = Math.min(damageLimit,multiplier);
                        Debugger.chat("Minimal : " + minimalDamage,2);
                        double calculateddamage = e.getFinalDamage() + minimalDamage;
                        Debugger.chat("Calculated damage: " + calculateddamage,2);
                        e.setDamage(EntityDamageEvent.DamageModifier.ARMOR,calculateddamage);
                    }
                }
            }
            return;
        }
        if (event instanceof ProjectileLaunchEvent ple){

            Projectile projectile = ple.getEntity();
            if (projectile.getShooter() instanceof Player player) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand.getEnchantments().containsKey(Enchants.FARARROW)){
                    Debugger.chat("Launches projectile",6);
                    //projectileItems.put(projectile.getUniqueId(), itemInHand);
                    //projectile.setMetadata("shooterItem", new FixedMetadataValue(EnchantsPlus.getInstance(), itemInHand));
                    projectile.setMetadata("fararrowlevel", new FixedMetadataValue(EnchantsPlus.getInstance(), itemInHand.getEnchantLevel(this)));
                }
        }
            return;
        }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            //meta.getStoredEnchants().forEach((k,v) -> k.getItemTarget().equals(this.getItemTarget()));
            Set<Enchantment> encs = meta.getStoredEnchants().keySet();
            Set<EnchantmentTarget> targets = encs.stream().map(Enchantment::getItemTarget).collect(Collectors.toSet());
            //todo Enchants.getTarget(itemStack);
            return targets.contains(EnchantmentTarget.BOW_AND_CROSSBOW) ||  targets.contains(EnchantmentTarget.BOW) ||targets.contains(EnchantmentTarget.CROSSBOW);
        }
        String name = itemStack.getType().toString();
        return name.contains("BOW") | name.contains("CROSSBOW"); //!itemStack.hasEnchant(this) &&
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
            //Enchants.setEnchantingColor(stack); //todo вернуть
            return true;
        }
    }

    @Override
    public int getmaxTotalLevel() {
        return maxTotalLEvel;
    }

    @Override
    public void printInfo() {
        Debugger.chat("[far arrow] Cached displayname: " + displayname,1);
        Debugger.chat("[far arrow] Cached enchant chance: " + chance,1);
        levelModifierMap.forEach((key,value) -> {
            Debugger.chat("Level: " + key + " Multiplier: " + value,4);
        });
        Debugger.chat("[far arrow] Cached blockedEnchants");
        blockedEnchantsments.forEach(l -> {
            Debugger.chat("[vampire] Cached Blocked enchant:" + l,4);
        });
    }
    public void deserealize(){
        //todo deserealize
        FileConfiguration config = EnchantsPlus.config.get();
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants.fararrow.displayname")));
        chance = config.getDouble("enchants.evasion.ItemEnchantChance");
        levelModifierMap.clear();
        Set<String> set = config.getConfigurationSection("enchants.fararrow.levelModifiers").getKeys(false);
        set.forEach(s -> {
            levelModifierMap.put(Integer.parseInt(s),config.getDouble("enchants.fararrow.levelModifiers."+s));
        });
        maxlevel = EnchantsPlus.config.get().getInt("enchants.fararrow.maxNaturalLevel");
        maxTotalLEvel = EnchantsPlus.config.get().getInt("enchants.fararrow.maxTotalLevel");
        damageLimit = EnchantsPlus.config.get().getInt("enchants.fararrow.damagelimit");
        blockedEnchantsments.clear();
        if (EnchantsPlus.config.get().contains("enchants.fararrow.conflictsWith") ){
            List<String> list = EnchantsPlus.config.get().getStringList("enchants.fararrow.conflictsWith");
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