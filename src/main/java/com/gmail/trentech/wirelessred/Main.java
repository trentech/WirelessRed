package com.gmail.trentech.wirelessred;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
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
import com.gmail.trentech.wirelessred.listeners.ReceiverListener;
import com.gmail.trentech.wirelessred.listeners.ToolListener;
import com.gmail.trentech.wirelessred.listeners.TransmitterListener;
import com.gmail.trentech.wirelessred.utils.ConfigManager;
import com.gmail.trentech.wirelessred.utils.RecipeHelper;
import com.gmail.trentech.wirelessred.utils.Resource;
import com.gmail.trentech.wirelessred.utils.SQLUtils;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	private static Logger log;
	private static PluginContainer plugin;

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		new ConfigManager().init();

		Sponge.getEventManager().registerListeners(this, new TransmitterListener());
		Sponge.getEventManager().registerListeners(this, new ReceiverListener());
		Sponge.getEventManager().registerListeners(this, new ToolListener());

		Sponge.getCommandManager().register(this, new CommandManager().cmdWR, "wr");

		Sponge.getDataManager().register(TransmitterData.class, ImmutableTransmitterData.class, new TransmitterDataManipulatorBuilder());
		Sponge.getDataManager().registerBuilder(Transmitter.class, new TransmitterBuilder());
		Sponge.getDataManager().register(ReceiverData.class, ImmutableReceiverData.class, new ReceiverDataManipulatorBuilder());
		Sponge.getDataManager().registerBuilder(Receiver.class, new ReceiverBuilder());

		try{
			RecipeHelper.init();
		}catch(Exception e) {
			getLog().warn("Recipe registration failed. This could be an implementation error.");
		}

		SQLUtils.createTables();
	}
	
	public static Logger getLog() {
		return log;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
}