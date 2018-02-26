package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;

import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shapes.Visualizable;

public interface Selection extends Visualizable{
	public boolean isValid();
	public String getValidityReason();
	public Location getLocation();
	public double distanceSquared(Location location);
	public double distanceSquared(Shape shape);
	public double getWidth();
	public double getHeight();
	public double getLength();
}
