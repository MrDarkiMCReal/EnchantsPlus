package org.mrdarkimc.enchantsplus.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
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
import org.jetbrains.annotations.Debug;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.CommandEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.handlers.Enchantable;
import org.mrdarkimc.enchantsplus.handlers.Handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class AnvilListener implements Listener {
    private Set<AnvilInventory> anvils = new HashSet<>();
    @EventHandler
    void onAnvil(PrepareAnvilEvent event) {
        handleAnvil3(event);
    }
    public void handleAnvil(PrepareAnvilEvent e) {
        //Debugger.chat("Triggering local PrepareAnvilEvent",4);
        AnvilInventory inv = e.getInventory();

        if (inv.getItem(0) == null || inv.getItem(1) == null)
            return;
        ItemStack cloned = inv.getFirstItem().clone();
        ItemStack book = inv.getSecondItem();

        if (book == null || !book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        //
//        if (cloned.getType().equals(Material.ENCHANTED_BOOK)) {
//            //mergeBooks(cloned,book,e);
//        return;
//        }
        boolean hasBeenEnchanted = false;
        boolean hasCustomEnchantInIt = false;
        ItemMeta meta = book.getItemMeta();
        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
        Map<Enchantment, Integer> enchantsMap = storedEnc.getStoredEnchants();
        ////////////////////////////////////////////////////
        for (Enchantment enchantment : enchantsMap.keySet()) {
            if (enchantment instanceof IEnchant) {
                if (enchantment.canEnchantItem(cloned)) {
                    hasBeenEnchanted = ((IEnchant) enchantment).enchantStack(cloned, enchantment, enchantsMap.get(enchantment));
                    hasCustomEnchantInIt = true;
                }
            } else {
                if (enchantment.canEnchantItem(cloned)) {

                    hasBeenEnchanted = doDefaultEnchant(cloned, enchantment, enchantsMap.get(enchantment));

                }
            }
        }
        if (!hasCustomEnchantInIt)
            return;
        if (!hasBeenEnchanted)
            return;
        inv.setRepairCost(10);
        // Debugger.chat("Enchant has been modified. Setting repair cost" ,4);
        e.setResult(cloned);
        anvils.add(e.getInventory());
    }
    public void handleAnvil3(PrepareAnvilEvent e) {
        //Debugger.chat("Triggering local PrepareAnvilEvent",4);
        AnvilInventory inv = e.getInventory();
        if (inv.getItem(0) !=null && inv.getItem(1) == null){ //handle renaming
            Debugger.chat("Itemname: "+ TextComponent.ofChildren(inv.getItem(0).displayName()).content(),4);
            Debugger.chat("renaming text: " + inv.getRenameText(),4);
            if (Component.translatable(inv.getItem(0).getTranslationKey()).asComponent().equals(TextComponent.ofChildren(inv.getItem(0).displayName()).content())){
                Debugger.chat("penis", 4);
            }
            if (TextComponent.ofChildren(inv.getItem(0).displayName()).content().equals(inv.getRenameText())){
                e.setResult(new ItemStack(Material.AIR));
            }else {
                ItemStack stackToRename = inv.getItem(0).clone();
                ItemMeta meta = stackToRename.getItemMeta();
                meta.displayName(Component.text(inv.getRenameText()));
                stackToRename.setItemMeta(meta);
                e.setResult(stackToRename);
            }
        }
        if (inv.getItem(0) == null || inv.getItem(1) == null)
            return;
        ItemStack cloned = inv.getFirstItem().clone();
        ItemStack book = inv.getSecondItem();

        if (book == null || !book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        Enchantable clonedEnc = Handler.newinstance(cloned);
        Enchantable bookEnc = Handler.newinstance(book);
        Debugger.chat("Inv Text: " + inv.getRenameText(),2);
        Debugger.chat("ItemText: " + cloned.displayName(),21);
        Debugger.chat("Trying to merge items",4);
        if (!TextComponent.ofChildren(inv.getItem(0).displayName()).content().equals(inv.getRenameText())){
            ItemMeta meta = cloned.getItemMeta();
            meta.displayName(Component.text(inv.getRenameText()));
            cloned.setItemMeta(meta);
        }
        if (clonedEnc.mergeWith(bookEnc)){
            Debugger.chat("Merging complite",4);
            inv.setRepairCost(calculateRepairCost());
            // Debugger.chat("Enchant has been modified. Setting repair cost" ,4);
            e.setResult(cloned);
            anvils.add(e.getInventory());
            return;
        }else {
            e.setResult(new ItemStack(Material.AIR)); //todo really?
        }
        return;
        //
//        if (cloned.getType().equals(Material.ENCHANTED_BOOK)) {
//            //mergeBooks(cloned,book,e);
//        return;
//        }
        //todo start
//        boolean hasBeenEnchanted = false;
//        boolean hasCustomEnchantInIt = false;
//        ItemMeta meta = book.getItemMeta();
//        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
//        Map<Enchantment, Integer> enchantsMap = storedEnc.getStoredEnchants();
//        ////////////////////////////////////////////////////
//        for (Enchantment enchantment : enchantsMap.keySet()) {
//            if (enchantment instanceof IEnchant) {
//                if (enchantment.canEnchantItem(cloned)) {
//                    hasBeenEnchanted = ((IEnchant) enchantment).enchantStack(cloned, enchantment, enchantsMap.get(enchantment));
//                    hasCustomEnchantInIt = true;
//                }
//            } else {
//                if (enchantment.canEnchantItem(cloned)) {
//
//                    hasBeenEnchanted = doDefaultEnchant(cloned, enchantment, enchantsMap.get(enchantment));
//
//                }
//            }
//        }
//        if (!hasCustomEnchantInIt)
//            return;
//        if (!hasBeenEnchanted)
//            return;
//        inv.setRepairCost(10);
//        // Debugger.chat("Enchant has been modified. Setting repair cost" ,4);
//        e.setResult(cloned);
//        anvils.add(e.getInventory());
        //todo end
    }
    public int calculateRepairCost(){
        return 10;
    }
//    public void mergeBooks(ItemStack firstBook, ItemStack secondBook, PrepareAnvilEvent e){
//        boolean hasCustomEnchants = false;
//        EnchantmentStorageMeta firstMeta = (EnchantmentStorageMeta) firstBook.getItemMeta(); //this one is cloned
//        EnchantmentStorageMeta secondMeta = (EnchantmentStorageMeta) secondBook.getItemMeta();
//        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : firstMeta.getStoredEnchants().entrySet()) {
//            if (enchantmentIntegerEntry.getKey() instanceof IEnchant){
//                hasCustomEnchants = true;
//                break;
//            }
//        }
//        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : secondMeta.getStoredEnchants().entrySet()) {
//            if (enchantmentIntegerEntry.getKey() instanceof IEnchant){
//                hasCustomEnchants = true;
//                break;
//            }
//        }
//        if (!hasCustomEnchants)
//            return;
//
//
//        secondMeta.getStoredEnchants().forEach((k,v) -> {
//            firstMeta.addStoredEnchant(k,v,true);
//        });
//        firstMeta
//        meta.setLore(meta.getLore().stream().filter(line -> (!line.contains(((IEnchant) enchantment).getDisplayName()))).collect(Collectors.toList())); //удаляем старый лор
//
//    }














//        boolean hasBeenModified = false;
//        boolean hasCustomEnchants = false;
//        for (Enchantment enchantment : enchantsMap.keySet()) {
//            if (enchantment instanceof IEnchant) {
//                hasCustomEnchants = true;
//            }
//        }
//        if (!hasCustomEnchants){
//            return;
//        }
//        if (Enchants.applyCustomEnchant(cloned,enchantsMap)!= null){
//            //cloned.getItemMeta().displayName(cloned.getItemMeta().displayName().color(TextColor.color(52221)));
//            hasBeenModified = true;
//        }





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
//        if (hasBeenModified) { //merge with  (hasCustomEnchants && Enchants.applyCustomEnchant(cloned,enchantsMap)!= null){
//            inv.setRepairCost(10);
//           // Debugger.chat("Enchant has been modified. Setting repair cost" ,4);
//            e.setResult(cloned);
//            anvils.add(e.getInventory());
//
//        }

    public boolean doDefaultEnchant(ItemStack stack, Enchantment enchantment, int level){
        if (!enchantment.canEnchantItem(stack))
            return false;
        ItemMeta meta = stack.getItemMeta();
//        if (meta.getEnchants().containsKey(enchantment)){
//
//        }
        meta.addEnchant(enchantment,level,true);
        stack.setItemMeta(meta);
        return true;
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
