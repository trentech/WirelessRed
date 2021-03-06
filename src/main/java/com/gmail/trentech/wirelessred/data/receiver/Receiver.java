package com.gmail.trentech.wirelessred.data.receiver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjc.core.SQLManager;
import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.DataQueries;

public class Receiver implements DataSerializable {

	private boolean enabled = false;
	private String transmitter = "";

	public Receiver() {

	}

	public Receiver(boolean enabled) {
		this.enabled = enabled;
	}

	public Receiver(boolean enabled, String transmitter) {
		this.enabled = enabled;
		this.transmitter = transmitter;
	}

	public Optional<Location<World>> getTransmitter() {
		String[] args = this.transmitter.split(":");

		if (!Sponge.getServer().getWorld(args[0]).isPresent()) {
			return Optional.empty();
		}
		World world = Sponge.getServer().getWorld(args[0]).get();

		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);

		return Optional.of(world.getLocation(x, y, z));
	}

	public void setTransmitter(Location<World> location) {
		this.transmitter = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	public DataContainer toContainer() {
		return DataContainer.createNew().set(DataQueries.ENABLED, enabled).set(DataQueries.TRANSMITTER, transmitter);
	}

	public static Optional<Receiver> get(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		Optional<Receiver> optional = Optional.empty();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + sqlManager.getPrefix("RECEIVERS"));

			ResultSet result = statement.executeQuery();

			while (result.next()) {
				if (result.getString("Location").equalsIgnoreCase(name)) {
					boolean enabled = result.getBoolean("Enabled");
					String transmitter = result.getString("Transmitter");
					optional = Optional.of(new Receiver(enabled, transmitter));
				}
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return optional;
	}

	public void save(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("INSERT into " + sqlManager.getPrefix("RECEIVERS") + " (Location, Enabled, Transmitter) VALUES (?, ?, ?)");

			statement.setString(1, name);
			statement.setBoolean(2, enabled);
			statement.setString(3, transmitter);

			statement.executeUpdate();

			connection.close();
			
			Sponge.getScheduler().createTaskBuilder().delayTicks(5).execute(c -> {
				location.offer(Keys.POWERED, enabled);
			}).submit(Main.getPlugin());			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateEnabled(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();
			
			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("RECEIVERS") + " SET Enabled = ? WHERE Location = ?");

			statement.setBoolean(1, enabled);
			statement.setString(2, name);

			statement.executeUpdate();
			connection.close();
			
			Sponge.getScheduler().createTaskBuilder().delayTicks(5).execute(c -> {
				location.offer(Keys.POWERED, enabled);
			}).submit(Main.getPlugin());	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateTransmitter(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();
			
			PreparedStatement statement = connection.prepareStatement("UPDATE " + sqlManager.getPrefix("RECEIVERS") + " SET Transmitter = ? WHERE Location = ?");

			statement.setString(1, transmitter);
			statement.setString(2, name);

			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void remove(Location<World> location) {
		String name = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();

		try {
			SQLManager sqlManager = SQLManager.get(Main.getPlugin());
			Connection connection = sqlManager.getDataSource().getConnection();

			PreparedStatement statement = connection.prepareStatement("DELETE from " + sqlManager.getPrefix("RECEIVERS") + " WHERE Location = ?");

			statement.setString(1, name);
			statement.executeUpdate();

			connection.close();
			
			Sponge.getScheduler().createTaskBuilder().delayTicks(5).execute(c -> {
				location.offer(Keys.POWERED, false);
			}).submit(Main.getPlugin());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
