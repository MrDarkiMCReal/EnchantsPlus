package org.mrdarkimc.enchantsplus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Dozer;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Evasion;
import org.mrdarkimc.enchantsplus.enchants.enchantList.FarArrow;
import org.mrdarkimc.enchantsplus.enchants.enchantList.HealthBoost;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    void doEnchantCommand(Player player, ItemStack stack, Enchantment enc, int level){
        Debugger.chat("level2 is: " + level,2);
        if (((IEnchant)enc).getEnchantment().canEnchantItem(stack)) {
            level = Math.min(level, ((IEnchant)enc).getmaxTotalLevel());
            Debugger.chat("level3 is: " +level,2);
            ((IEnchant)enc).enchantStack(stack, enc, level);
            player.getInventory().addItem(getCustomBook(enc,level));
        }else {
            player.sendMessage(ChatColor.RED + "Предмет: " + stack.getType() + " не поддерживает это зачарование");
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = null;
        ConsoleCommandSender sender = null;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        }
        if (commandSender instanceof ConsoleCommandSender) {
            sender = (ConsoleCommandSender)commandSender;
        }
            ItemStack stack = player!= null ? player.getInventory().getItemInMainHand() : null;
            if (!commandSender.hasPermission("Enchantsplus.admin"))
                return true;
            if (!(strings.length > 0)){
                commandSender.sendMessage(ChatColor.GRAY + "(Только админ-комманды)Список комманд:");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " <зачарование> <уровень> - дать себе книгу");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " all <уровень> - дать себе все книги на уровень");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <player> <зачарование> <уровень> - игроку книгу");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " reload - перезагрузить кеш плагина");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " info - показать инфу о чарах");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " list - список зачарований");
                commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " addAll - Добавить весь инвентарь в список разрешенных предметов");
                return true;
            }

            switch (strings[0]){
                case "dozer":
                    if (player != null) {
                        doEnchantCommand(player, stack, Enchants.DOZER, (strings.length > 1) ? Integer.parseInt(strings[1]) : 1);
                        //Debugger.chat("level is: " + ((strings.length > 1) ? Integer.parseInt(strings[1]) : 1),2);
                    }
                        return true;

                case "autosmelt":
                    if (player != null) {
                        doEnchantCommand(player, stack, Enchants.AUTOSMELT, (strings.length > 1) ? Integer.parseInt(strings[1]) : 1);
                    }
                        return true;

                case "magnet":
                    if (player != null) {
                        doEnchantCommand(player, stack, Enchants.MAGNET, (strings.length > 1) ? Integer.parseInt(strings[1]) : 1);
                    }
                        return true;

                case "vampire":
                    if (player != null) {
                        doEnchantCommand(player, stack, Enchants.VAMPIRE, (strings.length > 1) ? Integer.parseInt(strings[1]) : 1);
                    }
                        return true;

                case "poison":
                    if (player != null) {
                        if (strings.length > 1) {
                            doEnchantCommand(player, stack, Enchants.POISON, Integer.parseInt(strings[1]));
                        } else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " poison <уровень>");
                    }
                        return true;

                case "health":
                    if (player != null) {
                        if (strings.length > 1) {
                            doEnchantCommand(player, stack, Enchants.HEALTHBOOST, Integer.parseInt(strings[1]));
                        } else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " health <уровень>");
                    }
                        return true;
                case "evasion":
                    if (strings.length > 1) {
                        doEnchantCommand(player,stack, Enchants.EVASION,Integer.parseInt(strings[1]));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " evasion <уровень>");
                    break;
                case "fararrow":
                    if (player != null) {
                        if (strings.length > 1) {
                            doEnchantCommand(player, stack, Enchants.FARARROW, Integer.parseInt(strings[1]));
                        } else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " fararrow <уровень>");
                    }
                    return true;
                case "reload":
                    EnchantsPlus.config.reloadConfig();
                    Reloadable.reloadAll();
                    commandSender.sendMessage(ChatColor.GREEN + "[StackableEnchants] конфиг перезагружен");
                    break;
                case "addAll":
                    if (player != null) {
                        ItemStack[] stacks = player.getInventory().getContents();
                        List<String> materials = EnchantsPlus.config.get().getStringList("enchants.dozer.allowedMaterials");
                        for (ItemStack itemStack : stacks) {
                            if (itemStack == null)
                                continue;
                            Material material = itemStack.getType();
                            if (material.equals(Material.AIR))
                                continue;
                            if (materials.contains(material.toString()))
                                continue;
                            materials.add(material.toString());
                        }

                        EnchantsPlus.config.get().set("enchants.dozer.allowedMaterials", materials);
                        EnchantsPlus.config.saveConfig();
                    }
                    break;
                case "give":
                    if (strings.length > 3) {
                        Player giveTo = Bukkit.getPlayer(strings[1]);
                        if (giveTo !=null){
                            Enchantment enc;
                            switch (strings[2]){
                                case "dozer":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
                                    return true;
                                case "autosmelt":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT,1));
                                    return true;
                                case "magnet":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.MAGNET,1));
                                    return true;
                                case "vampire":
                                    enc = Enchants.VAMPIRE;
                                            giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    return true;
                                case "health":
                                    enc = Enchants.HEALTHBOOST;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    return true;
                                case "evasion":
                                    enc = Enchants.EVASION;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    return true;
                                case "fararrow":
                                    enc = Enchants.FARARROW;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    return true;
                                case "poison":
                                    enc = Enchants.POISON;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    return true;
                            }
                        }
                    }else commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <игрок> <зачарование> <уровень>");
                    break;
                case "toString":
                    commandSender.sendMessage(player.getInventory().getItemInMainHand().toString());
                    commandSender.sendMessage("Enchs: " + Enchantment.DAMAGE_ARTHROPODS.conflictsWith(Enchantment.DAMAGE_UNDEAD));
                    return true;
                case "info":
                    Enchants.customEnchants.forEach(e -> {
                        if (e instanceof Infoable) {
                            ((Infoable)e).printInfo();
                        }
                    });
                    break;
                case "list":
                    commandSender.sendMessage(ChatColor.GRAY + "Доступные зачарования: dozer,autosmelt,magnet,vampire,poison,health,evasion,fararrow");
                    break;
                case "all":
                    if (player != null) {
                        player.getInventory().addItem(getCustomBook(Enchants.DOZER, 1));
                        player.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT, 1));
                        player.getInventory().addItem(getCustomBook(Enchants.MAGNET, 1));
                        player.getInventory().addItem(getCustomBook(Enchants.VAMPIRE, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.POISON, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.HEALTHBOOST, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.EVASION, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.FARARROW, Integer.parseInt(strings[1])));
                    }
                        break;
            }
        return true;
    }
}
