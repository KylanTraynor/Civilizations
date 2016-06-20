package com.kylantraynor.civilizations.settings;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SettlementSettings extends GroupSettings{
	
	private Location location;

	
	/**
	 * Gets the location of the settlement.
	 * @return Location
	 */
	public Location getLocation(){
		if(location == null && this.contains("General.Location.X")){
			int x = this.getInt("General.Location.X");
			int y = this.getInt("General.Location.Y");
			int z = this.getInt("General.Location.Z");
			String world = this.getString("General.Location.World");
			location = new Location(Bukkit.getWorld(world), x, y, z);
		}
		return location;
	}
	
	/**
	 * Sets the location of the settlement.
	 * @param location
	 */
	public void setLocation(Location location){
		if(location != null){
			this.set("General.Location.X", location.getBlockX());
			this.set("General.Location.Y", location.getBlockY());
			this.set("General.Location.Z", location.getBlockZ());
			this.set("General.Location.World", location.getWorld().getName());
		} else {
			this.set("General.Location", null);
		}
		this.location = location;
	}
	
}
