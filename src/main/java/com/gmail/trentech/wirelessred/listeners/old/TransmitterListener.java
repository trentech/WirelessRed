package com.gmail.trentech.wirelessred.listeners.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.ImmutableTransmitterData;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.TransmitterHelper;

public class TransmitterListener {

	private static ConcurrentHashMap<String, TransmitterData> held = new ConcurrentHashMap<>();
	
	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event){
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()){
			BlockSnapshot snapshot = transaction.getOriginal();
			
			Optional<ImmutableTransmitterData> optionalTransmitterData = snapshot.get(ImmutableTransmitterData.class);
			
			if(!optionalTransmitterData.isPresent()){
				continue;
			}

			Optional<Player> optionalPlayer = event.getCause().first(Player.class);
			
			if(optionalPlayer.isPresent()){
				Player player = optionalPlayer.get();
				
				if(player.gameMode().get().equals(GameModes.CREATIVE)){
					return;
				}
				
				Optional<ItemStack> optionalItemStack = optionalPlayer.get().getItemInHand();
				
				if(!optionalItemStack.isPresent()){
					transaction.setValid(false);
					return;
				}		

				Optional<Text> optionalDisplayName = optionalItemStack.get().get(Keys.DISPLAY_NAME);
				
				if(!optionalDisplayName.isPresent()){
					transaction.setValid(false);
					return;
				}
				
				if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
					transaction.setValid(false);
					return;
				}
				return;
			}
			transaction.setValid(false);
		}
	}
	
	@Listener
	public void onChangeSignEvent(ChangeSignEvent event, @First Player player){
		if(!held.containsKey(player.getUniqueId().toString())){
			return;
		}
		TransmitterData transmitterData = held.get(player.getUniqueId().toString());
		
		for(Location<World> location : new ArrayList<>(transmitterData.transmitter().get().getReceivers())){
			Optional<Receiver> optionalReceiver = Receiver.get(location);
			
			if(!optionalReceiver.isPresent()){
				transmitterData.transmitter().get().removeReceiver(location);
				continue;
			}
			Receiver receiver = optionalReceiver.get();
			
			receiver.setTransmitter(event.getTargetTile().getLocation());		
			receiver.updateTransmitter(location);
		}
		
		Location<World> location = event.getTargetTile().getLocation();

		event.getTargetTile().offer(transmitterData);
		
		List<Text> lines = new ArrayList<>();

		if(TransmitterHelper.toggleTransmitter(location)){
			lines.add(Text.of(TextColors.DARK_BLUE, "[Transmitter]"));
			lines.add(Text.of(TextColors.GREEN, "====="));
			lines.add(Text.of(TextColors.GREEN, "==="));
			lines.add(Text.of(TextColors.GREEN, "="));
		}else{
			lines.add(Text.of(TextColors.DARK_BLUE, "[Transmitter]"));
			lines.add(Text.EMPTY);
			lines.add(Text.EMPTY);
			lines.add(Text.of(TextColors.RED, "="));
		}

		event.getText().set(Keys.SIGN_LINES, lines);

		if(!player.getItemInHand().isPresent() || !player.getItemInHand().get().get(TransmitterData.class).isPresent()){
			held.remove(player.getUniqueId().toString());
		}
	}

	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Held event, @First Player player){
		Optional<ImmutableTransmitterData> optionalTransmitter = event.getTransactions().get(1).getFinal().get(ImmutableTransmitterData.class);
		
		if(optionalTransmitter.isPresent()){
			held.put(player.getUniqueId().toString(), optionalTransmitter.get().asMutable());
		}else{
			held.remove(player.getUniqueId().toString());
		}
	}
	
	@Listener
	public void onNotifyNeighborBlockEvent(NotifyNeighborBlockEvent event, @First BlockSnapshot snapshot){
		if(snapshot.get(Keys.POWER).isPresent()){
			for(Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()){
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());
				TransmitterHelper.toggleTransmitter(location, (snapshot.get(Keys.POWER).get() >= 1));
			}
		}else if(snapshot.get(Keys.POWERED).isPresent()){
			for(Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()){
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());
				TransmitterHelper.toggleTransmitter(location, snapshot.get(Keys.POWERED).get());
			}
		}else{
			for(Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()){
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());
				TransmitterHelper.toggleTransmitter(location, false);
			}
		}
	}
}
