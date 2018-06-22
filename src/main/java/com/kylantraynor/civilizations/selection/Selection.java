package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;

import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shapes.Visualizable;

public interface Selection extends Visualizable{
	boolean isValid();
	String getValidityReason();
	Location getLocation();
	double distanceSquared(Location location);
	double distanceSquared(Shape shape);
	double getWidth();
	double getHeight();
	double getLength();
}
