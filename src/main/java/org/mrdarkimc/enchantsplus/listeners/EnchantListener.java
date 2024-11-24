package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.SatanicLib.ItemStackUtils.StackUtils;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.purpurmc.purpur.event.inventory.AnvilTakeResultEvent;

import java.util.Map;


public class EnchantListener implements Listener {
    @EventHandler
    void onEnchant(EnchantItemEvent event) {
        Enchants.applyCustomEnchant(event.getItem(), Enchants.AUTOSMELT, 1);
    }

    @EventHandler
    void onAnvil(PrepareAnvilEvent e) {

        AnvilInventory inventory = e.getInventory();
        if (inventory.getFirstItem() == null)
            return;
        if (inventory.getSecondItem() == null)
            return;
        ItemStack result = inventory.getFirstItem().clone();
        ItemStack book = inventory.getSecondItem();
        Bukkit.getLogger().info("triggers anvil event");

        if (book == null || !book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        Bukkit.getLogger().info("pass null and enc book");
        Bukkit.getLogger().info(book.toString());
        ItemMeta meta = book.getItemMeta();
        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
        Map<Enchantment, Integer> enchantsMap = storedEnc.getStoredEnchants();
        for (Enchantment enchantment : enchantsMap.keySet()) {
            if (enchantment instanceof IEnchant) {
                result = Enchants.applyCustomEnchant(result, enchantment, enchantsMap.get(enchantment));
            }
        }
        inventory.setRepairCost(5);
        inventory.setDoUnsafeEnchants(true);
        inventory.setMaximumRepairCost(7);
        inventory.setResult(result);
        ((Player) e.getView().getPlayer()).updateInventory();
    }



    @EventHandler
    void onClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvil = (AnvilInventory) e.getInventory();
            ItemStack result = anvil.getResult();
            HumanEntity player = e.getWhoClicked();
            Bukkit.getLogger().info("result is: " + result.toString());
            InventoryView view = e.getView().getPlayer().getOpenInventory();
            if (e.getCurrentItem().equals(result)) {
                Bukkit.getLogger().info("trigger equals");
                player.setItemOnCursor(result);
            }

        }
    }

    public void handleItemStack(ItemStack stack, Event e) {
        stack.getEnchantments().forEach((enchant, level) -> {
            if (enchant instanceof IEnchant) {
                ((IEnchant) enchant).accept(e);
                Bukkit.getLogger().info("trigger-1");
            }
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
        handleItemStack(stack, e);
    }

    @EventHandler
    public void onBlockBreak(BlockDropItemEvent e) {
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
        handleItemStack(stack, e);
    }
}
