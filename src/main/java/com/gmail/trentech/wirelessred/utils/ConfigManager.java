package com.gmail.trentech.wirelessred.utils;

import java.io.File;
import java.io.IOException;

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

	private void create(){
		if(!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();		
			} catch (IOException e) {				
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}
	
	public void init(){
		if(config.getNode("starting_range").isVirtual()){
			config.getNode("starting_range").setValue("32");
			save();
		}
	}
	
	private void load(){
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
	
	public void save(){
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
}
