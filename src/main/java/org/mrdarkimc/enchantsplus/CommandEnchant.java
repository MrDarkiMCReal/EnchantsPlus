package org.mrdarkimc.enchantsplus;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;

import java.util.List;

public class CommandEnchant implements CommandExecutor {
    public ItemStack getCustomBook(Enchantment enchantment, int level) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
        meta.setLore(List.of(((IEnchant) enchantment).getDisplayName() + ((IEnchant) enchantment).getDisplayLevel()));

        storedEnc.addStoredEnchant(enchantment,level,true);
            stack.setItemMeta(storedEnc);
            return stack;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            ItemStack stack = player.getInventory().getItemInMainHand();
            Enchants.applyCustomEnchant(stack,Enchants.DOZER,1);
            player.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
            player.updateInventory();

        }
        return true;
    }
}
