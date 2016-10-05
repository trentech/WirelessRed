package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.init.Items;

public class ReceiverListener {

	private static ConcurrentHashMap<UUID, ReceiverData> cache = new ConcurrentHashMap<>();
	
	@Listener
	public void onClientConnectionEventJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
		checkItemInHand(player);
	}
	
	@Listener(order = Order.POST)
	public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
		Sponge.getScheduler().createTaskBuilder().async().delayTicks(3).execute(task -> {
			checkItemInHand(player);
		}).submit(Main.getPlugin());
	}
	
	@Listener(order = Order.POST)
	public void onBlockChangeEvent(ChangeBlockEvent.Place event, @Root Player player) {
		for(Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getFinal();
			
			if(!snapshot.getState().getType().equals(BlockTypes.STONE_BUTTON)) {
				continue;
			}
			
			Optional<Location<World>> optionalLocation = snapshot.getLocation();
			
			if(!optionalLocation.isPresent()) {
				continue;
			}
			Location<World> location = optionalLocation.get();
			
			if(cache.containsKey(player.getUniqueId())) {
				ReceiverData receiverData = cache.get(player.getUniqueId());

				Receiver receiver = receiverData.receiver().get();

				Optional<Location<World>> optionalTransmitter = receiver.getTransmitter();

				if (!optionalTransmitter.isPresent()) {
					event.setCancelled(true);
					player.sendMessage(Text.of(TextColors.RED, "Not linked to a transmitter"));
					return;
				}
				Location<World> transmitter = optionalTransmitter.get();

				Optional<TransmitterData> optionalTransmitterData = transmitter.get(TransmitterData.class);

				if (!optionalTransmitterData.isPresent()) {
					event.setCancelled(true);
					player.sendMessage(Text.of(TextColors.RED, "Linked transmitter no longer exists"));
					return;
				}
				TransmitterData transmitterData = optionalTransmitterData.get();

				transmitterData.transmitter().get().addReceiver(location);
				transmitter.offer(transmitterData);

				if (transmitterData.transmitter().get().isEnabled()) {
					if (transmitter.getPosition().distance(location.getPosition()) <= transmitterData.transmitter().get().getRange()) {
						receiver.setEnabled(true);
					}
				}

				receiver.save(location);
				
				checkItemInHand(player);
			}
		}
	}

	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event) {
		for (Transaction<BlockSnapshot> transaction : new ArrayList<>(event.getTransactions())) {
			Optional<Location<World>> optionalLocation = transaction.getOriginal().getLocation();

			if (!optionalLocation.isPresent()) {
				return;
			}
			Location<World> location = optionalLocation.get();

			Optional<Receiver> optionalReceiver = Receiver.get(location);

			if (!optionalReceiver.isPresent()) {
				continue;
			}
			Receiver receiver = optionalReceiver.get();

			Receiver.remove(location);
			
			Optional<Player> optionalPlayer = event.getCause().first(Player.class);
			
			if(optionalPlayer.isPresent()) {
				Player player = optionalPlayer.get();
				
				if(player.gameMode().get().equals(GameModes.CREATIVE)) {
					return;
				}
			}
			
			ItemStack itemStack = Items.getReceiver(receiver, 1);

			Item item = (Item) location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
			item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
			
			location.getExtent().spawnEntity(item, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build())));

			transaction.setCustom(BlockTypes.AIR.getDefaultState().snapshotFor(location));
		}
	}

	@Listener
	public void onInteractLinkEvent(InteractBlockEvent.Secondary event, @Root Player player) {
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		if (!location.get(TransmitterData.class).isPresent()) {
			return;
		}

		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (!optionalItemStack.isPresent()) {
			return;
		}
		ItemStack itemStack = optionalItemStack.get();

		Optional<ReceiverData> optionalReceiverData = itemStack.get(ReceiverData.class);

		if (!optionalReceiverData.isPresent()) {
			return;
		}
		ReceiverData receiverData = optionalReceiverData.get();
		Receiver receiver = receiverData.receiver().get();

		receiver.setTransmitter(location);

		player.getInventory().query(itemStack).set(Items.getReceiver(receiver, itemStack.getQuantity()));

		player.sendMessage(Text.of(TextColors.GREEN, "Linked"));
	}

	@Listener
	public void onInteractReceiverEvent(InteractBlockEvent.Secondary event, @Root Player player) {
		BlockSnapshot snapshot = event.getTargetBlock();

		Optional<Location<World>> optionalLocation = snapshot.getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		if (!snapshot.getState().getType().equals(BlockTypes.STONE_BUTTON)) {
			return;
		}

		if (!Receiver.get(location).isPresent()) {
			return;
		}

		event.setUseBlockResult(Tristate.FALSE);
	}
	
	@Listener
	public void onInteractRecieverEventPrimary(InteractBlockEvent.Secondary event, @Root Player player) {
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		Optional<Receiver> optionalReceiver = Receiver.get(location);

		if (!optionalReceiver.isPresent()) {
			return;
		}
		Receiver receiver = optionalReceiver.get();

		Scoreboard scoreboard = Scoreboard.builder().build();

		Objective objective = Objective.builder().displayName(Text.of(TextColors.GREEN, "    Receiver Info    ")).name("receiverinfo").criterion(Criteria.DUMMY).build();

		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, receiver.isEnabled())).setScore(3);

		Optional<Location<World>> optionalTransmitterLocation = receiver.getTransmitter();

		if (optionalTransmitterLocation.isPresent()) {
			Location<World> transmitterLocation = optionalTransmitterLocation.get();

			Optional<TransmitterData> optionalTransmitterData = transmitterLocation.get(TransmitterData.class);

			if (optionalTransmitterData.isPresent()) {
				objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.WHITE, transmitterLocation.getExtent().getName(), " ", transmitterLocation.getBlockX(), " ", transmitterLocation.getBlockY(), " ", transmitterLocation.getBlockZ())).setScore(2);

				Transmitter transmitter = optionalTransmitterData.get().transmitter().get();

				if (Transmitter.isInRange(transmitter, transmitterLocation, location)) {
					if (transmitter.getReceivers().contains(location)) {
						objective.getOrCreateScore(Text.of(TextColors.GREEN, "- In range")).setScore(1);
					} else {
						objective.getOrCreateScore(Text.of(TextColors.RED, "- Not Linked")).setScore(1);
					}
				} else {
					objective.getOrCreateScore(Text.of(TextColors.RED, "- Out of range")).setScore(1);
				}
			} else {
				objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Not found")).setScore(1);
			}
		} else {
			objective.getOrCreateScore(Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Location Error")).setScore(1);
		}

		scoreboard.addObjective(objective);
		scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

		player.setScoreboard(scoreboard);

		Sponge.getScheduler().createTaskBuilder().async().delayTicks(100).execute(runnable -> {
			player.setScoreboard(Scoreboard.builder().build());
		}).submit(Main.getPlugin());
	}
	
	public static void checkItemInHand(Player player) {
		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
		
		if(optionalItemStack.isPresent()) {
			ItemStack itemStack = optionalItemStack.get();

			Optional<ReceiverData> optionalData = itemStack.get(ReceiverData.class);
			
			if(optionalData.isPresent()) {
				cache.put(player.getUniqueId(), optionalData.get());

				return;
			}
		}

		cache.remove(player.getUniqueId());
	}
}
