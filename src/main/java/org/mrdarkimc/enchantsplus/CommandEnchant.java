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
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.enchantList.Evasion;
import org.mrdarkimc.enchantsplus.enchants.enchantList.FarArrow;
import org.mrdarkimc.enchantsplus.enchants.enchantList.HealthBoost;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

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
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (!player.hasPermission("Enchantsplus.admin"))
                return true;
            if (!(strings.length > 0)){
                player.sendMessage(ChatColor.GRAY + "(Только админ-комманды)Список комманд:");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " <зачарование> <уровень> - дать себе книгу");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " all <уровень> - дать себе все книги на уровень");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <player> <зачарование> <уровень> - игроку книгу");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " reload - перезагрузить кеш плагина");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " info - показать инфу о чарах");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " list - список зачарований");
                player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " addAll - Добавить весь инвентарь в список разрешенных предметов");
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
                    if (strings.length > 1) {
                        Enchants.applyCustomEnchant(stack, Map.of(Enchants.VAMPIRE, Integer.parseInt(strings[1])));
                        player.getInventory().addItem(getCustomBook(Enchants.VAMPIRE, Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " vampire <уровень>");
                    break;
                case "poison":
                    if (strings.length > 1) {
                    Enchants.applyCustomEnchant(stack,Map.of(Enchants.POISON, Integer.parseInt(strings[1])));
                    player.getInventory().addItem(getCustomBook(Enchants.POISON,Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " poison <уровень>");
                    break;
                case "health":
                    if (strings.length > 1) {
                        ((HealthBoost)Enchants.HEALTHBOOST).enchantStack(stack,Enchants.HEALTHBOOST,Integer.parseInt(strings[1]));
                        player.getInventory().addItem(getCustomBook(Enchants.HEALTHBOOST,Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " health <уровень>");
                    break;
                case "evasion":
                    if (strings.length > 1) {
                        ((Evasion)Enchants.EVASION).enchantStack(stack,Enchants.EVASION,Integer.parseInt(strings[1]));
                        player.getInventory().addItem(getCustomBook(Enchants.EVASION,Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " evasion <уровень>");
                    break;
                case "fararrow":
                    if (strings.length > 1) {
                        ((FarArrow)Enchants.FARARROW).enchantStack(stack,Enchants.FARARROW,Integer.parseInt(strings[1]));
                        player.getInventory().addItem(getCustomBook(Enchants.FARARROW,Integer.parseInt(strings[1])));
                    }else player.sendMessage(ChatColor.GRAY + "/" + command.getName() + " fararrow <уровень>");
                    break;
                case "reload":
                    Reloadable.reloadAll();
                    EnchantsPlus.config.reloadConfig();
                    player.sendMessage(ChatColor.GREEN + "[StackableEnchants] конфиг перезагружен");
                    break;
                case "addAll":
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
                    EnchantsPlus.config.get().set("enchants.dozer.allowedMaterials",materials);
                    EnchantsPlus.config.saveConfig();
                    break;
                case "give":
                    if (strings.length > 3) {
                        Player giveTo = Bukkit.getPlayer(strings[1]);
                        if (giveTo !=null){
                            Enchantment enc;
                            switch (strings[2]){
                                case "dozer":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.DOZER,1));
                                    break;
                                case "autosmelt":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.AUTOSMELT,1));
                                    break;
                                case "magnet":
                                    giveTo.getInventory().addItem(getCustomBook(Enchants.MAGNET,1));
                                    break;
                                case "vampire":
                                    enc = Enchants.VAMPIRE;
                                            giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    break;
                                case "health":
                                    enc = Enchants.HEALTHBOOST;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    break;
                                case "evasion":
                                    enc = Enchants.EVASION;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    break;
                                case "fararrow":
                                    enc = Enchants.FARARROW;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    break;
                                case "poison":
                                    enc = Enchants.POISON;
                                    giveTo.getInventory().addItem(getCustomBook(enc, Integer.parseInt(strings[3])));
                                    break;
                            }
                        }
                    }else commandSender.sendMessage(ChatColor.GRAY + "/" + command.getName() + " give <игрок> <зачарование> <уровень>");
                    break;
                case "toString":
                    player.sendMessage(player.getInventory().getItemInMainHand().toString());
                    player.sendMessage("Enchs: " + Enchantment.DAMAGE_ARTHROPODS.conflictsWith(Enchantment.DAMAGE_UNDEAD));
                    break;
                case "enchant":
                    ItemStack stack1 =  player.getInventory().getItemInMainHand();
                    stack1.addEnchantment(Enchants.EVASION,3);
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
                    player.getInventory().addItem(getCustomBook(Enchants.HEALTHBOOST,Integer.parseInt(strings[1])));
                    player.getInventory().addItem(getCustomBook(Enchants.EVASION,Integer.parseInt(strings[1])));
                    player.getInventory().addItem(getCustomBook(Enchants.FARARROW,Integer.parseInt(strings[1])));
                    break;
            }
        return true;
    }
}
