package com.gmail.trentech.wirelessred;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.wirelessred.commands.CommandManager;
import com.gmail.trentech.wirelessred.data.receiver.ImmutableReceiverData;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverBuilder;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverDataManipulatorBuilder;
import com.gmail.trentech.wirelessred.data.transmitter.ImmutableTransmitterData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterBuilder;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterDataManipulatorBuilder;
import com.gmail.trentech.wirelessred.init.Recipes;
import com.gmail.trentech.wirelessred.listeners.ReceiverListener;
import com.gmail.trentech.wirelessred.listeners.TransmitterListener;
import com.gmail.trentech.wirelessred.utils.ConfigManager;
import com.gmail.trentech.wirelessred.utils.Resource;
import com.gmail.trentech.wirelessred.utils.SQLUtils;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
    private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;
	
	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {			
			Files.createDirectories(path);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		ConfigManager.init();

		Sponge.getEventManager().registerListeners(this, new TransmitterListener());
		Sponge.getEventManager().registerListeners(this, new ReceiverListener());

		Sponge.getCommandManager().register(this, new CommandManager().cmdWR, "wr");

		Sponge.getDataManager().register(TransmitterData.class, ImmutableTransmitterData.class, new TransmitterDataManipulatorBuilder());
		Sponge.getDataManager().registerBuilder(Transmitter.class, new TransmitterBuilder());
		Sponge.getDataManager().register(ReceiverData.class, ImmutableReceiverData.class, new ReceiverDataManipulatorBuilder());
		Sponge.getDataManager().registerBuilder(Receiver.class, new ReceiverBuilder());

		try{
			Recipes.init();
		}catch(Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}
		
		SQLUtils.createTables();
	}
	
	@Listener
	public void onReloadEvent(GameReloadEvent event) {
		Sponge.getEventManager().unregisterPluginListeners(getPlugin());
		
		try{
			Recipes.remove();
		}catch(Exception e) {
			getLog().warn("Recipe removal failed. This could be an implementation error.");
		}

		ConfigManager.init();
		
		Sponge.getEventManager().registerListeners(this, new TransmitterListener());
		Sponge.getEventManager().registerListeners(this, new ReceiverListener());
		
		try{
			Recipes.init();
		}catch(Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}
	}
	
	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}
	
	public static PluginContainer getPlugin() {
		return plugin;
	}
	
	public static Main instance() {
		return instance;
	}
}