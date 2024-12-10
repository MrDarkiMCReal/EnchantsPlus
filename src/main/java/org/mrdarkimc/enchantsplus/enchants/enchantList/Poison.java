package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.TriggerChance;

import java.util.*;
import java.util.stream.Collectors;

public class Poison extends EnchantmentWrapper implements IEnchant, TriggerChance, Reloadable, Infoable {

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_poison");
    private String displayname = ChatColor.GRAY + "Ядовитый клинок "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    private static double triggerChance = 0.3; //todo hardcode
    public static double multiplier = 1;
    public static int poisonTime = 200;
    private int maxTotalLEvel = 1;
    public int maxlevel = 3;
    public static Map<Player, Integer> poisonedPlayers = new HashMap<>();

    public Poison() {
        super(key);
        deserealizeDefaults("poison");
        deserealizeExtra("poison");
        Reloadable.register(this);
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
    public int getmaxTotalLevel() {
        return maxTotalLEvel;
    }

    public List<Enchantment> blockedEnchantsments = new ArrayList<>();
    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return blockedEnchantsments.contains(enchantment);
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            meta.getStoredEnchants().forEach((k,v) -> k.getItemTarget().equals(this.getItemTarget()));
            Set<Enchantment> encs = meta.getStoredEnchants().keySet();
            Set<EnchantmentTarget> targets = encs.stream().map(Enchantment::getItemTarget).collect(Collectors.toSet());
            return targets.contains(EnchantmentTarget.WEAPON) || targets.contains(EnchantmentTarget.TRIDENT);
        }
        String name = itemStack.getType().toString();
        return name.contains("SWORD") | name.contains("TRIDENT") | name.contains("_AXE") ; //!itemStack.hasEnchant(this) &&
    }

    public double getEnchantChance() {
        return chance;
    }

    public double getTriggerChance() {
        return triggerChance;
    }

    public static double increaseDamage(Player player, double damage) {
        int level = poisonedPlayers.get(player);
        return (damage * level) * multiplier; //((damage+0.5) * (double)level)
    }

    public void deserealizeDefaults(String enchant) {
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
    }

    public void deserealizeExtra(String enchant) {
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname")));
        triggerChance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".triggerChance");
        multiplier = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".damageModifier");
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
        maxlevel = EnchantsPlus.config.get().getInt("enchants." + enchant + ".maxNaturalLevel");
        maxTotalLEvel = EnchantsPlus.config.get().getInt("enchants." + enchant + ".maxTotalLevel");
        blockedEnchantsments.clear();
        if (EnchantsPlus.config.get().contains("enchants."+enchant+".conflictsWith") ){
            List<String> list = EnchantsPlus.config.get().getStringList("enchants."+enchant+".conflictsWith");
            list.forEach(s -> blockedEnchantsments.add(Enchantment.getByName(s.toUpperCase())));
        }
    }

    @Override
    public void accept(Event event) {
        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.isCancelled())
                return;
            if (e.getEntity() instanceof Player victim) {
                if (!poisonedPlayers.containsKey(victim)) {
                    Debugger.chat("Poison chance: " + triggerChance, 2);
                    double rand = ((double) Math.round((Math.random() * 100)) / 100);
                    if (rand <= triggerChance) {
                        poisonPlayer(victim, ((Player) e.getDamager()).getInventory().getItemInMainHand().getEnchantLevel(this));
                    }
                }
            }
        }
    }

    public void poisonPlayer(Player player, int levelOfEnchantment) {
        poisonedPlayers.put(player, levelOfEnchantment);
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Poison.poisonTime, 1, true));
        new BukkitRunnable() {
            @Override
            public void run() {
                poisonedPlayers.remove(player);
            }
        }.runTaskLaterAsynchronously(EnchantsPlus.getInstance(), Poison.poisonTime);
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }

    @Override
    public List<String> getCustomLore() {
        return List.of("arr1", "arr3");
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
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public void reload() {
        deserealizeDefaults("poison");
        deserealizeExtra("poison");
    }

    @Override
    public void printInfo() {
        Debugger.chat("[Poison] Cached displayname: " + displayname,1);
        Debugger.chat("[Poison] Cached enchant chance: " + chance,1);
        Debugger.chat("[Poison] Cached trigger chance: " + triggerChance,1);
        Debugger.chat("[Poison] Cached multiplier: " + multiplier,1);
        Debugger.chat("[Poison] Cached poisontime: " + poisonTime,1);
    }
}
