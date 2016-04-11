package com.kylantraynor.civilizations.groups.settlements.plots;

import org.bukkit.Location;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.shapes.Shape;

public class Plot extends Group {
	private PlotType type;
	private Settlement settlement;
	
	public Plot(String name, Shape shape, Settlement settlement){
		super();
		this.setName(name);
		setSettlement(settlement);
		this.getProtection().add(shape);
	}
	
	public Plot(Shape shape, Settlement settlement){
		super();
		this.setProtection(new Protection(this, settlement.getProtection()));
		setSettlement(settlement);
		this.getProtection().add(shape);
	}
	@Override
	public String getType() {
		return "Plot";
	}
	/**
	 * Gets the type of this plot.
	 * @return PlotType
	 */
	public PlotType getPlotType() { return type; }
	/**
	 * Sets the type of this plot.
	 * @param type
	 */
	public void setPlotType(PlotType type) { this.type = type; }
	/**
	 * Gets the settlement owning this plot.
	 * @return Settlement
	 */
	public Settlement getSettlement() { return settlement; }
	/**
	 * Sets the settlement this plot belongs to.
	 * @param settlement
	 */
	public void setSettlement(Settlement settlement) {
		Settlement oldSettlement = this.settlement;
		if(oldSettlement != null){
			oldSettlement.removePlot(this);
		}
		this.settlement = settlement;
		if(this.settlement != null){
			this.settlement.addPlot(this);
		}
	}
	/**
	 * Checks if this plot protects the given location.
	 * @param location
	 * @return true if the location is protected, false otherwise.
	 */
	public boolean protects(Location location) {
		return getProtection().isInside(location);
	}
}
