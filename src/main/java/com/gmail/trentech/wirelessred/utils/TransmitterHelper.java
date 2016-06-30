package com.gmail.trentech.wirelessred.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;

public class TransmitterHelper {

	public static boolean toggleTransmitter(TransmitterData transmitterData, Location<World> location) {
		boolean b = isPowered(location);
		toggleTransmitter(transmitterData, location, b);
		return b;
	}

	public static void toggleTransmitter(TransmitterData transmitterData, Location<World> location, boolean enable) {
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

			receiverLocation.offer(Keys.POWERED, enable);
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
}
