package org.mrdarkimc.enchantsplus.anvilHandler;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class AnvilRecipeListener implements Listener {

    private static Queue<AnvilRecipe> anvilRecipes = new ArrayDeque<>();
    private Set<AnvilInventory> anvils = new HashSet<>();
    private Random rand = new Random();

    public AnvilRecipeListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public static void addRecipe(AnvilRecipe recipe) {
        anvilRecipes.add(recipe);
    }
    @EventHandler
    public void onAnvilCraft(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
        if (inv.getItem(0) == null || inv.getItem(1) == null)
            return;
        ItemStack material = inv.getItem(0);
        ItemStack ingredient = inv.getItem(1);
        for (AnvilRecipe recipe : anvilRecipes) {
            if (!recipe.getStack().isSimilar(material))
                continue;
            if (recipe.isExactIngredients()) {
                for (ItemStack tempItem : recipe.getIngredients())
                    if (ingredient.isSimilar(tempItem)) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null)
                            result = recipe.getFunction().apply(e);
                        if (!inv.getRenameText().equals(ChatColor.stripColor(material.hasItemMeta() ? material.getItemMeta().getDisplayName() : ""))) {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(inv.getRenameText());
                            result.setItemMeta(meta);
                        }
                        e.setResult(result);
                        anvils.add(inv);
                        return;
                    }
            } else {
                for (ItemStack tempItem : recipe.getIngredients())
                    if (tempItem.getType() == ingredient.getType()) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null)
                            result = recipe.getFunction().apply(e);
                        if (!inv.getRenameText().equals(ChatColor.stripColor(material.hasItemMeta() ? material.getItemMeta().getDisplayName() : ""))) {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(inv.getRenameText());
                            result.setItemMeta(meta);
                        }
                        e.setResult(result);
                        anvils.add(inv);
                        return;
                    }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(InventoryClickEvent e) {
        if (e.isCancelled())
            return;
        if (e.getWhoClicked().getItemOnCursor().getType() == Material.AIR && e.getClickedInventory() != null && anvils.remove(e.getClickedInventory()) && e.getRawSlot() == 2) {
            AnvilInventory inv = (AnvilInventory) e.getClickedInventory();
            ItemStack result = inv.getItem(2);
            e.getWhoClicked().setItemOnCursor(result);
            for (ItemStack item : inv.getContents())
                item.setAmount(0);
            inv.setItem(2, new ItemStack(Material.AIR));
            Block block = inv.getLocation().getBlock();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1, 1);
            if (rand.nextInt(3) == 0 && !isPlayerImmune((Player) e.getWhoClicked())) {
                String data = block.getBlockData().getAsString();
                switch (block.getType()) {
                    case ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("anvil", "chipped_anvil")));
                        break;
                    case CHIPPED_ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("chipped_anvil", "damaged_anvil")));
                        break;
                    case DAMAGED_ANVIL:
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1, 1);
                        block.breakNaturally(new ItemStack(Material.AIR));
                        break;
                    default:
                        break;
                }
            }
            return;
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e) {
        anvils.remove(e.getInventory());
    }
    private boolean isPlayerImmune(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR;
    }
}
