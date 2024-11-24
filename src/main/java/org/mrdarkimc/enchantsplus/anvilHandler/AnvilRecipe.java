package org.mrdarkimc.enchantsplus.anvilHandler;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnvilRecipe {

    private ItemStack stack;
    private Queue<ItemStack> ingredients;
    private ItemStack result;
    private boolean exactIngredients;
    private Function<PrepareAnvilEvent, ItemStack> function;

    public AnvilRecipe(ItemStack stack, List<ItemStack> ingredients, ItemStack result, boolean exactIngredients) {
        this.stack = stack.clone();
        this.ingredients = new ArrayDeque<>(ingredients.stream().map(k -> k.clone()).collect(Collectors.toList()));
        this.result = result.clone();
        this.exactIngredients = exactIngredients;
    }
    public void setFunction(Function<PrepareAnvilEvent, ItemStack> function) {
        this.function = function;
    }
    public Function<PrepareAnvilEvent, ItemStack> getFunction() {
        return function;
    }
    public ItemStack getStack() {
        return stack.clone();
    }
    public void setStack(ItemStack stack) {
        this.stack = stack.clone();
    }
    public Queue<ItemStack> getIngredients() {
        return ingredients;
    }
    public void setIngredients(Queue<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }
    public ItemStack getResult() {
        return result.clone();
    }
    public void setResult(ItemStack result) {
        this.result = result;
    }
    public boolean isExactIngredients() {
        return exactIngredients;
    }
    public void setExactIngredients(boolean exactIngredients) {
        this.exactIngredients = exactIngredients;
    }
}
