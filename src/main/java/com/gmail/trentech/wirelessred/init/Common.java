package com.gmail.trentech.wirelessred.init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.RecipeManager;
import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.wirelessred.Main;

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
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
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
			config.getNode("recipes", "tool", "enable").setValue(true);
			config.getNode("recipes", "tool", "id").setValue("tool");
			config.getNode("recipes", "tool", "row1").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.NONE.getId() + "," + ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "row2").setValue(ItemTypes.NONE.getId() + "," + ItemTypes.STICK.getId() + "," + ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "row3").setValue(ItemTypes.NONE.getId() + "," + ItemTypes.NONE.getId() + "," + ItemTypes.NONE.getId());
		}
		if (config.getNode("recipes", "receiver").isVirtual()) {
			config.getNode("recipes", "receiver", "enable").setValue(true);
			config.getNode("recipes", "receiver", "id").setValue("receiver");
			config.getNode("recipes", "receiver", "row1").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "row2").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.REPEATER.getId() + "," + ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "row3").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId());
		}
		if (config.getNode("recipes", "transmitter").isVirtual()) {
			config.getNode("recipes", "transmitter", "enable").setValue(true);
			config.getNode("recipes", "transmitter", "id").setValue("transmitter");
			config.getNode("recipes", "transmitter", "row1").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "row2").setValue(ItemTypes.REDSTONE.getId() + "," + ItemTypes.COMPARATOR.getId() + "," + ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "row3").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.GOLD_INGOT.getId());
		}
		if (config.getNode("recipes", "upgrade_64").isVirtual()) {
			config.getNode("recipes", "upgrade_64", "enable").setValue(true);
			config.getNode("recipes", "upgrade_64", "id").setValue("upgrade_64");
			config.getNode("recipes", "upgrade_64", "row1").setValue(ItemTypes.STONE.getId() + "," + ItemTypes.STONE.getId() + "," + ItemTypes.STONE.getId());
			config.getNode("recipes", "upgrade_64", "row2").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_64", "row3").setValue(ItemTypes.STONE.getId() + "," + ItemTypes.STONE.getId() + "," + ItemTypes.STONE.getId());
		}
		if (config.getNode("recipes", "upgrade_128").isVirtual()) {
			config.getNode("recipes", "upgrade_128", "enable").setValue(true);
			config.getNode("recipes", "upgrade_128", "id").setValue("upgrade_128");
			config.getNode("recipes", "upgrade_128", "row1").setValue(ItemTypes.IRON_INGOT.getId() + "," + ItemTypes.IRON_INGOT.getId() + "," + ItemTypes.IRON_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "row2").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_128", "row3").setValue(ItemTypes.IRON_INGOT.getId() + "," + ItemTypes.IRON_INGOT.getId() + "," + ItemTypes.IRON_INGOT.getId());
		}
		if (config.getNode("recipes", "upgrade_256").isVirtual()) {
			config.getNode("recipes", "upgrade_256", "enable").setValue(true);
			config.getNode("recipes", "upgrade_256", "id").setValue("upgrade_256");
			config.getNode("recipes", "upgrade_256", "row1").setValue(ItemTypes.IRON_BLOCK.getId() + "," + ItemTypes.IRON_BLOCK.getId() + "," + ItemTypes.IRON_BLOCK.getId());
			config.getNode("recipes", "upgrade_256", "row2").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_256", "row3").setValue(ItemTypes.IRON_BLOCK.getId() + "," + ItemTypes.IRON_BLOCK.getId() + "," + ItemTypes.IRON_BLOCK.getId());
		}
		if (config.getNode("recipes", "upgrade_512").isVirtual()) {
			config.getNode("recipes", "upgrade_512", "enable").setValue(true);
			config.getNode("recipes", "upgrade_512", "id").setValue("upgrade_512");
			config.getNode("recipes", "upgrade_512", "row1").setValue(ItemTypes.DIAMOND.getId() + "," + ItemTypes.DIAMOND.getId() + "," + ItemTypes.DIAMOND.getId());
			config.getNode("recipes", "upgrade_512", "row2").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_512", "row3").setValue(ItemTypes.DIAMOND.getId() + "," + ItemTypes.DIAMOND.getId() + "," + ItemTypes.DIAMOND.getId());
		}
		if (config.getNode("recipes", "upgrade_unlimited").isVirtual()) {
			config.getNode("recipes", "upgrade_unlimited", "enable").setValue(true);
			config.getNode("recipes", "upgrade_unlimited", "id").setValue("upgrade_unlimited");
			config.getNode("recipes", "upgrade_unlimited", "row1").setValue(ItemTypes.DIAMOND_BLOCK.getId() + "," + ItemTypes.DIAMOND_BLOCK.getId() + "," + ItemTypes.DIAMOND_BLOCK.getId());
			config.getNode("recipes", "upgrade_unlimited", "row2").setValue(ItemTypes.GOLD_INGOT.getId() + "," + ItemTypes.REDSTONE_TORCH.getId() + "," + ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "upgrade_unlimited", "row3").setValue(ItemTypes.DIAMOND_BLOCK.getId() + "," + ItemTypes.DIAMOND_BLOCK.getId() + "," + ItemTypes.DIAMOND_BLOCK.getId());
		}
		if (config.getNode("settings", "sql", "database").isVirtual()) {
			config.getNode("settings", "sql", "database").setValue(Main.getPlugin().getId());
		}
		configManager.save();
	}
	
	public static void initRecipeManager() {
		ConfigurationNode recipes = ConfigManager.get(Main.getPlugin()).getConfig().getNode("recipes");

		if (recipes.getNode("transmitter", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("transmitter"), Items.getEmptyTransmitter());
		}
		if (recipes.getNode("receiver", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("receiver"), Items.getEmptyReceiver());
		}
		if (recipes.getNode("tool", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("tool"), Items.getTool(true));
		}
		if (recipes.getNode("upgrade_64", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("upgrade_64"), Items.getUpgrade("64"));
		}
		if (recipes.getNode("upgrade_128", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("upgrade_128"), Items.getUpgrade("128"));
		}
		if (recipes.getNode("upgrade_256", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("upgrade_256"), Items.getUpgrade("256"));
		}
		if (recipes.getNode("upgrade_512", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("upgrade_512"), Items.getUpgrade("512"));
		}
		if (recipes.getNode("upgrade_unlimited", "enable").getBoolean()) {
			RecipeManager.register(recipes.getNode("upgrade_unlimited"), Items.getUpgrade("Unlimited"));
		}
	}
}
