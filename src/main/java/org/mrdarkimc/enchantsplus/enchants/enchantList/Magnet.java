package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
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

import java.util.HashMap;
import java.util.List;

public class Magnet extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_magnet");
    private String displayname = ChatColor.GRAY + "Магнит "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode

    public Magnet() {
        super(key);
        deserealizeDefaults("magnet");
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
            Enchants.setEnchantingColor(stack);
            return true;
        }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        String name = itemStack.getType().toString();
        return name.contains("PICKAXE") | name.contains("SHOVEL") | name.contains("AXE") ; //!itemStack.hasEnchant(this) &&
    }

    public double getEnchantChance() {
        return chance;
    }

    public void deserealizeDefaults(String enchant) {
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
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
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public void reload() {
        deserealizeDefaults("magnet");
    }

    @Override
    public void printInfo() {
        Debugger.chat("[Magnet] Cached displayname: " + displayname,1);
        Debugger.chat("[Magnet] Cached enchant chance: " + chance,1);
    }
}
