package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.TransmitterHelper;

public class ToolListener {

	@Listener
	public void onInteractTransmitterEventPrimary(InteractBlockEvent.Secondary event, @First Player player){
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<Text> optionalDisplayName = optionalItemStack.get().get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}

		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
			return;
		}

		Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);
		
		if(!optionalTransmitterData.isPresent()){
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();
		Transmitter transmitter = transmitterData.transmitter().get();
		
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
		
		if(lore.get(0).toPlain().equalsIgnoreCase("Mode: Tool")){
			TransmitterHelper.toggleTransmitter(location, false);

			ItemStack spawnItemStack = ItemStack.builder().itemType(ItemTypes.PAPER).itemData(transmitterData).build();
			spawnItemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Circuit"));

			lore = new ArrayList<>();
			
			if(transmitter.getRange() == 60000000){
				lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "Unlimited"));
				lore.add(1, Text.of(TextColors.GREEN, "Mutli-World: ", TextColors.YELLOW, true));
			}else{
				lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, transmitter.getRange()));
				lore.add(1, Text.of(TextColors.GREEN, "Mutli-World: ", TextColors.YELLOW, false));
			}
			
			spawnItemStack.offer(Keys.ITEM_LORE, lore);
			
			Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

		    if (itemEntity.isPresent()) {
		        Item item = (Item) itemEntity.get();
		        item.offer( Keys.REPRESENTED_ITEM, spawnItemStack.createSnapshot() );
		        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
		    }
		    
		    location.offer(Keys.SIGN_LINES, new ArrayList<>());
		    location.remove(TransmitterData.class);
		}else{
			int score = transmitter.getReceivers().size() + 5;
			
			Scoreboard scoreboard = Scoreboard.builder().build();
			
			Objective objective = Objective.builder().displayName(Text.of(TextColors.GREEN, "     Transmitter Info     ")).name("transmitterinfo").criterion(Criteria.DUMMY).build();

			objective.getOrCreateScore(Text.of(TextColors.GREEN, "Upgrades")).setScore(score--);
			
			if(transmitter.getRange() == 60000000){
				objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Range: ", TextColors.WHITE, "Unlimited")).setScore(score--);
			}else{
				objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Range: ", TextColors.WHITE, transmitter.getRange())).setScore(score--);
			}
			
			objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Multi-World: ", TextColors.WHITE, transmitter.isMultiWorld())).setScore(score--);
			objective.getOrCreateScore(Text.EMPTY).setScore(score--);
			objective.getOrCreateScore(Text.of(TextColors.GREEN, "Receivers")).setScore(score--);
			
			for(Location<World> receiver : transmitter.getReceivers()){
				if(TransmitterHelper.isInRange(transmitter, location, receiver)) {
					objective.getOrCreateScore(Text.of("- ", receiver.getExtent().getName(), " ", receiver.getBlockX(), " ", receiver.getBlockY(), " ", receiver.getBlockZ())).setScore(score--);
				}else{
					objective.getOrCreateScore(Text.of(TextColors.RED, "- ", receiver.getExtent().getName(), " ", receiver.getBlockX(), " ", receiver.getBlockY(), " ", receiver.getBlockZ())).setScore(score--);
				}		
			}
			
			scoreboard.addObjective(objective);
			scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

			player.setScoreboard(scoreboard);
			
			Main.getGame().getScheduler().createTaskBuilder().async().delayTicks(100).execute(runnable -> {
				player.setScoreboard(Scoreboard.builder().build());
			}).submit(Main.getPlugin());
		}
	}
	
	@Listener
	public void onInteractRecieverEventPrimary(InteractBlockEvent.Secondary event, @First Player player){
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
		
		if(!optionalLocation.isPresent()){
			return;
		}
		Location<World> location = optionalLocation.get();
		
		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<Text> optionalDisplayName = optionalItemStack.get().get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}
		
		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
			return;
		}
		
		Optional<Receiver> optionalReceiver = Receiver.get(location);
		
		if(!optionalReceiver.isPresent()){
			return;
		}
		Receiver receiver = optionalReceiver.get();
		
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
		
		if(lore.get(0).toPlain().equalsIgnoreCase("Mode: Tool")){
			receiver.setEnabled(false);

			ItemStack spawnItemStack = ItemStack.builder().itemType(ItemTypes.PAPER).itemData(new ReceiverData(receiver)).build();
			spawnItemStack.offer(Keys.DISPLAY_NAME, Text.of("Receiver Circuit"));

			lore = new ArrayList<>();
			
			Optional<Location<World>> optionalTransmitter = receiver.getTransmitter();
			
			if(optionalTransmitter.isPresent()){
				Location<World> transmitter = optionalTransmitter.get();
				
				if(transmitter.get(TransmitterData.class).isPresent()){
					lore.add(0, Text.of(TextColors.GREEN, "Transmitter: ", TextColors.YELLOW, transmitter.getExtent().getName(), " ", transmitter.getBlockX(), " ", transmitter.getBlockY(), " ", transmitter.getBlockZ()));
				}else{
					lore.add(0, Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Not found"));
				}
			}else{
				lore.add(0, Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Location error"));
			}
			
			spawnItemStack.offer(Keys.ITEM_LORE, lore);
			
			Receiver.remove(location);
			
			Optional<Entity> itemEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());

		    if (itemEntity.isPresent()) {
		        Item item = (Item) itemEntity.get();
		        item.offer( Keys.REPRESENTED_ITEM, spawnItemStack.createSnapshot() );
		        location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));
		    }
		    
		    location.offer(Keys.POWERED, false);
		}else{
			Scoreboard scoreboard = Scoreboard.builder().build();
			
			Objective objective = Objective.builder().displayName(Text.of(TextColors.GREEN, "    Receiver Info    ")).name("receiverinfo").criterion(Criteria.DUMMY).build();

			objective.getOrCreateScore(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, receiver.isEnabled())).setScore(3);
			
			Optional<Location<World>> optionalTransmitterLocation = receiver.getTransmitter();

			if(optionalTransmitterLocation.isPresent()){
				Location<World> transmitterLocation = optionalTransmitterLocation.get();

				Optional<TransmitterData> optionalTransmitterData = transmitterLocation.get(TransmitterData.class);
				
				if(optionalTransmitterData.isPresent()){
					objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.WHITE, transmitterLocation.getExtent().getName(), " ", transmitterLocation.getBlockX(), " ", transmitterLocation.getBlockY(), " ", transmitterLocation.getBlockZ())).setScore(2);
					
					Transmitter transmitter = optionalTransmitterData.get().transmitter().get();
					
					if(!TransmitterHelper.isInRange(transmitter, transmitterLocation, location)) {
						objective.getOrCreateScore(Text.of(TextColors.GREEN, "- In range")).setScore(1);
					}else{
						objective.getOrCreateScore(Text.of(TextColors.RED, "- Out of range")).setScore(1);
					}
				}else{
					objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Not found")).setScore(1);
				}
			}else{
				objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Location Error")).setScore(1);
			}

			scoreboard.addObjective(objective);
			scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

			player.setScoreboard(scoreboard);
			
			Main.getGame().getScheduler().createTaskBuilder().async().delayTicks(100).execute(runnable -> {
				player.setScoreboard(Scoreboard.builder().build());
			}).submit(Main.getPlugin());
		}
	}
	
	@Listener
	public void onInteractModeEventPrimary(InteractBlockEvent.Secondary event, @First Player player){
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
		
		if(optionalLocation.isPresent()){
			return;
		}

		Optional<ItemStack> optionalItemStack = player.getItemInHand();
		
		if(!optionalItemStack.isPresent()){
			return;
		}		
		ItemStack itemStack = optionalItemStack.get();
		
		Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);
		
		if(!optionalDisplayName.isPresent()){
			return;
		}
		
		if(!optionalDisplayName.get().toPlain().equalsIgnoreCase("Screw Driver")){
			return;
		}
		
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();
		
		if(lore.get(0).toPlain().equalsIgnoreCase("Mode: Tool")){
			lore.remove(0);
			lore.add(0, Text.of(TextColors.GREEN, "Mode: ", TextColors.YELLOW, "Information"));
			player.sendMessage(Text.of(TextColors.GREEN, "Information mode"));
		}else{
			lore.remove(0);
			lore.add(0, Text.of(TextColors.GREEN, "Mode: ", TextColors.YELLOW, "Tool"));
			player.sendMessage(Text.of(TextColors.GREEN, "Tool mode"));
		}
		
		Inventory inv = player.getInventory().query(itemStack);
		
		itemStack.offer(Keys.ITEM_LORE, lore);
		inv.set(itemStack);
	}
}
