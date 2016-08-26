package com.gmail.trentech.wirelessred.data.transmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.DataQueries;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;

public class Transmitter implements DataSerializable {

	private boolean enabled = false;
	private List<String> receivers = new ArrayList<>();
	private double range;
	private boolean multiWorld = false;

	public Transmitter() {
		String range = Main.getConfigManager().getConfig().getNode("settings", "starting_range").getString();
		
		if (range.equalsIgnoreCase("unlimited")) {
			this.range = 60000000;
			this.multiWorld = true;
		} else {
			this.range = Double.parseDouble(range);
		}
	}

	public Transmitter(boolean enabled, double range, boolean multiWorld, List<String> receivers) {
		this.enabled = enabled;
		this.range = range;
		this.multiWorld = multiWorld;
		this.receivers = receivers;
	}

	public List<Location<World>> getReceivers() {
		List<Location<World>> locations = new ArrayList<>();

		for (String receiver : this.receivers) {
			String[] args = receiver.split(":");

			if (!Sponge.getServer().getWorld(args[0]).isPresent()) {
				continue;
			}
			World world = Sponge.getServer().getWorld(args[0]).get();

			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			int z = Integer.parseInt(args[3]);

			Location<World> location = world.getLocation(x, y, z);

			if (Receiver.get(location).isPresent()) {
				locations.add(location);
			}
		}

		return locations;
	}

	public boolean addReceiver(Location<World> location) {
		String loc = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
		return this.receivers.add(loc);
	}

	public boolean removeReceiver(Location<World> location) {
		String loc = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
		return this.receivers.remove(loc);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public double getRange() {
		return this.range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public boolean isMultiWorld() {
		return multiWorld;
	}

	public void setMultiWorld(boolean multiWorld) {
		this.multiWorld = multiWorld;
	}

	public static boolean toggle(TransmitterData transmitterData, Location<World> location) {
		boolean b = isPowered(location);
		toggle(transmitterData, location, b);
		return b;
	}

	public static void toggle(TransmitterData transmitterData, Location<World> location, boolean enable) {
		Transmitter transmitter = transmitterData.transmitter().get();
		transmitter.setEnabled(enable);

		for (Location<World> receiverLocation : transmitterData.transmitter().get().getReceivers()) {
			Optional<Receiver> optionalReceiver = Receiver.get(receiverLocation);

			if (!optionalReceiver.isPresent()) {
				transmitter.removeReceiver(receiverLocation);
				continue;
			}
			Receiver receiver = optionalReceiver.get();

			if (!isInRange(transmitter, location, receiverLocation)) {
				continue;
			}

			receiver.setEnabled(enable);
			receiver.updateEnabled(receiverLocation);
			receiverLocation.offer(Keys.POWERED, enable, Cause.of(NamedCause.source(Main.getPlugin())));
		}

		List<Text> lines = new ArrayList<>();

		if (enable) {
			lines.add(Text.of(TextColors.DARK_BLUE, "[Transmitter]"));
			lines.add(Text.of(TextColors.GREEN, "====="));
			lines.add(Text.of(TextColors.GREEN, "==="));
			lines.add(Text.of(TextColors.GREEN, "="));
			enableParticles(location);
		} else {
			lines.add(Text.of(TextColors.DARK_BLUE, "[Transmitter]"));
			lines.add(Text.EMPTY);
			lines.add(Text.EMPTY);
			lines.add(Text.of(TextColors.RED, "="));

			disableParticles(location);
		}

		location.offer(Keys.SIGN_LINES, lines);

		location.offer(transmitterData);
	}

	private static boolean isPowered(Location<World> location) {
		List<Direction> directions = new ArrayList<>();

		directions.add(Direction.NORTH);
		directions.add(Direction.SOUTH);
		directions.add(Direction.EAST);
		directions.add(Direction.WEST);

		for (Direction direction : directions) {
			Location<World> relative = location.getRelative(direction);
			if (relative.get(Keys.POWERED).isPresent() && relative.get(Keys.POWERED).get()) {
				return true;
			}
			if (relative.get(Keys.POWER).isPresent() && (relative.get(Keys.POWER).get() >= 1)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInRange(Transmitter transmitter, Location<World> transmitterLocation, Location<World> receiverLocation) {
		if ((receiverLocation.getExtent() != transmitterLocation.getExtent()) && !transmitter.isMultiWorld()) {
			return false;
		}

		if (receiverLocation.getPosition().distance(transmitterLocation.getPosition()) > transmitter.getRange()) {
			return false;
		}
		return true;
	}

	public static void enableParticles(Location<World> location) {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		if (!Sponge.getScheduler().getTasksByName(name).isEmpty()) {
			return;
		}

		ParticleEffect particle = ParticleEffect.builder().type(ParticleTypes.REDSTONE).build();

		Sponge.getScheduler().createTaskBuilder().interval(400, TimeUnit.MILLISECONDS).name(name).execute(t -> {
			if (random.nextDouble() > .8) {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
			} // .add(.5, .8, .5));
			if (random.nextDouble() > .8) {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
			} // .add(.5, .8, .5));
			if (random.nextDouble() > .8) {
				location.getExtent().spawnParticles(particle, location.getPosition().add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
			} // .add(.5, .8, .5));
		}).submit(Main.getPlugin());
	}

	public static void disableParticles(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		for (Task task : Sponge.getScheduler().getScheduledTasks()) {
			if (task.getName().equals(name)) {
				task.cancel();
			}
		}
	}
	
	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return new MemoryDataContainer().set(DataQueries.ENABLED, enabled).set(DataQueries.RANGE, range).set(DataQueries.MULTIWORLD, multiWorld).set(DataQueries.RECEVIERS, receivers);
	}
}
