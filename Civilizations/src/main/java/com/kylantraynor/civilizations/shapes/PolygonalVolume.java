package com.kylantraynor.civilizations.shapes;

import org.bukkit.Location;

public class PolygonalVolume extends Polygon2D{
	private int height;
	
	public PolygonalVolume(int height, Location... locations){
		super(locations);
		this.height = height;
	}
	
	@Override
	public int getMinY(){
		return getLocation().getBlockY();
	}
	
	@Override
	public int getMaxY(){
		return getLocation().getBlockY() + height;
	}
	
	@Override
	public boolean isInside(double x, double y, double z){
		if(!super.isInside(x, y, z)) return false;
		return getMinY() < y && y < getMaxY();
	}
	
	@Override
	public double distanceSquared(Location location){
		if(getMinY() < location.getBlockY() && location.getBlockY() < getMaxY()){
			return super.distanceSquared(location);
		} else {
			if(getMinY() > location.getBlockY()){
				double dy = location.getBlockY() - getMinY();
				return super.distanceSquared(location) + dy*dy;
			} else{
				double dy = getMaxY() - location.getBlockY();
				return super.distanceSquared(location) + dy*dy;
			}
		}
	}
}