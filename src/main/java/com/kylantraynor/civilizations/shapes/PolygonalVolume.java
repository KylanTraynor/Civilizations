package com.kylantraynor.civilizations.shapes;

import org.bukkit.Location;

public class PolygonalVolume extends Polygon2D{
	private int height;
	
	public PolygonalVolume(int height, Location... locations){
		super(locations);
		this.height = height;
	}
	
	@Override
	public int getMinBlockY(){
		return getLocation().getBlockY();
	}
	
	@Override
	public int getMaxBlockY(){
		return getLocation().getBlockY() + height;
	}
	
	@Override
	public boolean isInside(double x, double y, double z){
		if(y < getMinY() || getMaxY() > y) return false;
		return super.isInside(x,y,z);
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