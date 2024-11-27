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
import org.bukkit.scheduler.BukkitRunnable;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Poison;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class EnchantListener implements Listener, Reloadable {
    @EventHandler
    void onEnchant(EnchantItemEvent event) {
        ItemStack stack = event.getItem();
        double globalChance = 0.3; //todo hardcode
        if (Math.random() < globalChance) {
            doEnchant(stack,event.getEnchantsToAdd().keySet().stream().findFirst().get().getItemTarget()); //Enchants.getTarget(stack)
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
        boolean hasBeenModified = false;
        for (Enchantment enchantment : enchantsMap.keySet()) {
            if (enchantment instanceof IEnchant) {
                if (enchantment.canEnchantItem(cloned)) {
                    Enchants.applyCustomEnchant(cloned, enchantment, enchantsMap.get(enchantment));
                    hasBeenModified = true;
                }
            }else {
                ItemMeta clonedMeta =  cloned.getItemMeta();
                clonedMeta.addEnchant(enchantment,enchantsMap.get(enchantment),true);
                cloned.setItemMeta(meta);
                hasBeenModified = true;
            }
        }
        if (hasBeenModified) {
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


    public void handleItemStack(ItemStack stack, Event e) {
        List<Enchantment> enchantmentsOrder = List.of(Enchants.DOZER, Enchants.AUTOSMELT, Enchants.MAGNET, Enchants.VAMPIRE, Enchants.POISON); //ordered list!!!!!
        for (Enchantment enchantment : enchantmentsOrder) {
            Set<Enchantment> enchantmentIntegerMap = stack.getEnchantments().keySet();
            if (enchantmentIntegerMap.contains(enchantment)){
                ((IEnchant) enchantment).accept(e);
            }
        }
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
                event.setDamage(damage); //todo is this really necessary
                if (damage+0.5 > 1){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.damage(damage);
                        }
                    }.runTaskLater(EnchantsPlus.getInstance(),10);
                }
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
    public void deserealize(){
        EnchantsPlus.config.get().getDouble("global.customEnchantChance");
    }

    @Override
    public void reload() {
        deserealize();
    }
}
