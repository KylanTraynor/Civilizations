package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;

import com.kylantraynor.civilizations.shapes.Prism;

public class Selection extends Prism{

	public Selection(Location location, int width, int height, int length) {
		super(location, width, height, length);
	}
	
	public Selection(Location corner1, Location corner2){
		super(getFirstCorner(corner1, corner2),
				corner1.getBlockX() - corner2.getBlockX(),
				corner1.getBlockY() - corner2.getBlockY(),
				corner1.getBlockZ() - corner2.getBlockZ());
	}
	
	public static Location getFirstCorner(Location corner1, Location corner2){
		return new Location(corner1.getWorld(), Math.min(corner1.getBlockX(), corner2.getBlockX()),
				Math.min(corner1.getBlockY(), corner2.getBlockY()),
				Math.min(corner1.getBlockZ(), corner2.getBlockZ()));
	}
	
}
