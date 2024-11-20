package org.mrdarkimc.enchantsplus.enchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoSmelt extends Enchantment implements IEnchant {
    private String displayname = ChatColor.GRAY + "Автоплавка";
    private int maxlevel;
    private int startlevel;

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_autosmelt");
    private final List<ItemStack> allowedStacks = new ArrayList<>();
    {
        allowedStacks.add(new ItemStack(Material.COAL_ORE));
    }
    public enum AllowedMaterials {

    }


    public AutoSmelt() {
        super(key);
        //registerEnchantment(this);
    }
    @Override
    public void accept(Event event){
        Bukkit.getLogger().info("triggering2");
        if (event instanceof BlockDropItemEvent){
            BlockDropItemEvent e = ((BlockDropItemEvent)event);
            for (Item item : e.getItems()) {
                switch (item.getItemStack().getType()){
                    case IRON_ORE:
                        item.setItemStack(new ItemStack(Material.IRON_INGOT));
                        break;
                    case GOLD_ORE:
                        item.setItemStack(new ItemStack(Material.GOLD_INGOT));
                        break;
                }
            }
        }
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }

    public static void registerEnchantment(Enchantment enchantment) throws IllegalArgumentException {
        try {
            Field field = Enchantment.class.getDeclaredField("byKey");
            field.setAccessible(true);
            field.set(key, enchantment);
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось зарегистрировать зачарование: " + key);
        }
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
