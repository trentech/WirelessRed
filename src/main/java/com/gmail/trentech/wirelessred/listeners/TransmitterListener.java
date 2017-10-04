package com.gmail.trentech.wirelessred.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
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
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.ImmutableTransmitterData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.init.Items;

public class TransmitterListener {

	private static ConcurrentHashMap<UUID, TransmitterData> cache = new ConcurrentHashMap<>();
	
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
	
	@Listener
	public void onChangeSignEvent(ChangeSignEvent event, @Root Player player) {
		Sign sign = event.getTargetTile();
		Location<World> location = sign.getLocation();
		
		if(cache.containsKey(player.getUniqueId())) {
			TransmitterData transmitterData = cache.get(player.getUniqueId());
			Transmitter transmitter = transmitterData.transmitter().get();
			
			for (Location<World> receiverLocation : new ArrayList<>(transmitter.getReceivers())) {
				Optional<Receiver> optionalReceiver = Receiver.get(receiverLocation);

				if (!optionalReceiver.isPresent()) {
					transmitter.removeReceiver(receiverLocation);
					continue;
				}
				Receiver receiver = optionalReceiver.get();

				receiver.setTransmitter(location);
				receiver.updateTransmitter(receiverLocation);
			}

			List<Text> lines = new ArrayList<>();
			
			if(Transmitter.toggle(transmitterData, location)) {
				lines.add(Text.of(TextColors.GREEN, "======="));
				lines.add(Text.of(TextColors.GREEN, "====="));
				lines.add(Text.of(TextColors.GREEN, "==="));
				lines.add(Text.of(TextColors.GREEN, "="));
			} else {
				lines.add(Text.EMPTY);
				lines.add(Text.EMPTY);
				lines.add(Text.of(TextColors.RED, "==="));
				lines.add(Text.of(TextColors.RED, "="));
			}
			
			event.getText().setElements(lines);

			checkItemInHand(player);
		}	
	}
	
	@Listener
	public void onChangeBlockEventBreak(ChangeBlockEvent.Break event) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot snapshot = transaction.getOriginal();

			Optional<Location<World>> optionalLocation = snapshot.getLocation();

			if (!optionalLocation.isPresent()) {
				return;
			}
			Location<World> location = optionalLocation.get();

			Optional<ImmutableTransmitterData> optionalTransmitterData = snapshot.get(ImmutableTransmitterData.class);

			if (!optionalTransmitterData.isPresent()) {
				continue;
			}
			TransmitterData transmitterData = optionalTransmitterData.get().asMutable();

			Transmitter.toggle(transmitterData, location, false);

			Optional<Player> optionalPlayer = event.getCause().first(Player.class);
			
			if(optionalPlayer.isPresent()) {
				Player player = optionalPlayer.get();
				
				if(player.gameMode().get().equals(GameModes.CREATIVE)) {
					return;
				}
			}
			
			ItemStack itemStack = Items.getTransmitter(transmitterData);

			Item item = (Item) location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
			item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
			
			location.getExtent().spawnEntity(item);
			
