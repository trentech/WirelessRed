package com.gmail.trentech.wirelessred.init;

import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.ShapedRecipe;
import org.spongepowered.api.item.recipe.ShapedRecipe.Builder;

import com.flowpowered.math.vector.Vector2i;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.ConfigManager;
import com.gmail.trentech.wirelessred.utils.InvalidItemTypeException;

import ninja.leaping.configurate.ConfigurationNode;

public class Recipes {

	public static void init() {
		ConfigurationNode config = ConfigManager.get().getConfig().getNode("recipes");
		
		try {
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("transmitter"), Items.getTransmitter(new TransmitterData(), 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("receiver"), Items.getReceiver(new Receiver(), 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("tool"), Items.getTool(true)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("upgrade_64"), Items.getUpgrade("64", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("upgrade_128"), Items.getUpgrade("128", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("upgrade_256"), Items.getUpgrade("256", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("upgrade_512"), Items.getUpgrade("512", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(getRecipe(config.getNode("upgrade_unlimited"), Items.getUpgrade("Unlimited", 1)));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}
	}

	public static void remove() {
		ConfigurationNode config = ConfigManager.get().getConfig().getNode("recipes");
		
		try{	
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("transmitter"), Items.getTransmitter(new TransmitterData(), 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("receiver"), Items.getReceiver(new Receiver(), 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("tool"), Items.getTool(true)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("upgrade_64"), Items.getUpgrade("64", 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("upgrade_128"), Items.getUpgrade("128", 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("upgrade_256"), Items.getUpgrade("256", 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("upgrade_512"), Items.getUpgrade("512", 1)));
			Sponge.getRegistry().getRecipeRegistry().remove(getRecipe(config.getNode("upgrade_unlimited"), Items.getUpgrade("Unlimited", 1)));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}
	}
	
	private static ShapedRecipe getRecipe(ConfigurationNode node, ItemStack result) throws InvalidItemTypeException {
		Builder builder = Sponge.getRegistry().createBuilder(ShapedRecipe.Builder.class);

		for(Entry<Object, ? extends ConfigurationNode> child : node.getChildrenMap().entrySet()) {
			ConfigurationNode childNode = child.getValue();
			
			String key = childNode.getKey().toString();
			
			if(key.equals("grid_size")) {
				String[] size = childNode.getString().split("x");
				
				builder.dimensions(new Vector2i(Integer.parseInt(size[0]), Integer.parseInt(size[1])));
			}else {
				String itemId = childNode.getString();
				
				Optional<ItemType> optionalItemType = Sponge.getRegistry().getType(ItemType.class, itemId);
				
				if(optionalItemType.isPresent()) {
					String[] grid = key.split("x");
					
					builder.ingredient(new Vector2i(Integer.parseInt(grid[0]), Integer.parseInt(grid[1])), ItemStack.builder().itemType(optionalItemType.get()).build());
				}else {
					throw new InvalidItemTypeException("ItemType in config.conf at " + childNode.getKey().toString() + " is invalid");
				}
			}
		}

		return builder.addResult(result).build();
	}
}
