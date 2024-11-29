package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.List;

public class AutoSmelt extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {

    private String displayName = ChatColor.GRAY + "Автоплавка "; // TODO: fix hardcode
    private static double chance = 0.3; // TODO: fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_autosmelt");

    public AutoSmelt() {
        super(key);
        deserialize("autosmelt");
        Reloadable.register(this);
    }


    public double getEnchantChance() {
        return chance;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1", "arr3");
    }

    @Override
    public void reload() {
        deserialize("autosmelt");
    }

    public void deserialize(String enchant) {
        // Deserialize data for the enchantment
        this.displayName = PlaceholderAPI.setPlaceholders(
                null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname"))
        );
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
    }

    @Override
    public void accept(Event event) {
        if (event instanceof BlockDropItemEvent e) {
            ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
            convertItems(e.getItems(), stack);
        }
    }

    static void convertItems(List<Item> itemList, ItemStack stack) {
        for (Item item : itemList) {
            int multiplier = 1;
            if (stack.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
                int level = stack.getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS);
                double chanceOfGettingMultiplied = 1.0 / (level + 2); // 0.3, 0.2, etc.
                multiplier = (Math.random() < chanceOfGettingMultiplied) ? (level + 1) : 1;
            }

            switch (item.getItemStack().getType()) {
                case IRON_ORE:
                    item.setItemStack(new ItemStack(Material.IRON_INGOT, multiplier));
                    break;
                case GOLD_ORE:
                    item.setItemStack(new ItemStack(Material.GOLD_INGOT, multiplier));
                    break;
                case COBBLESTONE:
                    item.setItemStack(new ItemStack(Material.STONE));
                    break;
                case SAND:
                    item.setItemStack(new ItemStack(Material.GLASS));
                    break;
                default:
                    break;
            }
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
    public void printInfo() {
        Debugger.chat("[AutoSmelt] Cached displayname: " + displayName,1);
        Debugger.chat("[AutoSmelt] Cached enchant chance: " + chance,1);
    }
}
