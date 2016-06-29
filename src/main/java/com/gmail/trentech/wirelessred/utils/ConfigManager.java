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
		if (config.getNode("starting_range").isVirtual()) {
			config.getNode("starting_range").setValue("32");			
		}
		if (config.getNode("recipes", "tool").isVirtual()) {
			config.getNode("recipes", "tool", "grid_size").setValue("2x2");
			config.getNode("recipes", "tool", "1,1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "tool", "1,2").setValue(ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "2,1").setValue(ItemTypes.NONE.getId());
			config.getNode("recipes", "tool", "2,2").setValue(ItemTypes.STICK.getId());
		}
		if (config.getNode("recipes", "receiver").isVirtual()) {
			config.getNode("recipes", "receiver", "grid_size").setValue("3x3");
			config.getNode("recipes", "receiver", "1x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "1x2").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "1x3").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "2x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "receiver", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "receiver", "3x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "3x2").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "receiver", "3x3").setValue(ItemTypes.REDSTONE.getId());
		}
		if (config.getNode("recipes", "transmitter").isVirtual()) {
			config.getNode("recipes", "transmitter", "grid_size").setValue("3x3");
			config.getNode("recipes", "transmitter", "1x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "1x2").setValue(ItemTypes.REDSTONE_TORCH.getId());
			config.getNode("recipes", "transmitter", "1x3").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "2x1").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "2x2").setValue(ItemTypes.COMPARATOR.getId());
			config.getNode("recipes", "transmitter", "2x3").setValue(ItemTypes.GOLD_INGOT.getId());
			config.getNode("recipes", "transmitter", "3x1").setValue(ItemTypes.REDSTONE.getId());
			config.getNode("recipes", "transmitter", "3x2").setValue(ItemTypes.REPEATER.getId());
			config.getNode("recipes", "transmitter", "3x3").setValue(ItemTypes.REDSTONE.getId());
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
