package org.mrdarkimc.enchantsplus.enchants.enchantList;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IAnvilable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.TriggerChance;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.*;

public class Poison extends Enchantment implements IEnchant, IAnvilable, TriggerChance {
    private String displayname = ChatColor.GRAY + "Ядовитый клинок "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_poison");
    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1","arr3");
    }

    public Poison() {
        super(key);
    }
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.3; //todo hardcode
    public double getEnchantChance(){
        return chance;
    }
    public double getTriggerChance(){
        return triggerChance;
    }
    public static double increaseDamage(Player player, double damage){
    int level = poisonedPlayers.get(player);
    return ((damage+0.5) * (double)level);
    }
    public static Map<Player, Integer> poisonedPlayers = new HashMap<>();
    @Override
    public void accept(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getEntity() instanceof Player victim) {
                if (!poisonedPlayers.containsKey(victim)){
                    if (Math.random() > triggerChance) {
                        poisonPlayer(victim, ((Player) e.getDamager()).getInventory().getItemInMainHand().getEnchantLevel(this));
                    }

                }


            }
        }
    }

    public void poisonPlayer(Player player, int levelOfEnchantment){
        poisonedPlayers.put(player, levelOfEnchantment);
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,200,1,true)); //todo hardcode
        new BukkitRunnable(){

            @Override
            public void run() {
            poisonedPlayers.remove(player);
            }
        }.runTaskLaterAsynchronously(EnchantsPlus.getInstance(),200); //todo hardcode
    }

    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @NotNull
    @Override
    public String getName() {
        return key.getKey();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
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
        return !itemStack.hasEnchant(this) && Enchants.getTarget(itemStack).equals(this.getItemTarget());
    }

    @NotNull
    @Override
    public Component displayName(int i) {
        return Component.text(displayname);
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
