package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Magnet extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_magnet");
    private String displayname = ChatColor.GRAY + "Магнит "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    public int maxlevel = 1;

    public Magnet() {
        super(key);
        deserealize("magnet");
        Reloadable.register(this);
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
            //Enchants.setEnchantingColor(stack);
            return true;
        }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            meta.getStoredEnchants().forEach((k,v) -> k.getItemTarget().equals(this.getItemTarget()));
            Set<Enchantment> encs = meta.getStoredEnchants().keySet();
            Set<EnchantmentTarget> targets = encs.stream().map(Enchantment::getItemTarget).collect(Collectors.toSet());
            return targets.contains(EnchantmentTarget.TOOL) || targets.contains(EnchantmentTarget.BREAKABLE); //todo unbreakable может быть и на меч
        }
        String name = itemStack.getType().toString();
        return name.contains("PICKAXE") | name.contains("SHOVEL") | name.contains("AXE") ; //!itemStack.hasEnchant(this) &&
    }

    public double getEnchantChance() {
        return chance;
    }

    public void deserealize(String enchant) {
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
        maxlevel = EnchantsPlus.config.get().getInt("enchants." + enchant + ".maxNaturalLevel");
        blockedEnchantsments.clear();
        if (EnchantsPlus.config.get().contains("enchants."+enchant+".conflictsWith") ){
            List<String> list = EnchantsPlus.config.get().getStringList("enchants."+enchant+".conflictsWith");
            list.forEach(s -> blockedEnchantsments.add(Enchantment.getByName(s.toUpperCase())));
        }
    }
    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return blockedEnchantsments.contains(enchantment);
    }
    @Override
    public void accept(Event event) {
        if (event instanceof BlockDropItemEvent e) {
            //Bukkit.getLogger().info("triggering local block drop event");
            for (Item item : e.getItems()) {
                ItemStack stack = item.getItemStack();
                HashMap<Integer, ItemStack> didntFit = e.getPlayer().getInventory().addItem(stack);
                if (!didntFit.isEmpty()) {
                    //drop items on player location
                }
                e.getItems().forEach(Entity::remove);
                //todo удалять энтити
            }
        }
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1", "arr3");
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @Override
    public int getMaxLevel() {
        return maxlevel;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public void reload() {
        deserealize("magnet");
    }

    @Override
    public void printInfo() {
        Debugger.chat("[Magnet] Cached displayname: " + displayname,1);
        Debugger.chat("[Magnet] Cached enchant chance: " + chance,1);
    }
}
