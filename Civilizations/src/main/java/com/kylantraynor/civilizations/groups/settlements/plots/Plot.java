package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.shapes.Shape;

public class Plot extends Group {
	private PlotType type;
	private Settlement settlement;
	private boolean persistent = false;
	
	//Constructor for reloads from file
	public Plot(){
		super();
	}
	
	public Plot(String name, Shape shape, Settlement settlement){
		super();
		this.setName(name);
		setSettlement(settlement);
		this.getProtection().add(shape);
		Cache.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(String name, List<Shape> shapes, Settlement settlement){
		super();
		this.setName(name);
		setSettlement(settlement);
		this.getProtection().setShapes(shapes);
		Cache.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(Shape shape, Settlement settlement){
		super();
		this.setProtection(new Protection(settlement.getProtection()));
		setSettlement(settlement);
		this.getProtection().add(shape);
		Cache.plotListChanged = true;
		setChanged(true);
	}
	
	@Override
	public void postLoad(){
		if(getSettlement() == null){
			this.setProtection(new Protection());
		} else {
			this.setProtection(new Protection(getSettlement().getProtection()));
		}
	}
	
	@Override
	public void initSettings(){
		setSettings(new PlotSettings());
	}
	
	@Override
	public PlotSettings getSettings(){
		return (PlotSettings) super.getSettings();
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
	
	/**
	 * Gets the file where this plot is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File dir = new File(Civilizations.getPlotDirectory(), getPlotType().toString());
		if(!dir.exists()){
			dir.mkdir();
		}
		File f = new File(dir, "" + getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	
	@Override
	public boolean save(){
		return false;
	}
	/**
	 * Gets the settlement owning this plot.
	 * @return Settlement
	 */
	public Settlement getSettlement() { return getSettings().getSettlement(); }
	/**
	 * Sets the settlement this plot belongs to.
	 * @param settlement
	 */
	public void setSettlement(Settlement settlement) {
		Settlement oldSettlement = getSettlement();
		if(oldSettlement != null){
			oldSettlement.removePlot(this);
		}
		getSettings().setSettlement(settlement);
		if(getSettlement() != null){
			getSettlement().addPlot(this);
		}
		setChanged(true);
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
	
	public static List<Shape> parseShapes(String str){
		String[] shapes = str.split(" ");
		List<Shape> list = new ArrayList<Shape>();
		for(String shape : shapes){
			Shape s = Shape.parse(shape);
			if(s != null){
				list.add(s);
			}
		}
		return list;
	}
	
	public String getShapesString(){
		StringBuilder sb = new StringBuilder();
		for(Shape s : getProtection().getShapes()){
			sb.append(s.toString() + " ");
		}
		return sb.toString();
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
}
