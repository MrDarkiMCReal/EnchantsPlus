package org.mrdarkimc.enchantsplus.enchants.enchantList;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IAnvilable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoSmelt extends Enchantment implements IEnchant, IAnvilable {
    private String displayname = ChatColor.GRAY + "Автоплавка "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_autosmelt");
    @Override
    public String getDisplayName() {
        return displayname;
    }
    @Override
    public String getDisplayLevel() {
        return "I";
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1","arr3");
    }

    public AutoSmelt() {
        super(key);
    }
    @Override
    public void accept(Event event){
        if (event instanceof BlockDropItemEvent){
            BlockDropItemEvent e = ((BlockDropItemEvent)event);
            ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
            int multiplier = 1;
            if (stack.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)){
                int level = stack.getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS);
                double chanceOfGettingMultiplied = ((double) 1 /(level+2)); //0.3 0.2 etc
                multiplier = (int) ((Math.random() < chanceOfGettingMultiplied) ? (level + 1) : 1);
            }
            for (Item item : e.getItems()) {
                switch (item.getItemStack().getType()){
                    case IRON_ORE:
                        item.setItemStack(new ItemStack(Material.IRON_INGOT,multiplier));
                        break;
                    case GOLD_ORE:
                        item.setItemStack(new ItemStack(Material.GOLD_INGOT,multiplier));
                        break;
                    case COBBLESTONE:
                        item.setItemStack(new ItemStack(Material.STONE));
                        break;
                    case SAND:
                        item.setItemStack(new ItemStack(Material.GLASS));
                        break;
                }
            }
        }
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return key.getKey();
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
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
        return true;
    }

    @NotNull
    @Override
    public Component displayName(int i) {
        return Component.text(ChatColor.GRAY + displayname + "I");
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

}
