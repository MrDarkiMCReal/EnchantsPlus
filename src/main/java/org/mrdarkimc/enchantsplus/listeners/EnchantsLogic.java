package org.mrdarkimc.enchantsplus.listeners;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Poison;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.*;


public class EnchantsLogic implements Listener {
    ////todo добавить энчант который блочит урон от магии

    public void handleItemStack(ItemStack stack, Event e) {
        List<Enchantment> enchantmentsOrder = List.of(Enchants.DOZER, Enchants.AUTOSMELT, Enchants.MAGNET, Enchants.VAMPIRE, Enchants.POISON); //ordered list!!!!!
        for (Enchantment enchantment : enchantmentsOrder) {
            Set<Enchantment> enchantmentIntegerMap = stack.getEnchantments().keySet();
            if (enchantmentIntegerMap.contains(enchantment)) {
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
    }

    @EventHandler
    void onPoisonTick(EntityDamageEvent event) {
        //todo вынести это в Poison class с помощью accept(e) но с разделением на инстансы (PoisonTick or DmgEvent)
        if (event.getEntity() instanceof Player player) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.POISON)) {
                if (Poison.poisonedPlayers.containsKey(player)) {
                    Debugger.chat("Original damage: " + event.getDamage(), 2);
                    Debugger.chat("Damage multiplier: " + Poison.multiplier, 2);
                    double damage = Poison.increaseDamage(player, 0.5);
                    Debugger.chat("Increased damage: " + damage, 2);
                    event.setDamage(damage); //todo is this really necessary
//                    if (damage + 0.5 > 1) {
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                player.damage(damage);
//                            }
//                        }.runTaskLater(EnchantsPlus.getInstance(), Poison.poisonTime);
//                    }
                }
            }
        }
    }

    @EventHandler
    void onMilk(PlayerItemConsumeEvent e) {
        if (e.getItem().getType().equals(Material.MILK_BUCKET)) { //todo вынести это в Poison class с помощью accept(e) но с разделением на инстансы (PoisonTick or DmgEvent)
            Poison.poisonedPlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    void PoisonDeathListener(PlayerDeathEvent event) {
        Poison.poisonedPlayers.remove(event.getEntity());
    }

    @EventHandler
    void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            ItemStack stack = player.getInventory().getItemInMainHand();
            handleItemStack(stack, e);
        }
    }
}
