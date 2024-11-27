package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.HashMap;
import java.util.List;

public class Magnet extends EnchantmentWrapper implements IEnchant, Reloadable {
    private String displayname = ChatColor.GRAY + "Магнит "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_magnet");
    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1","arr3");
    }

    public Magnet() {
        super(key);
        deserealizeDefaults("magnet");
        Reloadable.register(this);
    }
    private static double chance = 0.3; //todo hardcode
    public double getEnchantChance(){
        return chance;
    }
    public void deserealizeDefaults(String enchant){
        //Utils.translateHex(EnchantsPlus.config.get().getString("enchants.autosmelt.displayname"));
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants."+ enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".ItemEnchantChance");
    }
    @Override
    public void accept(Event event){
        if (event instanceof BlockDropItemEvent e){
            Bukkit.getLogger().info("triggering local block drop event");
            for (Item item : e.getItems()) {
                ItemStack stack = item.getItemStack();
                HashMap<Integer, ItemStack> didntFit = e.getPlayer().getInventory().addItem(stack);
                if (!didntFit.isEmpty()){
                    //drop items on player location
                }
            }
//            for (Item item : e.getItems()) {
//                switch (item.getItemStack().getType()){
//                    case IRON_ORE:
//                        item.setItemStack(new ItemStack(Material.IRON_INGOT,multiplier));
//                        break;
//                    case GOLD_ORE:
//                        item.setItemStack(new ItemStack(Material.GOLD_INGOT,multiplier));
//                        break;
//                    case COBBLESTONE:
//                        item.setItemStack(new ItemStack(Material.STONE));
//                        break;
//                    case SAND:
//                        item.setItemStack(new ItemStack(Material.GLASS));
//                        break;
//                }
//            }
        }
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
}
