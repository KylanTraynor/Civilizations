package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Hull extends Shape {
	
	private List<Location> points = new ArrayList<Location>();

	public Hull(Location location) {
		super(location);
	}

	@Override
	int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	boolean isInside(double x, double y, double z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location[] getBlockLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block[] getBlockSurface() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addPoint(Location location){
		points.add(location);
	}
	
	public Location getMassCenter(){
		int totalX = 0;
		int totalZ = 0;
		int totalY = 0;
		for(Location l : points){
			totalX += l.getBlockX();
			totalZ += l.getBlockZ();
			totalY += l.getBlockY();
		}
		return new Location(getLocation().getWorld(), totalX / points.size(), totalY / points.size(), totalZ / points.size());
	}
	
	public int getMaxDistanceFromCenter(){
		int distance = 0;
		Location center = getMassCenter();
		for(Location l : points){
			if(l.distance(center) > distance){
				distance = (int) l.distance(center);
			}
		}
		return distance;
	}
}
