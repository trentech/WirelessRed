package com.gmail.trentech.wirelessred.listeners.old;

import java.util.Optional;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;

public class ToolListener {

	@Listener
	public void onInteractTransmitterEventPrimary(InteractBlockEvent.Primary event, @First Player player){
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		

		Optional<Text> optionalDisplayName = optionalItemStack.get().get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}

		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
			return;
		}

		if(player.gameMode().get().equals(GameModes.CREATIVE)){
			player.sendMessage(Text.of(TextColors.RED, "Cannot complete this action in Creative"));
			return;
		}
		
		Location<World> location = event.getTargetBlock().getLocation().get();
		
		Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);
		
		if(!optionalTransmitterData.isPresent()){
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();

		for(Location<World> receiverLocation : transmitterData.transmitter().get().getReceivers()){
			Optional<Receiver> optionalReceiver = Receiver.get(receiverLocation);
			
			if(!optionalReceiver.isPresent()){
				continue;
			}
			Receiver receiver = optionalReceiver.get();
			
			receiverLocation.offer(Keys.POWERED, false);
			
			receiver.setEnabled(false);
			receiver.updateEnabled(receiverLocation);
		}

		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.SIGN).itemData(transmitterData).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter"));

		location.setBlock(BlockTypes.AIR.getDefaultState());

		Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

	    if (itemEntity.isPresent()) {
	        Item item = (Item) itemEntity.get();
	        item.offer( Keys.REPRESENTED_ITEM, itemStack.createSnapshot() );
	        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
	    }
	}
	
	@Listener
	public void onInteractRecieverEventPrimary(InteractBlockEvent.Primary event, @First Player player){
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		

		Optional<Text> optionalDisplayName = optionalItemStack.get().get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}
		
		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
			return;
		}
		
		if(player.gameMode().get().equals(GameModes.CREATIVE)){
			player.sendMessage(Text.of(TextColors.RED, "Cannot complete this action in Creative"));
			return;
		}
		
		Location<World> location = event.getTargetBlock().getLocation().get();
		
		Optional<Receiver> optionalReceiver = Receiver.get(location);
		
		if(!optionalReceiver.isPresent()){
			return;
		}
		Receiver receiver = optionalReceiver.get();
		receiver.setEnabled(false);

		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.STONE_BUTTON).itemData(new ReceiverData(receiver)).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Receiver"));

		location.setBlock(BlockTypes.AIR.getDefaultState());
		
		Receiver.remove(location);
		
		Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

	    if (itemEntity.isPresent()) {
	        Item item = (Item) itemEntity.get();
	        item.offer( Keys.REPRESENTED_ITEM, itemStack.createSnapshot() );
	        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
	    }
	}
}
