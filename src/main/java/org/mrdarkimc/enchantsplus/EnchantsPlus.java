package org.mrdarkimc.enchantsplus;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.mrdarkimc.enchantsplus.enchants.AutoSmelt;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.listeners.EnchantListener;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public final class EnchantsPlus extends JavaPlugin {
    public final Set<IEnchant> listOfEnchants = new HashSet<>();
    private static EnchantsPlus instance;

    public static EnchantsPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        setUpEnchants();
        getServer().getPluginManager().registerEvents(new EnchantListener(),this);
        getServer().getPluginCommand("try").setExecutor(new CommandEnchant());

    }
    public void setUpEnchants() {
        new Enchants();
//        try {
//            // Попробуем зарегистрировать кастомное зачарование
//            Enchantment customEnchant = new AutoSmelt(); // Это ваш кастомный Enchantment
////            if (!Enchantment.isAcceptingRegistrations()) {
////                // Если регистрация запрещена, выводим предупреждение
////                getLogger().warning("Невозможно зарегистрировать кастомные зачарования. Возможно, сервер не поддерживает это.");
////                return;
////            }
//
//            // Используем Reflection для регистрации зачарования
//            Field field = Enchantment.class.getDeclaredField("byName");
//            field.setAccessible(true);
//            field.set(null, null); // сбрасываем кеш
//
//            // Теперь регистрируем
//            Enchantment.registerEnchantment(customEnchant);
//            getLogger().info("Кастомное зачарование " + customEnchant.getName() + " успешно зарегистрировано.");
//        } catch (Exception e) {
//            getLogger().severe("Ошибка при регистрации кастомного зачарования: " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
