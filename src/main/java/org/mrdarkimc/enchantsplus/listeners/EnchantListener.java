package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;



public class EnchantListener implements Listener {
    public void handleItemStack(ItemStack stack, Event e){
        stack.getEnchantments().forEach((enchant,level) -> {
            if (enchant instanceof IEnchant){
                ((IEnchant)enchant).accept(e);
                Bukkit.getLogger().info("trigger-1");
            }
        });
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
            handleItemStack(stack, e);
    }
    @EventHandler
    public void onBlockBreak(BlockDropItemEvent e){
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
        handleItemStack(stack, e);
    }
}
