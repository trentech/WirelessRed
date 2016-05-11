package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
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

	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event){
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()){
			BlockSnapshot snapshot = transaction.getOriginal();
			
			Optional<Location<World>> optionalLocation = snapshot.getLocation();
			
			if(!optionalLocation.isPresent()){
				return;
			}
			Location<World> location = optionalLocation.get();
			
			Optional<ImmutableTransmitterData> optionalTransmitterData = snapshot.get(ImmutableTransmitterData.class);
			
			if(!optionalTransmitterData.isPresent()){
				continue;
			}
			TransmitterData transmitterData = optionalTransmitterData.get().asMutable();

			TransmitterHelper.toggleTransmitter(location, false);
			
			ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).itemData(transmitterData).build();
			itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Circuit"));

			Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

		    if (itemEntity.isPresent()) {
		        Item item = (Item) itemEntity.get();
		        item.offer( Keys.REPRESENTED_ITEM, itemStack.createSnapshot() );
		        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
		    }
		}
	}
	
	@Listener
	public void onInteractTransmitterEvent(InteractBlockEvent.Secondary event, @First Player player){
		BlockSnapshot snapshot = event.getTargetBlock();
		
		Optional<Location<World>> optionalLocation = snapshot.getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<TransmitterData> optionalTransmitterData = optionalItemStack.get().get(TransmitterData.class);
		
		if(!optionalTransmitterData.isPresent()){
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();

		if(!snapshot.getState().getType().equals(BlockTypes.STANDING_SIGN) && !snapshot.getState().getType().equals(BlockTypes.WALL_SIGN)){
			return;
		}

		if(location.get(TransmitterData.class).isPresent()){
			player.sendMessage(Text.of(TextColors.RED, "Circuit already inserted"));
			return;
		}
		
		for(Location<World> receiverLocation : new ArrayList<>(transmitterData.transmitter().get().getReceivers())){
			Optional<Receiver> optionalReceiver = Receiver.get(receiverLocation);
			
			if(!optionalReceiver.isPresent()){
				transmitterData.transmitter().get().removeReceiver(receiverLocation);
				continue;
			}
			Receiver receiver = optionalReceiver.get();
			
			receiver.setTransmitter(snapshot.getLocation().get());		
			receiver.updateTransmitter(receiverLocation);
		}
		
		location.offer(transmitterData);

		TransmitterHelper.toggleTransmitter(location);
		
		player.getInventory().query(itemStack).poll(1);
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
