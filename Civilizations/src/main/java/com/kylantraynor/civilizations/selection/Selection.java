package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;

import com.kylantraynor.civilizations.shapes.Prism;

public class Selection extends Prism{
	
	private boolean isValid = false;
	private String validityReason = "";

	public Selection(Location location, int width, int height, int length) {
		super(location, width, height, length);
	}
	
	public Selection(Location corner1, Location corner2){
		super(getFirstCorner(corner1, corner2),
				Math.abs(corner1.getBlockX() - corner2.getBlockX()),
				Math.abs(corner1.getBlockY() - corner2.getBlockY()),
				Math.abs(corner1.getBlockZ() - corner2.getBlockZ()));
		if(corner1.getWorld() == corner2.getWorld()){
			isValid = true;
		} else {
			isValid = false;
			validityReason = "Pos 1 in " + corner1.getWorld() + ", Pos 2 in " + corner2.getWorld() + "."; 
		}
	}
	
	public static Location getFirstCorner(Location corner1, Location corner2){
		return new Location(corner1.getWorld(), Math.min(corner1.getBlockX(), corner2.getBlockX()),
				Math.min(corner1.getBlockY(), corner2.getBlockY()),
				Math.min(corner1.getBlockZ(), corner2.getBlockZ()));
	}
	
	@Override
	public int getWidth(){
		return getMaxX() - getMinX() + 1;
	}
	
	@Override
	public int getLength(){
		return getMaxZ() - getMinZ() + 1;
	}
	
	@Override
	public int getHeight(){
		return getMaxY() - getMinY() + 1;
	}
	
	public boolean isValid(){
		return isValid;
	}
	
	public String getValidityReason(){
		return validityReason;
	}
}
