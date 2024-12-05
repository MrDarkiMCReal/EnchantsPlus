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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.mrdarkimc.SatanicLib.Debugger;
import org.mrdarkimc.SatanicLib.Utils;
import org.mrdarkimc.enchantsplus.EnchantsPlus;
import org.mrdarkimc.enchantsplus.enchants.EnchantmentWrapper;
import org.mrdarkimc.enchantsplus.enchants.Enchants;
import org.mrdarkimc.enchantsplus.enchants.interfaces.IEnchant;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Infoable;
import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

import java.util.*;
import java.util.stream.Collectors;

public class Dozer extends EnchantmentWrapper implements IEnchant, Reloadable, Infoable {

    public static final NamespacedKey key = new NamespacedKey(EnchantsPlus.getInstance(), "encantmentsplus_dozer");
    private String displayname = ChatColor.GRAY + "Будьдозер "; //todo fix hardcode
    private static double chance = 0.3; //todo hardcode
    private List<Material> allowedMaterials = new ArrayList<>();

    public Dozer() {
        super(key);
        deserealize("dozer");
        Reloadable.register(this);
    }

    public double getEnchantChance() {
        return chance;
    }

    public void deserealize(String enchant) {
        this.displayname = PlaceholderAPI.setPlaceholders(null, Utils.translateHex(EnchantsPlus.config.get().getString("enchants." + enchant + ".displayname")));
        chance = EnchantsPlus.config.get().getDouble("enchants." + enchant + ".ItemEnchantChance");
        allowedMaterials = EnchantsPlus.config.get().getStringList("enchants." + enchant + ".allowedMaterials").stream().map(Material::valueOf).collect(Collectors.toList());
    }
    @Override
    public boolean enchantStack(ItemStack stack, Enchantment enchantment, int level) {
        if (!stack.getType().toString().contains("PICKAXE"))
            return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta.getEnchants().containsKey(enchantment)) {
            if (meta.getEnchantLevel(enchantment) < level) {
                Enchants.reEnchantCustom(stack,enchantment,level);
                return true;
            }else return false;
        } else {
            Enchants.setCustomLore(meta, enchantment, level);
            meta.addEnchant(enchantment, level, true);
            stack.setItemMeta(meta);
            Enchants.setEnchantingColor(stack);
            return true;
        }
    }

    @Override
    public void accept(Event event) {
        if (event instanceof BlockBreakEvent e) {
            if (!allowedMaterials.contains(e.getBlock().getBlockData().getMaterial()))
                return;
            Player player = e.getPlayer();
            ItemStack stack = player.getInventory().getItemInMainHand();
            ItemMeta meta = stack.getItemMeta();
            if (meta instanceof Damageable && !meta.isUnbreakable()){
                ((Damageable)meta).setDamage(((Damageable)meta).getDamage()-1);
            }

            e.setCancelled(true);

            List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks((Set) null, 10);
            if (lastTwoTargetBlocks.size() != 2 || !((Block) lastTwoTargetBlocks.get(1)).getType().isOccluding()) {
                return;
            }

            Block targetBlock = (Block) lastTwoTargetBlocks.get(1);
            Block adjacentBlock = (Block) lastTwoTargetBlocks.get(0);
            BlockFace face = targetBlock.getFace(adjacentBlock);
            this.breakBlocks(this.getBlocks(e.getBlock(), face.toString()), player);
        }
    }
    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        if (itemStack.getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            meta.getStoredEnchants().forEach((k,v) -> k.getItemTarget().equals(this.getItemTarget()));
            Set<Enchantment> encs = meta.getStoredEnchants().keySet();
            Set<EnchantmentTarget> targets = encs.stream().map(Enchantment::getItemTarget).collect(Collectors.toSet());
            return targets.contains(EnchantmentTarget.TOOL) || targets.contains(EnchantmentTarget.BREAKABLE);
        }
        return itemStack.getType().toString().contains("PICKAXE"); //!itemStack.hasEnchant(this) &&
    }

    private void breakBlocks(List<Block> blocks, Player player) {
        Block first = blocks.get(0);
        Iterator var2 = blocks.iterator();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        while (var2.hasNext()) {
            Block b = (Block) var2.next();
            Debugger.chat("Material: " + b.getBlockData().getMaterial(),4);
            if (allowedMaterials.contains(b.getBlockData().getMaterial())) {
                b.breakNaturally();
                //вот тут нужно ломать блок только если он быстро ломается киркой
                //например если b = камень, то ломаем. Если например b = дуб или другой блок который быстро не ломается киркой, то не ломаем блок
            }
        }

        Collection<Entity> entities = Bukkit.getServer().getWorld(player.getWorld().getUID()).getNearbyEntities(first.getLocation(), 3, 3, 3);
        List<Item> itemList = entities.stream().filter(entity -> entity instanceof Item).map(entity -> ((Item) entity)).collect(Collectors.toList());

        Bukkit.getPluginManager().callEvent(new BlockDropItemEvent(first, first.getState(), player, itemList));
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
        return List.of("arr1", "arr2");
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
        deserealize("dozer");
    }

    @Override
    public void printInfo() {
        Debugger.chat("[Dozer] Cached displayname: " + displayname,1);
        Debugger.chat("[Dozer] Cached enchant chance: " + chance,1);
    }
}
