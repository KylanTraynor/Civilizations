package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.ActionType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.GroupAction;
import com.kylantraynor.civilizations.groups.Purchasable;
import com.kylantraynor.civilizations.groups.Rentable;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.protection.PermissionType;
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
		this.getProtection().add(shape);
		setSettlement(settlement);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(String name, List<Shape> shapes, Settlement settlement){
		super();
		this.setName(name);
		this.getProtection().setShapes(shapes);
		setSettlement(settlement);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	public Plot(Shape shape, Settlement settlement){
		super();
		this.setProtection(new Protection(settlement.getProtection()));
		this.getProtection().add(shape);
		setSettlement(settlement);
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
	
	public void update(){
		if(getSettlement() == null){
			Settlement s = Settlement.getClosest(getProtection().getCenter());
			if(s.canMergeWith(getProtection().getShapes().get(0))){
				setSettlement(s);
			}
			/*
			if(Settlement.getAt(getProtection().getCenter()) != null){
				setSettlement(Settlement.getAt(getProtection().getCenter()));
			}
			*/
		}
		super.update();
	}
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
			.then("\nPart of ").color(ChatColor.GRAY)
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
	
	@Override
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		list.add(new GroupAction("Rename", "Rename this plot", ActionType.SUGGEST, "/group " + this.getId() + " rename <NEW NAME>", this.hasPermission(PermissionType.MANAGE, null, player)));
		if(this instanceof Rentable){
			if(((Rentable)this).isOwner(player)){
				list.add(new GroupAction("For Rent", "Toggle the rentable state of this plot", ActionType.TOGGLE, "/group " + getId() + " toggleForRent", ((Rentable)this).isForRent()));
				list.add(new GroupAction("Kick", "Kick the player renting this plot", ActionType.COMMAND, "/group " + getId() + " kick", ((Rentable)this).getRenter() != null));
				list.add(new GroupAction("Rent Price", "Set the rent of this plot", ActionType.SUGGEST, "/group " + getId() + " setRent " + ((Rentable)this).getRent(), ((Rentable)this).isOwner(player)));
			} else if(((Rentable)this).isRenter(player)) {
				list.add(new GroupAction("Leave", "Stop renting this plot", ActionType.COMMAND, "/group " + getId() + " leave", true));
			} else {
				list.add(new GroupAction("Rent", "Start renting this plot", ActionType.COMMAND, "/group " + getId() + " rent", ((Rentable)this).isForRent()));
			}
		}
		if(this instanceof Purchasable){
			if(((Purchasable)this).isOwner(player)){
				list.add(new GroupAction("For Sale", "Toggle the for sale state of this plot", ActionType.TOGGLE, "/group " + getId() + " toggleForSale", ((Purchasable)this).isForSale()));
				list.add(new GroupAction("Purchase Price", "Set the purchase price of this plot", ActionType.SUGGEST, "/group " + getId() + " setPrice " + ((Purchasable)this).getPrice(), ((Purchasable)this).isOwner(player)));
			} else {
				list.add(new GroupAction("Purchase", "Buy this plot", ActionType.COMMAND, "/group " + getId() + " buy", ((Purchasable)this).isForSale()));
			}
		}
		list.add(new GroupAction("Remove", "Remove this plot", ActionType.COMMAND, "/group " + getId() + " remove", this.hasPermission(PermissionType.MANAGE, null, player)));
		
		return list;
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
