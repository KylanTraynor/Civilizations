package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Sphere extends Shape {
	
	int radius;

	public Sphere(Location location, int radius) {
		super(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		this.radius = radius;
	}

	@Override
	int getWidth() {
		return this.radius * 2;
	}

	@Override
	int getHeight() {
		return this.radius * 2;
	}

	@Override
	int getLength() {
		return this.radius * 2;
	}

	@Override
	int getVolume() {
		return (int) ((4.0/3.0) * (Math.PI * this.radius * this.radius * this.radius));
	}

	@Override
	int getArea() {
		return (int)(Math.PI * this.radius * this.radius);
	}

	@Override
	boolean isInside(double x, double y, double z) {
		x = x - this.getLocation().getX();
		y = y - this.getLocation().getY();
		z = z - this.getLocation().getZ();
		double distance = Math.sqrt(x * x + y * y + z * z); 
		if(distance <= this.radius) return true;
		return false;
	}
	
	@Override
	public String toString() {
		String result = "Sphere;" + this.getLocation().getWorld().getName() + ";" +
				this.getLocation().getX() + ";" + this.getLocation().getY() +
				";" + this.getLocation().getZ() + ";" + getRadius();
		return result;
	}

	private int getRadius() {
		return this.radius;
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					if(isInside(getLocation().getBlockX() + x, getLocation().getBlockY() + y, getLocation().getBlockZ() + z)){
						list.add(getLocation().clone().add(x, y, z));
					}
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		List<Block> list = new ArrayList<Block>();
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					Location l = new Location(getLocation().getWorld(), getLocation().getBlockX() + x, getLocation().getBlockY() + y, getLocation().getBlockZ() + z);
					if(getLocation().distance(l) <= radius && getLocation().distance(l) > (radius - 1)){
						list.add(getLocation().clone().add(x, y, z).getBlock());
					}
				}
			}
		}
		return list.toArray(new Block[list.size()]);
	}
}
