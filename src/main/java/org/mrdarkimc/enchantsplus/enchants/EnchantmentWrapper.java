package org.mrdarkimc.enchantsplus.enchants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EnchantmentWrapper extends Enchantment {
    //todo как добавить энчант.
    //Создаем класс в пакете enchantList
    //наследуемся от EnchantmentWrapper и имплементируем IEnchant
    //если нужно что то изменить в самом энчанте(относительно EnchantmentWrapper класса), то делаем просто override
    //обычно это 2 метода
    // public int getMaxLevel() {
    //        return 1;
    //    }
    //
    //
    //    @NotNull
    //    @Override
    //    public EnchantmentTarget getItemTarget() {
    //        return EnchantmentTarget.TOOL;
    //    }
    //В классе enchantList.Enchants создаем экземпляр класса который мы создали и добавляем наш энчант в конструктор в метод registerNewEnchant
    //
    ////Логика энчанта выполняется с помощью метода accept(Event event)
    //В классе EnchantLisener создаем нужное нам событие, делаем минимальные проверки (instanceof player или еще какую-то)
    //и вызываем метод handleItemStack(stack, e)
    //

    public EnchantmentWrapper(@NotNull NamespacedKey key) {
        super(key);
        this.key = key.getKey();
    }
    public String key;
    @NotNull
    @Override
    public String getName() {
        return key;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
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
        return Enchants.getTarget(itemStack).equals(this.getItemTarget()); //!itemStack.hasEnchant(this) &&
    }

    @NotNull
    @Override
    public Component displayName(int i) {
        return Component.translatable("enchantment.minecraft.bane_of_arthropods2");
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
