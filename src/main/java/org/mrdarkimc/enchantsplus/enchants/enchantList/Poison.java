package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.TriggerChance;

import java.util.*;

public class Poison extends EnchantmentWrapper implements IEnchant, TriggerChance, Reloadable {
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
        deserealizeDefaults("poison");
        Reloadable.register(this);
    }
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.3; //todo hardcode
    private static double multiplier = 1;
    public static int poisonTime = 200;
    public double getEnchantChance(){
        return chance;
    }
    public double getTriggerChance(){
        return triggerChance;
    }
    public static double increaseDamage(Player player, double damage){
    int level = poisonedPlayers.get(player);
    return (damage*level) * multiplier; //((damage+0.5) * (double)level)
    }
    public void deserealizeDefaults(String enchant){
        //Utils.translateHex(EnchantsPlus.config.get().getString("enchants.autosmelt.displayname"));
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants."+ enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".ItemEnchantChance");
    }
    public void deserealizeExtra(String enchant){
        //Utils.translateHex(EnchantsPlus.config.get().getString("enchants.autosmelt.displayname"));
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants."+ enchant + ".displayname")));
        triggerChance = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".triggerChance");
        multiplier = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".damageModifier");
        chance = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".ItemEnchantChance");
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

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }


    @Override
    public void reload() {
        deserealizeDefaults("poison");
        deserealizeExtra("posion");
    }
}
