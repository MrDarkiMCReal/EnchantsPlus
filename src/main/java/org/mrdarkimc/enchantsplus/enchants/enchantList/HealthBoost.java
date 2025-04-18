package org.mrdarkimc.enchantsplus.enchants.enchantList;

import com.google.common.collect.Multimap;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
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

public class HealthBoost extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {
    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_healthboost");
    private String displayname = ChatColor.GRAY + "Бонус жизни "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    public int maxlevel = 2;
    private int maxTotalLEvel = 1;
    public HealthBoost() {
        super(key);
        Reloadable.register(this);
        deserealize();
    }
    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return blockedEnchantsments.contains(enchantment);
    }
    @Override
    public int getMaxLevel(){
        return maxlevel;
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
    public boolean enchantStack(ItemStack stack, Enchantment enchantment, int level) {
        ItemMeta meta = stack.getItemMeta();
        if (meta.getEnchants().containsKey(enchantment)) {
            if (meta.getEnchantLevel(enchantment) < level) {
                //Enchants.reEnchantCustom(stack,enchantment,level);
                reEnchantHealthBoost(stack, level);
                return true;
            }else return false;
        } else {
            //Enchants.setCustomLore(meta, enchantment, level);
            //todo обработать если есть такой уже зачар
            EquipmentSlot slot = stack.getType().getEquipmentSlot();
            if (!List.of(EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD).contains(slot))
                return false;
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,new AttributeModifier(UUID.randomUUID(),"customHealthBoost",levelModifierMap.get(level), AttributeModifier.Operation.ADD_NUMBER,slot));
            meta.addEnchant(this,level,true);
            Enchants.setCustomLore(meta,this,level);
            stack.setItemMeta(meta);
            //Enchants.setEnchantingColor(stack);
            //Enchants.setEnchantingColor(stack);
            return true;
        }
    }

    @Override
    public int getmaxTotalLevel() {
        return maxTotalLEvel;
    }

    public void reEnchantHealthBoost(ItemStack stack, int level){
    EquipmentSlot slot = stack.getType().getEquipmentSlot();
        ItemMeta meta =  stack.getItemMeta();

        Multimap<Attribute,AttributeModifier> multimap = stack.getItemMeta().getAttributeModifiers();
        if (multimap !=null && multimap.containsKey(Attribute.GENERIC_MAX_HEALTH)){
            multimap.get(Attribute.GENERIC_MAX_HEALTH).clear();
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,new AttributeModifier(UUID.randomUUID(),"customHealthBoost",levelModifierMap.get(level), AttributeModifier.Operation.ADD_NUMBER,slot));
            Enchants.setCustomLore(meta,this,level);
            stack.setItemMeta(meta);
        }


}

    public Map<Integer,Double> levelModifierMap = new HashMap<>();
public void deserealize(){
        //todo deserealize
    FileConfiguration config = EnchantsPlus.config.get();
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants.healthboost.displayname")));
        chance = config.getDouble("enchants.healthboost.ItemEnchantChance"); //todo вынести в конфиг
        levelModifierMap.clear();
        Set<String> set = config.getConfigurationSection("enchants.healthboost.levelModifiers").getKeys(false);
        set.forEach(s -> {
            levelModifierMap.put(Integer.parseInt(s),config.getDouble("enchants.healthboost.levelModifiers."+s));
        });
        maxlevel = EnchantsPlus.config.get().getInt("enchants.healthboost.maxNaturalLevel");
    maxTotalLEvel = EnchantsPlus.config.get().getInt("enchants.healthboost.maxTotalLevel");
    blockedEnchantsments.clear();
    if (EnchantsPlus.config.get().contains("enchants.healthboost.conflictsWith") ){
        List<String> list = EnchantsPlus.config.get().getStringList("enchants.healthboost.conflictsWith");
        list.forEach(s -> blockedEnchantsments.add(Enchantment.getByName(s.toUpperCase())));
    }
        //levelModifierMap.put(1,2.5); //todo hardcode
        //levelModifierMap.put(2,5.0); //todo hardcode

}
        @NotNull
        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ARMOR;
        }
    @Override
    public void reload() {
        deserealize();
    }

    @Override
    public double getEnchantChance() {
        return chance;
    }
//    public void HealthBoostItem(ItemStack stack, int level) {
//        if (stack == null || !stack.hasItemMeta()) {
//            return;
//        }
//
//        ItemMeta meta = stack.getItemMeta();
//        if (meta == null) {
//            return;
//        }
//
//
//        if (meta.getAttributeModifiers() != null) {
//            AttributeModifier healthBoost = new AttributeModifier(
//                    "Health Boost",
//                    level * 2.0,
//                    AttributeModifier.Operation.ADD_NUMBER
//            );
//
//
//            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthBoost);
//        }
//
//        stack.setItemMeta(meta);
//    }

    @Override
    public void accept(Event event) {
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
        return List.of("1","2");
    }

    @Override
    public void printInfo() {
        Debugger.chat("[Health] Cached displayname: " + displayname,1);
        Debugger.chat("[Health] Cached enchant chance: " + chance,1);
        Debugger.chat("[Health] Cached naturalMaxLevel: " + maxlevel,1);
        Debugger.chat("[Health] Cached totalMaxLevel: " + maxTotalLEvel,1);
        blockedEnchantsments.forEach(l -> {
            Debugger.chat("[Health] Cached Blocked enchant:" + l,1);
        });
    }
}
