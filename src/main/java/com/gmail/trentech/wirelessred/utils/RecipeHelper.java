package com.gmail.trentech.wirelessred.utils;

import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapedRecipe.Builder;

import com.flowpowered.math.vector.Vector2i;
import com.gmail.trentech.wirelessred.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class RecipeHelper {

	public static void init() {
		ConfigurationNode config = new ConfigManager().getConfig().getNode("recipes");
		
		try {
			Main.getGame().getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("transmitter")));
			Main.getGame().getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("receiver")));
			Main.getGame().getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("tool")));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}
	}

	private static ShapedRecipe getRecipe(ConfigurationNode node) throws InvalidItemTypeException {
		Builder builder = Main.getGame().getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for(Entry<Object, ? extends ConfigurationNode> child : node.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();
			
			String key = childNode.getKey().toString();
			
			if(key.equals("grid_size")) {
				String[] size = childNode.getString().split("x");
				
				builder.dimensions(new Vector2i(Integer.parseInt(size[0]), Integer.parseInt(size[1])));
			}else {
				String itemId = childNode.getString();
				
				Optional<ItemType> optionalItemType = Main.getGame().getRegistry().getType(ItemType.class, itemId);
				
				if(optionalItemType.isPresent()) {
					String[] grid = key.split("x");
					
					builder.ingredient(new Vector2i(Integer.parseInt(grid[0]), Integer.parseInt(grid[1])), ItemStack.builder().itemType(optionalItemType.get()).build());
				}else {
					throw new InvalidItemTypeException("ItemType in configuration is invalid");
				}
			}
		}

		return builder.build();
	}
}
