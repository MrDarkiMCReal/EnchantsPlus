package org.mrdarkimc.enchantsplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.enchants.AutoSmelt;
import org.mrdarkimc.enchantsplus.enchants.Enchants;

public class CommandEnchant implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            ItemStack stack = player.getInventory().getItemInMainHand();
            Enchants.enchantItemStack(stack,new AutoSmelt(),1);
            player.updateInventory();

        }
        return true;
    }
}
