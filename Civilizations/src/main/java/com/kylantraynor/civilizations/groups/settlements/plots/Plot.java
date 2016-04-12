package com.kylantraynor.civilizations.groups.settlements.plots;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
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
		Cache.plotListChanged = true;
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
	 * Destroys this plot.
	 * @return true if the plot has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			getProtection().hide(p);
		}
		Cache.plotListChanged = true;
		return super.remove();
	}
	
	@Override
	public boolean save(){
		return false;
	}
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
	
	public static List<Plot> getAll(){
		return Cache.getPlotList();
	}
	
	public static Plot getAt(Location location){
		for(Plot p : getAll()){
			if(p.protects(location)) return p;
		}
		return null;
	}
}
