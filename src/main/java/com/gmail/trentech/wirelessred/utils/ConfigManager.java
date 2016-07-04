package com.gmail.trentech.wirelessred.utils;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.item.ItemTypes;

import com.gmail.trentech.wirelessred.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(String folder, String configName) {
		folder = "config" + File.separator + "wirelessred" + File.separator + folder;
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, configName);

		create();
		load();
	}

	public ConfigManager(String configName) {
		String folder = "config" + File.separator + "wirelessred";
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, configName);

		create();
		load();
	}

	public ConfigManager() {
		String folder = "config" + File.separator + "wirelessred";
		if (!new File(folder).isDirectory()) {
			new File(folder).mkdirs();
		}
		file = new File(folder, "config.conf");

		create();
		load();
	}

	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	private void create() {
		if (!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();
			} catch (IOException e) {
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}

	public void init() {
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
		save();
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
}
