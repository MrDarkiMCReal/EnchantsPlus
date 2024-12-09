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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoSmelt extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {

    private String displayName = ChatColor.GRAY + "Автоплавка "; // TODO: fix hardcode
    private static double chance = 0.3; // TODO: fix hardcode
    private int maxlevel = 1;

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_autosmelt");

    public AutoSmelt() {
        super(key);
        deserialize("autosmelt");
        Reloadable.register(this);
    }
    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return blockedEnchantsments.contains(enchantment);
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
            return targets.contains(EnchantmentTarget.TOOL) || targets.contains(EnchantmentTarget.BREAKABLE);
        }
        String name = itemStack.getType().toString();
        return name.contains("PICKAXE"); //!itemStack.hasEnchant(this) &&
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
        maxlevel = EnchantsPlus.config.get().getInt("enchants." + enchant + ".maxNaturalLevel");
        blockedEnchantsments.clear();
        if (EnchantsPlus.config.get().contains("enchants.autosmelt.conflictsWith") ){
            List<String> list = EnchantsPlus.config.get().getStringList("enchants.autosmelt.conflictsWith");
            list.forEach(s -> blockedEnchantsments.add(Enchantment.getByName(s.toUpperCase())));
        }
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
        return maxlevel;
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
