package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.shapes.Shape;

public class Plot extends Group {
	private PlotType type;
	private boolean persistent = false;
	
	//Constructor for reloads from file
	public Plot(){
		super();
		CacheManager.plotListChanged = true;
	}
	
	public Plot(String name, Shape shape, Settlement settlement){
		super();
		this.setName(name);
		setSettlement(settlement);
		this.getProtection().add(shape);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(String name, List<Shape> shapes, Settlement settlement){
		super();
		this.setName(name);
		setSettlement(settlement);
		this.getProtection().setShapes(shapes);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(Shape shape, Settlement settlement){
		super();
		this.setProtection(new Protection(settlement.getProtection()));
		setSettlement(settlement);
		this.getProtection().add(shape);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	@Override
	public void postLoad(){
		if(getSettlement() == null){
			this.setProtection(new Protection());
		} else {
			this.setProtection(new Protection(getSettlement().getProtection()));
		}
		this.getProtection().setShapes(getSettings().getShapes());
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
		CacheManager.plotListChanged = true;
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
		if(isPersistent()){
			return super.save();
		} else {
			return false;
		}
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
	 * Gets an interactive info panel adapted to the given player.
	 * @param player Context
	 * @return FancyMessage
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), ChatColor.GREEN))
			.then("\nBelongs to ").color(ChatColor.GRAY)
			.then(getNameOf(getSettlement())).color(ChatColor.GOLD);
		if(getSettlement() != null){
			fm.command("/group " + getSettlement().getId() + " info");
		}
		fm.then(".").color(ChatColor.GRAY)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getId() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getId() + " members")
			.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
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
		return CacheManager.getPlotList();
	}
	
	public static Plot getAt(Location location){
		for(Plot p : getAll()){
			if(p.protects(location)) return p;
		}
		return null;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
}
