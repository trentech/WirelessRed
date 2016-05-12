package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.List;
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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.ImmutableTransmitterData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.ItemHelper;
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

			ItemStack itemStack = ItemHelper.getTransmitter(transmitterData);
			
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
		Transmitter transmitter = transmitterData.transmitter().get();
		
		if(!snapshot.getState().getType().equals(BlockTypes.STANDING_SIGN) && !snapshot.getState().getType().equals(BlockTypes.WALL_SIGN)){
			return;
		}

		if(location.get(TransmitterData.class).isPresent()){
			player.sendMessage(Text.of(TextColors.RED, "Circuit already inserted"));
			return;
		}
		
		for(Location<World> receiverLocation : new ArrayList<>(transmitter.getReceivers())){
			Optional<Receiver> optionalReceiver = Receiver.get(receiverLocation);
			
			if(!optionalReceiver.isPresent()){
				transmitter.removeReceiver(receiverLocation);
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
	public void onInteractUpgradeEvent(InteractBlockEvent.Secondary event, @First Player player){
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);
		
		if(!optionalTransmitterData.isPresent()){
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}
		
		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Transmitter Upgrade")){
			return;
		}
		
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
		
		String upgrade = lore.get(0).toPlain().replace("Range: ", "");
		
		Transmitter transmitter = transmitterData.transmitter().get();
		
		if((upgrade.equalsIgnoreCase("Unlimited") && transmitter.isMultiWorld()) || (!upgrade.equalsIgnoreCase("Unlimited") && Double.parseDouble(upgrade) <= transmitter.getRange())){
			player.sendMessage(Text.of(TextColors.RED, "Transmitter already contains current upgrade"));
			return;
		}
		
		if(upgrade.equalsIgnoreCase("Unlimited")){
			transmitter.setMultiWorld(true);
			transmitter.setRange(60000000);
		}else{
			transmitter.setRange(Double.parseDouble(upgrade));
		}

		TransmitterHelper.toggleTransmitter(location, transmitter.isEnabled());
		
		player.getInventory().query(itemStack).poll(1);
		
		player.sendMessage(Text.of(TextColors.GREEN, "Transmitter upgraded"));
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
