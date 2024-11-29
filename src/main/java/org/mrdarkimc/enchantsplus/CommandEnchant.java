package org.mrdarkimc.enchantsplus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;
import org.mrdarkimc.enchantsplus.utils.Randomizer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandEnchant implements CommandExecutor {
    public static ItemStack getCustomBook(Enchantment enchantment, int level) {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
        EnchantmentStorageMeta storedEnc = (EnchantmentStorageMeta) meta;
        meta.setLore(List.of(((IEnchant) enchantment).getDisplayName() + Enchants.getDisplayLevel(level)));

        storedEnc.addStoredEnchant(enchantment,level,true);
            stack.setItemMeta(storedEnc);
            return stack;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (!player.hasPermission("Enchantsplus.admin"))
                return true;
            if (!(strings.length > 1)){
                player.sendMessage(ChatColor.GRAY + "(Только админ-комманды)Список комманд:");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " <зачарование> <уровень> - дать себе книгу");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " all <уровень> - дать себе все книги на уровень");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <player> <зачарование> <уровень> - игроку книгу");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " reload - перезагрузить кеш плагина");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " info - показать инфу о чарах");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " list - список зачарований");
                return true;
            }

            switch (strings[0]){
                case "dozer":
                    if (Enchants.DOZER.canEnchantItem(stack)) {
                        Enchants.applyCustomEnchant(stack, Map.of(Enchants.DOZER, 1));
                    }
                    player.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
                    break;
                case "autosmelt":
                    Enchants.applyCustomEnchant(stack,Map.of(Enchants.AUTOSMELT, 1));
                    player.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT,1));
                    break;
                case "magnet":
                    Enchants.applyCustomEnchant(stack,Map.of(Enchants.MAGNET, 1));
                    player.getInventory().addItem(getCustomBook(Enchants.MAGNET,1));
                    break;
                case "vampire":
                    if (strings.length > 2) {
                        Enchants.applyCustomEnchant(stack, Map.of(Enchants.VAMPIRE, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.VAMPIRE, Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " vampire <уровень>");
                    break;
                case "poison":
                    if (strings.length > 2) {
                    Enchants.applyCustomEnchant(stack,Map.of(Enchants.POISON, Integer.parseInt(strings[1])));
                    player.getInventory().addItem(getCustomBook(Enchants.POISON,Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " poison <уровень>");
                    break;
                case "reload":
                    Reloadable.reloadAll();
                    break;
                case "give":
                    if (strings.length > 3) {
                        Player giveTo = Bukkit.getPlayer(strings[1]);
                        if (giveTo !=null){
                            switch (strings[2]){
                                case "dozer":
                                    player.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
                                    break;
                                case "autosmelt":
                                    player.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT,1));
                                    break;
                                case "magnet":
                                    player.getInventory().addItem(getCustomBook(Enchants.MAGNET,1));
                                    break;
                                case "vampire":
                                        player.getInventory().addItem(getCustomBook(Enchants.VAMPIRE, Integer.parseInt(strings[3])));
                                    break;
                                case "poison":
                                        player.getInventory().addItem(getCustomBook(Enchants.POISON,Integer.parseInt(strings[3])));
                                    break;
                            }
                        }
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <игрок> <зачарование> <уровень>");
                    break;
                case "info":
                    Enchants.customEnchants.forEach(e -> {
                        if (e instanceof Infoable) {
                            ((Infoable)e).printInfo();
                        }
                    });
                    break;
                case "list":
                    player.sendMessage(ChatColor.GRAY + "Доступные зачарования: dozer,autosmelt,magnet,vampire,poison");
                    break;
                case "all":
                    player.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
                    player.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT,1));
                    player.getInventory().addItem(getCustomBook(Enchants.MAGNET,1));
                    player.getInventory().addItem(getCustomBook(Enchants.VAMPIRE,Integer.parseInt(strings[1])));
                    player.getInventory().addItem(getCustomBook(Enchants.POISON,Integer.parseInt(strings[1])));
                    break;
            }
        }
        return true;
    }
}
