package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Prism extends Shape {

	public Prism(Location location, int width, int height, int length) {
		super(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		this.width = width;
		this.height = height;
		this.length = length;
	}

	@Override
	int getWidth() {
		return this.width;
	}

	@Override
	int getHeight() {
		return this.height;
	}

	@Override
	int getLength() {
		return this.length;
	}

	@Override
	int getVolume() {
		return getWidth() * getHeight() * getLength();
	}

	@Override
	int getArea() {
		return getWidth() * getLength();
	}
	
	Location[] getCorners(){
		Location c1 = getLocation().clone();
		Location c2 = getLocation().clone().add(this.width, this.height, this.length);
		return new Location[]{c1, c2};
	}

	@Override
	boolean isInside(double x, double y, double z) {
		if(x >= getLocation().getBlockX() && x <= getLocation().getBlockX() + width){
			if(y >= getLocation().getBlockY() && y <= getLocation().getBlockY() + height){
				if(z >= getLocation().getBlockZ() && z <= getLocation().getBlockZ() + length){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String result = "Prism;" + getLocation().getWorld().getName() + ";" +
				getLocation().getX() + ";" + getLocation().getY() +
				";" + getLocation().getZ() + ";" + getWidth() + ";" +
				getHeight() + ";" + getLength();
		return result;
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				for(int z = 0; z < length; z++){
					list.add(getLocation().clone().add(x, y, z));
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		List<Block> list = new ArrayList<Block>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				for(int z = 0; z < length; z++){
					if((x == 0 || x == width - 1) || (y == 0 || y == height - 1) || (z == 0 || z == length - 1)){
						list.add(getLocation().clone().add(x, y, z).getBlock());
					}
				}
			}
		}
		return list.toArray(new Block[list.size()]);
	}
}