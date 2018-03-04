package com.kylantraynor.civilizations.settings;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SettlementSettings extends GroupSettings{
	
	private Location location;
	private List<Shape> shapes;

	
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
		this.setChanged(true);
	}

	public void setShapes(List<Shape> shapes) {
		if(shapes != null){
			try{
				this.set("Protection.Shape", Utils.getShapesString(shapes));
				this.shapes = shapes;
			} catch(Exception e){
				Civilizations.currentInstance.getLogger().warning("Couldn't save protection shapes for " + this.getName() + ".");
				e.printStackTrace();
			}
		} else {
			this.set("Protection.Shape", null);
			this.shapes = null;
		}
	}

	public List<Shape> getShapes(){
	    if(shapes != null) return shapes;
		if(this.contains("Protection.Shape")){
			return shapes = Utils.parseShapes(this.getString("Protection.Shape"));
		}
		return shapes = new ArrayList<Shape>();
	}
}
