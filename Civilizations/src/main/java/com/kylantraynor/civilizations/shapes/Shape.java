package com.kylantraynor.civilizations.shapes;

import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class Shape {
	int width;
	int height;
	int length;
	private Location location;
	
	public Shape(Location location){
		this.location = location;
	}
	
	abstract int getWidth();
	abstract int getHeight();
	abstract int getLength();
	abstract int getVolume();
	abstract int getArea();
	abstract boolean isInside(double x, double y, double z);
	public abstract String toString();
	public abstract Location[] getBlockLocations();
	public abstract Block[] getBlockSurface();
	
	public boolean isInside(Location location){
		if(location.getWorld().equals(this.location.getWorld())){
			return isInside(location.getX(), location.getY(), location.getZ());
		} else {
			return false;
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
