package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AnvilListener implements Listener {
    private Set<AnvilInventory> anvils = new HashSet<>();
    @EventHandler
    void onAnvil(PrepareAnvilEvent event) {
        handleAnvil(event);
    }
    public void handleAnvil(PrepareAnvilEvent e){
        //Debugger.chat("Triggering local PrepareAnvilEvent",4);
        AnvilInventory inv = e.getInventory();
        if (inv.getItem(0) == null || inv.getItem(1) == null)
            return;
        ItemStack cloned = inv.getFirstItem().clone();
        ItemStack book = inv.getSecondItem();

        if (book == null || !book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        ItemMeta meta = book.getItemMeta();
        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
        Map<Enchantment, Integer> enchantsMap = storedEnc.getStoredEnchants();
        boolean hasBeenModified = false;
        boolean hasCustomEnchants = false;
        for (Enchantment enchantment : enchantsMap.keySet()) {
            if (enchantment instanceof IEnchant) {
                hasCustomEnchants = true;
            }
        }
        if (!hasCustomEnchants){
            return;
        }
        if (Enchants.applyCustomEnchant(cloned,enchantsMap)!= null){
            //cloned.getItemMeta().displayName(cloned.getItemMeta().displayName().color(TextColor.color(52221)));
            hasBeenModified = true;
        }





//        for (Enchantment enchantment : enchantsMap.keySet()) {
//            if (!(enchantment instanceof IEnchant)) {
//                if (enchantment.canEnchantItem(cloned)) {
//                    clonedMeta.addEnchant(enchantment, enchantsMap.get(enchantment), true);
//                    hasBeenModified = true;
//                    cloned.setItemMeta(clonedMeta);
//                }
//            }
//        }

//
//        for (Enchantment enchantment : enchantsMap.keySet()) {
//            if (enchantment instanceof IEnchant) {
//                if (enchantment.canEnchantItem(cloned)) {
//                    Enchants.applyCustomEnchant(cloned, enchantment, enchantsMap.get(enchantment));
//                    hasBeenModified = true;
//                }
//            }else {
//                if (enchantment.canEnchantItem(cloned)) {
//                    ItemMeta clonedMeta = cloned.getItemMeta();
//                    clonedMeta.addEnchant(enchantment, enchantsMap.get(enchantment), true);
//                    hasBeenModified = true;
//                    cloned.setItemMeta(clonedMeta);
//                }
//            }
//        }
//todo он триггерится 4 раща
        //Debugger.chat("final",4);
        if (hasBeenModified) { //merge with  (hasCustomEnchants && Enchants.applyCustomEnchant(cloned,enchantsMap)!= null){
            inv.setRepairCost(10);
           // Debugger.chat("Enchant has been modified. Setting repair cost" ,4);
            e.setResult(cloned);
            anvils.add(e.getInventory());

        }

    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(InventoryClickEvent e) {
        if (!(e.getInventory() instanceof AnvilInventory))
            return;
        if (e.isCancelled())
            return;
        if (e.getCurrentItem()==null)
            return;
        if (e.getWhoClicked().getItemOnCursor().getType() == Material.AIR && e.getClickedInventory() != null && anvils.remove(e.getClickedInventory()) && e.getRawSlot() == 2) {

            AnvilInventory inv = (AnvilInventory) e.getClickedInventory();
            ItemStack result = inv.getItem(2);
            e.getWhoClicked().setItemOnCursor(result);
            for (ItemStack item : inv.getContents())
                item.setAmount(0);
            inv.setItem(2, new ItemStack(Material.AIR));
            ((Player) e.getView().getPlayer()).updateInventory();
            Block block = inv.getLocation().getBlock();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1, 1);
            if (new Random().nextInt(3) == 0 && !isPlayerImmune((Player) e.getWhoClicked())) {
                String data = block.getBlockData().getAsString();
                switch (block.getType()) {
                    case ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("anvil", "chipped_anvil")));
                        break;
                    case CHIPPED_ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("chipped_anvil", "damaged_anvil")));
                        break;
                    case DAMAGED_ANVIL:
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1, 1);
                        block.breakNaturally(new ItemStack(Material.AIR));
                        break;
                    default:
                        break;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e) {
        anvils.remove(e.getInventory());
    }
    private boolean isPlayerImmune(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR;
    }
}
