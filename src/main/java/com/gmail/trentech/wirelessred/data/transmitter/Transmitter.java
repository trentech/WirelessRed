package com.gmail.trentech.wirelessred.data.transmitter;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.data.DataQueries;
import com.gmail.trentech.wirelessred.data.receiver.Receiver;

public class Transmitter implements DataSerializable {

	private boolean enabled = false;
	private List<String> receivers = new ArrayList<>();
	private double range = 32;
	private boolean multiWorld = false;
	
	public Transmitter(){

	}

	public Transmitter(boolean enabled, double range, boolean multiWorld, List<String> receivers){
		this.enabled = enabled;
		this.range = range;
		this.multiWorld = multiWorld;
		this.receivers = receivers;
	}

	public List<Location<World>> getReceivers(){
		List<Location<World>> locations = new ArrayList<>();
		
		for(String receiver : this.receivers){
			String[] args = receiver.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			int z = Integer.parseInt(args[3]);

			Location<World> location = world.getLocation(x, y, z);
			
			if(Receiver.get(location).isPresent()){
				locations.add(location);
			}
		}
		
		return locations;
	}
	
	public boolean addReceiver(Location<World> location){
		String loc = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
		return this.receivers.add(loc);
	}
	
	public boolean removeReceiver(Location<World> location){
		String loc = location.getExtent().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
		return this.receivers.remove(loc);
	}

	public boolean isEnabled(){
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public double getRange(){
		return this.range;
	}
	
	public void setRange(int range){
		this.range = range;
	}
	
	public boolean isMultiWorld() {
		return multiWorld;
	}

	public void setMultiWorld(boolean multiWorld) {
		this.multiWorld = multiWorld;
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
