package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Poison;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class EnchantListener implements Listener {
    @EventHandler
    void onEnchant(EnchantItemEvent event) {
        ItemStack stack = event.getItem();
        double globalChance = 0.3; //todo hardcode
        if (Math.random() < globalChance) {
            doEnchant(stack,Enchants.getTarget(stack));
        }

    }
    private void doEnchant(ItemStack stack, EnchantmentTarget target){
        IEnchant customEnchant = Randomizer.chooseObject(Enchants.getEnchants().stream().filter((enchant -> enchant.getEnchantment().getItemTarget().equals(target))).collect(Collectors.toList()));
        int level = customEnchant.getEnchantment().getMaxLevel() > 1 ? ThreadLocalRandom.current().nextInt(1,customEnchant.getEnchantment().getMaxLevel()) : 1;
        Enchants.applyCustomEnchant(stack,customEnchant.getEnchantment(),level);
    }
    private Set<AnvilInventory> anvils = new HashSet<>();
    ////todo добавить энчант который блочит урон от магии
    @EventHandler
    void onAnvil(PrepareAnvilEvent e) {
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
        for (Enchantment enchantment : enchantsMap.keySet()) {
            if (enchantment instanceof IEnchant) {
                if (enchantment.canEnchantItem(cloned)) {
                    Enchants.applyCustomEnchant(cloned, enchantment, enchantsMap.get(enchantment));
                }

            }
        }
        if (!cloned.equals(inv.getFirstItem())) {
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
            return;
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

//    @EventHandler
//    void onClick(InventoryClickEvent e) {
//        if (e.getInventory() instanceof AnvilInventory) {
//            AnvilInventory anvil = (AnvilInventory) e.getInventory();
//            ItemStack result = anvil.getResult();
//            HumanEntity player = e.getWhoClicked();
//            Bukkit.getLogger().info("result is: " + result.toString());
//            InventoryView view = e.getView().getPlayer().getOpenInventory();
//            if (e.getCurrentItem().equals(result)) {
//                Bukkit.getLogger().info("trigger equals");
//                player.setItemOnCursor(result);
//            }
//
//        }
//    }

    public void handleItemStack(ItemStack stack, Event e) {
        List<Enchantment> enchantmentsOrder = List.of(Enchants.DOZER, Enchants.AUTOSMELT, Enchants.MAGNET, Enchants.VAMPIRE, Enchants.POISON);
        for (Enchantment enchantment : enchantmentsOrder) {
            Set<Enchantment> enchantmentIntegerMap = stack.getEnchantments().keySet();
            if (enchantmentIntegerMap.contains(enchantment)){
                ((IEnchant) enchantment).accept(e);
            }
        }
//        stack.getEnchantments().forEach((enchant, level) -> {
//            if (enchant instanceof IEnchant) {
//                ((IEnchant) enchant).accept(e);
//                Bukkit.getLogger().info("trigger-1");
//                //todo переделать на List<IEnchant> и в определенном порядке триггерить энчанты
        //или через IEnchant getPriority
//            }
//        });
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
        Bukkit.getLogger().info("Triggering main blockdrop event");
    }
    @EventHandler
    void onPoisonTick(EntityDamageEvent event){
        if (event.getEntity() instanceof Player player){
            if (Poison.poisonedPlayers.containsKey(player)){
                Bukkit.getLogger().info("original dmg: " + event.getDamage());
                double damage = Poison.increaseDamage(player, event.getDamage());
                Bukkit.getLogger().info("Increased dmg: " + damage);
                event.setDamage(damage);
            }
        }
    }
    @EventHandler
    void PoisonDeathListener(PlayerDeathEvent event){
        Poison.poisonedPlayers.remove(event.getEntity());
    }

    @EventHandler
    void onDamage(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player player) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            handleItemStack(stack, e);
        }
    }
}
