package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
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
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.ItemHelper;

public class ReceiverListener {

	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event){
		for(Transaction<BlockSnapshot> transaction : new ArrayList<>(event.getTransactions())){
			Optional<Location<World>> optionalLocation = transaction.getOriginal().getLocation();
			
			if(!optionalLocation.isPresent()){
				return;
			}
			Location<World> location = optionalLocation.get();
			
			Optional<Receiver> optionalReceiver = Receiver.get(location);
			
			if(!optionalReceiver.isPresent()){
				continue;
			}
			Receiver receiver = optionalReceiver.get();
			
			ItemStack itemStack = ItemHelper.getReceiver(receiver);
			
			Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

		    if (itemEntity.isPresent()) {
		        Item item = (Item) itemEntity.get();
		        item.offer( Keys.REPRESENTED_ITEM, itemStack.createSnapshot() );
		        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
		    }
		    
		    Receiver.remove(location);
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
		
		Optional<ReceiverData> optionalReceiverData = optionalItemStack.get().get(ReceiverData.class);
		
		if(!optionalReceiverData.isPresent()){
			return;
		}
		Receiver receiver = optionalReceiverData.get().receiver().get();

		if(!snapshot.getState().getType().equals(BlockTypes.STONE_BUTTON)){
			return;
		}

		if(Receiver.get(location).isPresent()){
			player.sendMessage(Text.of(TextColors.RED, "Circuit already inserted"));
			return;
		}

		Optional<Location<World>> optionalTransmitter = receiver.getTransmitter();
		
		if(!optionalTransmitter.isPresent()){
			event.setCancelled(true);
			player.sendMessage(Text.of(TextColors.RED, "Not linked to a transmitter"));
			return;
		}
		Location<World> transmitter = optionalTransmitter.get();

		Optional<TransmitterData> optionalTransmitterData = transmitter.get(TransmitterData.class);
		
		if(!optionalTransmitterData.isPresent()){
			event.setCancelled(true);
			player.sendMessage(Text.of(TextColors.RED, "Linked transmitter no longer exists"));
			return;
		}		
		TransmitterData transmitterData = optionalTransmitterData.get();

		transmitterData.transmitter().get().addReceiver(location);
		transmitter.offer(transmitterData);
		
		if(transmitterData.transmitter().get().isEnabled()){
			if(transmitter.getPosition().distance(location.getPosition()) <= transmitterData.transmitter().get().getRange()){
				receiver.setEnabled(true);
				location.offer(Keys.POWERED, true);
			}
		}
		
		receiver.save(location);
		
		player.getInventory().query(itemStack).poll(1);
	}

	@Listener
	public void onInteractLinkEvent(InteractBlockEvent.Secondary event, @First Player player){
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		if(!location.get(TransmitterData.class).isPresent()){
			return;
		}
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<ReceiverData> optionalReceiverData = itemStack.get(ReceiverData.class);
		
		if(!optionalReceiverData.isPresent()){
			return;
		}
		ReceiverData receiverData = optionalReceiverData.get();
		Receiver receiver = receiverData.receiver().get();
		
		receiver.setTransmitter(location);

		player.getInventory().query(itemStack).set(ItemHelper.getReceiver(receiver));

		player.sendMessage(Text.of(TextColors.GREEN, "Linked"));
	}

	@Listener
	public void onInteractReceiverEvent(InteractBlockEvent.Secondary event, @First Player player){
		BlockSnapshot snapshot = event.getTargetBlock();

		Optional<Location<World>> optionalLocation = snapshot.getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		if(!snapshot.getState().getType().equals(BlockTypes.STONE_BUTTON)){
			return;
		}

		if(!Receiver.get(location).isPresent()){
			return;
		}
		
		event.setUseBlockResult(Tristate.FALSE);
	}
}
