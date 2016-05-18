package com.gmail.trentech.wirelessred.utils;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapedRecipe.Builder;

import com.flowpowered.math.vector.Vector2i;
import com.gmail.trentech.wirelessred.Main;

public class RecipeHelper {

	public static ShapedRecipe getTransmitter(){
		Builder builder = Main.getGame().getRegistry().createBuilder(ShapedRecipe.Builder.class);
		builder.dimensions(new Vector2i(3,3));
		
		builder.ingredient(1, 1, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(1, 2, ItemStack.builder().itemType(ItemTypes.REDSTONE_TORCH).build());
		builder.ingredient(1, 3, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(2, 1, ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).build());
		builder.ingredient(2, 2, ItemStack.builder().itemType(ItemTypes.COMPARATOR).build());
		builder.ingredient(2, 3, ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).build());
		builder.ingredient(3, 1, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(3, 2, ItemStack.builder().itemType(ItemTypes.REPEATER).build());
		builder.ingredient(3, 3, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		
		return builder.build();
	}
	
	public static ShapedRecipe getReceiver(){
		Builder builder = Main.getGame().getRegistry().createBuilder(ShapedRecipe.Builder.class);
		builder.dimensions(new Vector2i(3,3));
		
		builder.ingredient(1, 1, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(1, 2, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(1, 3, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(2, 1, ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).build());
		builder.ingredient(2, 2, ItemStack.builder().itemType(ItemTypes.REDSTONE_TORCH).build());
		builder.ingredient(2, 3, ItemStack.builder().itemType(ItemTypes.GOLD_INGOT).build());
		builder.ingredient(3, 1, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(3, 2, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(3, 3, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		
		return builder.build();
	}
	
	public static ShapedRecipe getTool(){
		Builder builder = Main.getGame().getRegistry().createBuilder(ShapedRecipe.Builder.class);
		builder.dimensions(new Vector2i(2,2));
		
		builder.ingredient(1, 1, ItemStack.builder().itemType(ItemTypes.REDSTONE_ORE).build());
		builder.ingredient(2, 2, ItemStack.builder().itemType(ItemTypes.STICK).build());
		
		return builder.build();
	}
}
