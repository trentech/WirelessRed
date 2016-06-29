package com.gmail.trentech.wirelessred;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.gmail.trentech.wirelessred.commands.CMDReceiver;
import com.gmail.trentech.wirelessred.commands.CMDTool;
import com.gmail.trentech.wirelessred.commands.CMDTransmitter;
import com.gmail.trentech.wirelessred.commands.CMDUpgrade;
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
import com.gmail.trentech.wirelessred.utils.Resource;
import com.gmail.trentech.wirelessred.utils.SQLUtils;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = "WirelessRed", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	private static Game game;
	private static Logger log;
	private static PluginContainer plugin;

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		new ConfigManager().init();

		getGame().getEventManager().registerListeners(this, new TransmitterListener());
		getGame().getEventManager().registerListeners(this, new ReceiverListener());
		getGame().getEventManager().registerListeners(this, new ToolListener());

		getGame().getCommandManager().register(this, new CMDTransmitter().cmdTransmitter, "transmitter");
		getGame().getCommandManager().register(this, new CMDReceiver().cmdReceiver, "receiver");
		getGame().getCommandManager().register(this, new CMDTool().cmdTool, "tool");
		getGame().getCommandManager().register(this, new CMDUpgrade().cmdUpgrade, "upgrade");

		getGame().getDataManager().register(TransmitterData.class, ImmutableTransmitterData.class, new TransmitterDataManipulatorBuilder());
		getGame().getDataManager().registerBuilder(Transmitter.class, new TransmitterBuilder());
		getGame().getDataManager().register(ReceiverData.class, ImmutableReceiverData.class, new ReceiverDataManipulatorBuilder());
		getGame().getDataManager().registerBuilder(Receiver.class, new ReceiverBuilder());

//		getGame().getRegistry().getRecipeRegistry().register(RecipeHelper.getTransmitter());
//		getGame().getRegistry().getRecipeRegistry().register(RecipeHelper.getReceiver());
//		getGame().getRegistry().getRecipeRegistry().register(RecipeHelper.getTool());

		SQLUtils.createTables();
	}

	public static Logger getLog() {
		return log;
	}

	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}
}