			transaction.setCustom(BlockTypes.AIR.getDefaultState().snapshotFor(location));
		}
	}

	@Listener
	public void onInteractUpgradeEvent(InteractBlockEvent.Secondary event, @First Player player) {
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

		if (!optionalTransmitterData.isPresent()) {
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();

		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (!optionalItemStack.isPresent()) {
			return;
		}
		ItemStack itemStack = optionalItemStack.get();

		Optional<Text> optionalDisplayName = itemStack.get(Keys.DISPLAY_NAME);

		if (!optionalDisplayName.isPresent()) {
			return;
		}

		if (!optionalDisplayName.get().toPlain().equalsIgnoreCase("Transmitter Upgrade")) {
			return;
		}

		List<Text> lore = itemStack.get(Keys.ITEM_LORE).get();

		String upgrade = lore.get(0).toPlain().replace("Range: ", "");

		Transmitter transmitter = transmitterData.transmitter().get();

		if ((upgrade.equalsIgnoreCase("Unlimited") && transmitter.isMultiWorld()) || (!upgrade.equalsIgnoreCase("Unlimited") && Double.parseDouble(upgrade) <= transmitter.getRange())) {
			player.sendMessage(Text.of(TextColors.RED, "Transmitter already contains current upgrade"));
			return;
		}

		if (upgrade.equalsIgnoreCase("Unlimited")) {
			transmitter.setMultiWorld(true);
			transmitter.setRange(60000000);
		} else {
			transmitter.setRange(Double.parseDouble(upgrade));
		}

		Transmitter.toggle(transmitterData, location);

		player.getInventory().query(itemStack).poll(1);

		player.sendMessage(Text.of(TextColors.GREEN, "Transmitter upgraded"));
	}

	@Listener
	public void onNotifyNeighborBlockEvent(NotifyNeighborBlockEvent event, @First BlockSnapshot snapshot) {
		if (snapshot.get(Keys.POWER).isPresent()) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

				if (optionalTransmitterData.isPresent()) {
					Transmitter.toggle(optionalTransmitterData.get(), location, (snapshot.get(Keys.POWER).get() >= 1));
				}
			}
		} else if (snapshot.get(Keys.POWERED).isPresent()) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

				if (optionalTransmitterData.isPresent()) {
					Transmitter.toggle(optionalTransmitterData.get(), location, snapshot.get(Keys.POWERED).get());
				}
			}
		} else if (snapshot.getState().getType().equals(BlockTypes.REDSTONE_BLOCK)) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

				if (optionalTransmitterData.isPresent()) {
					Transmitter.toggle(optionalTransmitterData.get(), location, true);
				}
			}
		} else if (snapshot.getState().getType().equals(BlockTypes.REDSTONE_TORCH)) {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

				if (optionalTransmitterData.isPresent()) {
					Transmitter.toggle(optionalTransmitterData.get(), location, true);
				}
			}
		} else {
			for (Entry<Direction, BlockState> entry : event.getNeighbors().entrySet()) {
				Location<World> location = snapshot.getLocation().get().getRelative(entry.getKey());

				Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

				if (optionalTransmitterData.isPresent()) {
					Transmitter.toggle(optionalTransmitterData.get(), location, false);
				}
			}
		}
	}

	@Listener
	public void onInteractEventSecondary(InteractBlockEvent.Secondary event, @Root Player player) {
		Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

		if (!optionalLocation.isPresent()) {
			return;
		}
		Location<World> location = optionalLocation.get();

		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);

		if (optionalItemStack.isPresent()) {
			if(optionalItemStack.get().getType().equals(ItemTypes.STONE_BUTTON)) {
				return;
			}
		}

		Optional<TransmitterData> optionalTransmitterData = location.get(TransmitterData.class);

		if (!optionalTransmitterData.isPresent()) {
			return;
		}
		TransmitterData transmitterData = optionalTransmitterData.get();
		Transmitter transmitter = transmitterData.transmitter().get();

		int score = transmitter.getReceivers().size() + 5;

		Scoreboard scoreboard = Scoreboard.builder().build();

		Objective objective = Objective.builder().displayName(Text.of(TextColors.GREEN, "     Transmitter Info     ")).name("transmitterinfo").criterion(Criteria.DUMMY).build();

		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Upgrades")).setScore(score--);

		if (transmitter.getRange() == 60000000) {
			objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Range: ", TextColors.WHITE, "Unlimited")).setScore(score--);
		} else {
			objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Range: ", TextColors.WHITE, transmitter.getRange())).setScore(score--);
		}

		objective.getOrCreateScore(Text.of(TextColors.YELLOW, "  Multi-World: ", TextColors.WHITE, transmitter.isMultiWorld())).setScore(score--);
		objective.getOrCreateScore(Text.EMPTY).setScore(score--);
		objective.getOrCreateScore(Text.of(TextColors.GREEN, "Receivers")).setScore(score--);

		for (Location<World> receiver : transmitter.getReceivers()) {
			if (Transmitter.isInRange(transmitter, location, receiver)) {
				objective.getOrCreateScore(Text.of("- ", receiver.getExtent().getName(), " ", receiver.getBlockX(), " ", receiver.getBlockY(), " ", receiver.getBlockZ())).setScore(score--);
			} else {
				objective.getOrCreateScore(Text.of(TextColors.RED, "- ", receiver.getExtent().getName(), " ", receiver.getBlockX(), " ", receiver.getBlockY(), " ", receiver.getBlockZ())).setScore(score--);
			}
		}

		scoreboard.addObjective(objective);
		scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

		player.setScoreboard(scoreboard);

		Sponge.getScheduler().createTaskBuilder().async().delayTicks(100).execute(runnable -> {
			player.setScoreboard(Scoreboard.builder().build());
		}).submit(Main.getPlugin());	
	}
	
	@Listener
	public void onLoadChunkEvent(UnloadChunkEvent event) {
		for (TileEntity tileEntity : event.getTargetChunk().getTileEntities(getFilter())) {
			Location<World> location = tileEntity.getLocation();

			Transmitter transmitter = location.get(TransmitterData.class).get().transmitter().get();

			if (!transmitter.isEnabled()) {
				return;
			}

			Transmitter.disableParticles(location);
		}
	}

	@Listener
	public void onLoadChunkEvent(LoadChunkEvent event) {
		for (TileEntity tileEntity : event.getTargetChunk().getTileEntities(getFilter())) {
			Location<World> location = tileEntity.getLocation();

			Transmitter transmitter = location.get(TransmitterData.class).get().transmitter().get();

			if (!transmitter.isEnabled()) {
				return;
			}

			Transmitter.enableParticles(location);
		}
	}
	
	private Predicate<TileEntity> getFilter() {
		return new Predicate<TileEntity>() {

			@Override
			public boolean test(TileEntity tileEntity) {
				return tileEntity.getLocation().get(TransmitterData.class).isPresent();
			}
		};
	}
	
	public static void checkItemInHand(Player player) {
		Optional<ItemStack> optionalItemStack = player.getItemInHand(HandTypes.MAIN_HAND);
		
		if(optionalItemStack.isPresent()) {
			ItemStack itemStack = optionalItemStack.get();

			Optional<Text> optionalName = itemStack.get(Keys.DISPLAY_NAME);
			
			if(!optionalName.isPresent()) {
				return;
			}		

			if(!optionalName.get().toPlain().equalsIgnoreCase("Transmitter")) {
				return;
			}
			Optional<TransmitterData> optionalData = itemStack.get(TransmitterData.class);

			TransmitterData data;
			if (!optionalData.isPresent()) {
				data = new TransmitterData();
				
				itemStack.offer(data);
				
				player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
			} else {
				data = optionalData.get();
			}

			cache.put(player.getUniqueId(), data);

			return;
		}

		cache.remove(player.getUniqueId());
	}
}
