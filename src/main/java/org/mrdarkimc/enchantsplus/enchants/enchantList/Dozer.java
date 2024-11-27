package org.mrdarkimc.enchantsplus.enchants.enchantList;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.*;
import java.util.stream.Collectors;

public class Dozer extends EnchantmentWrapper implements IEnchant, Reloadable  {
    private String displayname = ChatColor.GRAY + "Будьдозер "; //todo fix hardcode

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(),"encantmentsplus_dozer");

    public Dozer() {
        super(key);
        deserealizeDefaults("dozer");
        Reloadable.register(this);
    }
    private static double chance = 0.3; //todo hardcode
    public double getEnchantChance(){
        return chance;
    }
    public void deserealizeDefaults(String enchant){
        //Utils.translateHex(EnchantsPlus.config.get().getString("enchants.autosmelt.displayname"));
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants."+ enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants."+ enchant + ".ItemEnchantChance");
    }
    @Override
    public void accept(Event event) {
    if (event instanceof BlockBreakEvent e){
        Player player = e.getPlayer();
        e.setCancelled(true);

        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks((Set)null, 10);
        if (lastTwoTargetBlocks.size() != 2 || !((Block)lastTwoTargetBlocks.get(1)).getType().isOccluding()) {
            return;
        }

        Block targetBlock = (Block)lastTwoTargetBlocks.get(1);
        Block adjacentBlock = (Block)lastTwoTargetBlocks.get(0);
        BlockFace face = targetBlock.getFace(adjacentBlock);
        this.breakBlocks(filterBlocks(this.getBlocks(e.getBlock(), face.toString())), player);
    }
    }
    private void breakBlocks(List<Block> blocks, Player player) {
        Block first = blocks.get(0);
        Iterator var2 = blocks.iterator();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        while(var2.hasNext()) {
            Block b = (Block)var2.next();
                b.breakNaturally(heldItem);
        }
        Collection<Entity> entities = Bukkit.getServer().getWorld(player.getWorld().getUID()).getNearbyEntities(first.getLocation(),3,3,3);
        List<Item> itemList = entities.stream().filter(entity -> entity instanceof Item).map(entity -> ((Item)entity)).collect(Collectors.toList());
//        itemList.forEach((item -> {
//            item.setCanPlayerPickup(false);
//            item.getLocation().
//            item.setVelocity();
//        }));
        Bukkit.getPluginManager().callEvent(new BlockDropItemEvent(first,first.getState(),player,itemList));

    }
    public List<Block> filterBlocks(List<Block> blocks){

        return blocks;
    }
    private List<Block> getBlocks(Block block, String face) {
        List<Block> blocks = new ArrayList();
        Location loc = block.getLocation();
        switch (face) {
            case "UP", "DOWN":
                blocks.add(block);
                blocks.add(loc.clone().add(0.0, 0.0, 1.0).getBlock());
                blocks.add(loc.clone().add(0.0, 0.0, -1.0).getBlock());
                blocks.add(loc.clone().add(1.0, 0.0, 0.0).getBlock());
                blocks.add(loc.clone().add(1.0, 0.0, 1.0).getBlock());
                blocks.add(loc.clone().add(1.0, 0.0, -1.0).getBlock());
                blocks.add(loc.clone().add(-1.0, 0.0, 0.0).getBlock());
                blocks.add(loc.clone().add(-1.0, 0.0, 1.0).getBlock());
                blocks.add(loc.clone().add(-1.0, 0.0, -1.0).getBlock());
                break;
            case "EAST", "WEST":
                blocks.add(block);
                blocks.add(loc.clone().add(0.0, 0.0, 1.0).getBlock());
                blocks.add(loc.clone().add(0.0, 0.0, -1.0).getBlock());
                blocks.add(loc.clone().add(0.0, 1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(0.0, 1.0, 1.0).getBlock());
                blocks.add(loc.clone().add(0.0, 1.0, -1.0).getBlock());
                blocks.add(loc.clone().add(0.0, -1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(0.0, -1.0, 1.0).getBlock());
                blocks.add(loc.clone().add(0.0, -1.0, -1.0).getBlock());
                break;
            case "NORTH", "SOUTH":
                blocks.add(block);
                blocks.add(loc.clone().add(1.0, 0.0, 0.0).getBlock());
                blocks.add(loc.clone().add(-1.0, 0.0, 0.0).getBlock());
                blocks.add(loc.clone().add(0.0, 1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(1.0, 1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(-1.0, 1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(0.0, -1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(1.0, -1.0, 0.0).getBlock());
                blocks.add(loc.clone().add(-1.0, -1.0, 0.0).getBlock());
                break;
        }

        return blocks;
    }
    @Override
    public List<String> getCustomLore() {
        return List.of("arr1","arr2");
    }
    @Override
    public Enchantment getEnchantment() {
        return this;
    }

    @Override
    public String getDisplayName() {
        return displayname;
    }


    @Override
    public int getMaxLevel() {
        return 1;
    }


    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public void reload() {
        deserealizeDefaults("dozer");
    }
}
