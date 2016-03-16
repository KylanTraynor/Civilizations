package com.kylantraynor.civilizations.plots;

import org.bukkit.Location;

import com.kylantraynor.civilizations.Group;
import com.kylantraynor.civilizations.Protection;
import com.kylantraynor.civilizations.Settlement;
import com.kylantraynor.civilizations.shapes.Shape;

public class Plot extends Group {
	private PlotType type;
	private Settlement settlement;
	
	public Plot(String name, Shape shape, Settlement settlement){
		super();
		this.setName(name);
		this.settlement = settlement;
		this.getProtection().add(shape);
	}
	
	public Plot(Shape shape, Settlement settlement){
		super();
		this.setProtection(new Protection(this, settlement.getProtection()));
		this.settlement = settlement;
		this.getProtection().add(shape);
	}

	public PlotType getType() { return type; }
	public void setType(PlotType type) { this.type = type; }

	public Settlement getSettlement() { return settlement; }
	public void setSettlement(Settlement settlement) { this.settlement = settlement; }

	public boolean protects(Location location) {
		return getProtection().isInside(location);
	}
}
