package com.gmail.trentech.wirelessred.listeners.old;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.ImmutableReceiverData;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;

public class ReceiverListener {

	private static ConcurrentHashMap<String, ReceiverData> held = new ConcurrentHashMap<>();
	
	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event){
		for(Transaction<BlockSnapshot> transaction : new ArrayList<>(event.getTransactions())){
			BlockSnapshot snapshot = transaction.getOriginal();

			Location<World> location = snapshot.getLocation().get();
			
			Optional<Receiver> optionalReceiver = Receiver.get(location);
			
			if(!optionalReceiver.isPresent()){
				continue;
			}
			
			Optional<Player> optionalPlayer = event.getCause().first(Player.class);
			
			if(optionalPlayer.isPresent()){
				Player player = optionalPlayer.get();
				
				if(player.gameMode().get().equals(GameModes.CREATIVE)){
					Receiver.remove(location);
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
	public void onChangeBlockEventPlace(ChangeBlockEvent.Place event, @First Player player){
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()){
			if(!transaction.getFinal().getState().getType().equals(BlockTypes.STONE_BUTTON)){
				continue;
			}
			
			if(!held.containsKey(player.getUniqueId().toString())){
				continue;
			}			
			ReceiverData receiverData = held.get(player.getUniqueId().toString());
			
			Optional<Location<World>> optionalTransmitter = receiverData.receiver().get().getTransmitter();
			
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
			
			Location<World> location = transaction.getFinal().getLocation().get();
			
			transmitterData.transmitter().get().addReceiver(location);
			transmitter.offer(transmitterData);
			
			if(transmitterData.transmitter().get().isEnabled()){
				receiverData.receiver().get().setEnabled(true);
				location.offer(Keys.POWERED, true);
			}
			
			receiverData.receiver().get().save(location);
			
			if(!player.getItemInHand().isPresent() || !player.getItemInHand().get().get(ReceiverData.class).isPresent()){
				held.remove(player.getUniqueId().toString());
			}
		}
	}

	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Held event, @First Player player){
		Optional<ImmutableReceiverData> optionalReceiver = event.getTransactions().get(1).getFinal().get(ImmutableReceiverData.class);
		if(optionalReceiver.isPresent()){
			held.put(player.getUniqueId().toString(), optionalReceiver.get().asMutable());
		}else{
			held.remove(player.getUniqueId().toString());
		}
	}
	
	@Listener
	public void onChangeInventoryEvent(ChangeInventoryEvent.Pickup event, @First Player player){
		System.out.println("HELD");
	}

	@Listener
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event, @First Player player){
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

		receiverData.receiver().get().setTransmitter(location);
		itemStack.offer(receiverData);
		
		held.put(player.getUniqueId().toString(), receiverData);
		
		player.sendMessage(Text.of(TextColors.GREEN, "Linked"));
	}
}
