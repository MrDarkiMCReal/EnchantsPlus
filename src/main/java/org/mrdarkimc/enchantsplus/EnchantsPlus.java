package org.mrdarkimc.enchantsplus;

import org.bukkit.plugin.java.JavaPlugin;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.SatanicLib;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.SatanicLib.configsetups.Configs;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.listeners.AnvilListener;
import org.mrdarkimc.enchantsplus.listeners.EnchantsLogic;
import org.mrdarkimc.enchantsplus.listeners.ItemEnchantListener;

import java.util.HashSet;
import java.util.Set;

public final class EnchantsPlus extends JavaPlugin implements Reloadable {
    public final Set<IEnchant> listOfEnchants = new HashSet<>();
    private static EnchantsPlus instance;
    public static Configs config;

    public static EnchantsPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        SatanicLib.setupLib(this);
        config = Configs.Defaults.setupConfig();
        setUpEnchants();
        getServer().getPluginManager().registerEvents(new EnchantsLogic(),this);
        getServer().getPluginManager().registerEvents(new AnvilListener(),this);
        getServer().getPluginManager().registerEvents(new ItemEnchantListener(),this);
        getServer().getPluginCommand("EP").setExecutor(new CommandEnchant());
        new Debugger();
        Reloadable.register(this);
        Utils.startUp("StackableEnchants");
        //new AnvilRecipeListener(instance);
        //new AnvilRecipes();

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

    @Override
    public void reload() {
        config.loadConfig();
        config.reloadConfig();
    }
}
