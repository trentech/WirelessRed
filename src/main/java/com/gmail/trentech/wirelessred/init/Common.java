package com.gmail.trentech.wirelessred.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.RecipeManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.pjc.utils.InvalidItemTypeException;
import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class Common {

	public static void init() {
		initConfig();
		initHelp();
		initData();
	}
	
	public static void initData() {
		try {
			String database = ConfigManager.get(Main.getPlugin()).getConfig().getNode("settings", "sql", "database").getString();

			SQLManager sqlManager = SQLManager.get(Main.getPlugin(), database);
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sqlManager.getPrefix("RECEIVERS") + " (Location TEXT, Enabled BOOL, Transmitter TEXT, Destination TEXT)");
			statement.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void initHelp() {

	}
	
	public static void initConfig() {
		ConfigManager configManager = ConfigManager.init(Main.getPlugin());
		CommentedConfigurationNode config = configManager.getConfig();

		if (config.getNode("settings", "starting_range").isVirtual()) {
			config.getNode("settings", "starting_range").setValue("32");			
		}
		if (config.getNode("settings", "economy").isVirtual()) {
			config.getNode("settings", "economy", "items", "tool").setValue(50.0);
			config.getNode("settings", "economy", "items", "transmitter").setValue(50.0);
			config.getNode("settings", "economy", "items", "receiver").setValue(50.0);
			config.getNode("settings", "economy", "items", "upgrade_64").setValue(50.0);
			config.getNode("settings", "economy", "items", "upgrade_128").setValue(100.0);
			config.getNode("settings", "economy", "items", "upgrade_256").setValue(150.0);
			config.getNode("settings", "economy", "items", "upgrade_512").setValue(175.0);
			config.getNode("settings", "economy", "items", "upgrade_unlimited").setValue(200.0);
			config.getNode("settings", "economy", "enable").setValue(true);
		}
		if (config.getNode("recipes", "tool").isVirtual()) {
			config.getNode("recipes", "tool", "grid_size").setValue("2x2");
			config.getNode("recipes", "tool", "1x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "tool", "1x2").setValue(ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "2x1").setValue(ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "2x2").setValue(ItemTypes.STICK.getId());
		}
		if (config.getNode("recipes", "receiver").isVirtual()) {
			config.getNode("recipes", "receiver", "grid_size").setValue("3x3");
			config.getNode("recipes", "receiver", "1x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "1x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "receiver", "1x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "2x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "2x2").setValue(ItemTypes.REPEATER.getId());
			config.getNode("recipes", "receiver", "2x3").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "3x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "3x2").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "3x3").setValue(ItemTypes.GOLD_INGOT.getId());
		}
		if (config.getNode("recipes", "transmitter").isVirtual()) {
			config.getNode("recipes", "transmitter", "grid_size").setValue("3x3");
			config.getNode("recipes", "transmitter", "1x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "1x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "transmitter", "1x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "2x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "2x2").setValue(ItemTypes.COMPARATOR.getId());
			config.getNode("recipes", "transmitter", "2x3").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "3x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "3x2").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "3x3").setValue(ItemTypes.GOLD_INGOT.getId());
		}
		if (config.getNode("recipes", "upgrade_64").isVirtual()) {
			config.getNode("recipes", "upgrade_64", "grid_size").setValue("3x3");
			config.getNode("recipes", "upgrade_64", "1x1").setValue(ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "1x2").setValue(ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "1x3").setValue(ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_64", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "upgrade_64", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_64", "3x1").setValue(ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "3x2").setValue(ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "3x3").setValue(ItemTypes.STONE.getId());
		}
		if (config.getNode("recipes", "upgrade_128").isVirtual()) {
			config.getNode("recipes", "upgrade_128", "grid_size").setValue("3x3");
			config.getNode("recipes", "upgrade_128", "1x1").setValue(ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "1x2").setValue(ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "1x3").setValue(ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "upgrade_128", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "3x1").setValue(ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "3x2").setValue(ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "3x3").setValue(ItemTypes.IRON_INGOT.getId());
		}
		if (config.getNode("recipes", "upgrade_256").isVirtual()) {
			config.getNode("recipes", "upgrade_256", "grid_size").setValue("3x3");
			config.getNode("recipes", "upgrade_256", "1x1").setValue(ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "1x2").setValue(ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "1x3").setValue(ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_256", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "upgrade_256", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_256", "3x1").setValue(ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "3x2").setValue(ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "3x3").setValue(ItemTypes.IRON_BLOCK.getId());
		}
		if (config.getNode("recipes", "upgrade_512").isVirtual()) {
			config.getNode("recipes", "upgrade_512", "grid_size").setValue("3x3");
			config.getNode("recipes", "upgrade_512", "1x1").setValue(ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "1x2").setValue(ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "1x3").setValue(ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_512", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "upgrade_512", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_512", "3x1").setValue(ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "3x2").setValue(ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "3x3").setValue(ItemTypes.DIAMOND.getId());
		}
		if (config.getNode("recipes", "upgrade_unlimited").isVirtual()) {
			config.getNode("recipes", "upgrade_unlimited", "grid_size").setValue("3x3");
			config.getNode("recipes", "upgrade_unlimited", "1x1").setValue(ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "1x2").setValue(ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "1x3").setValue(ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_unlimited", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "upgrade_unlimited", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_unlimited", "3x1").setValue(ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "3x2").setValue(ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "3x3").setValue(ItemTypes.DIAMOND_BLOCK.getId());
		}
		if (config.getNode("settings", "sql", "database").isVirtual()) {
			config.getNode("settings", "sql", "database").setValue(Main.getPlugin().getId());
		}
		configManager.save();
	}
	
	public static void initRecipeManager() {
		ConfigurationNode config = ConfigManager.get(Main.getPlugin()).getConfig().getNode("recipes");

		try {
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("transmitter"), Items.getTransmitter(new TransmitterData(), 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("receiver"), Items.getReceiver(new Receiver(), 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("tool"), Items.getTool(true)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("upgrade_64"), Items.getUpgrade("64", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("upgrade_128"), Items.getUpgrade("128", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("upgrade_256"), Items.getUpgrade("256", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("upgrade_512"), Items.getUpgrade("512", 1)));
			Sponge.getRegistry().getRecipeRegistry().register(RecipeManager.getShapedRecipe(config.getNode("upgrade_unlimited"), Items.getUpgrade("Unlimited", 1)));
		} catch (InvalidItemTypeException e) {
			e.printStackTrace();
		}
	}
}
