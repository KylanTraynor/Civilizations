package com.kylantraynor.civilizations.groups.settlements;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.shapes.Shape;

public class Settlement extends Group {
	
	private List<Plot> plots = new ArrayList<Plot>();
	private Location location;
	
	@Override
	public String getType() {
		return "Settlement";
	}
	/**
	 * Gets the icon to be displayed on the dynmap.
	 * @return String
	 */
	public String getIcon(){return null;}
	
	public Settlement(Location l){
		super();
		this.location = l;
		Cache.settlementListChanged = true;
	}
	
	@Override
	public void init(){
		super.init();
		setChatColor(ChatColor.GRAY);
	}
	/**
	 * Gets the list of plots of this settlement.
	 * @return List<Plot> of Plots.
	 */
	public List<Plot> getPlots() {return plots;}
	/**
	 * Sets the list of plots of this settlement.
	 * @param plts
	 */
	public void setPlots(List<Plot> plts) {
		this.plots = plts;
		setChanged(true);
	}
	/**
	 * Adds a plot to this settlement.
	 * @param p
	 * @return true if the plot has been added, false otherwise.
	 */
	public boolean addPlot(Plot p){
		if(this.plots.contains(p)){
			return false;
		} else {
			p.getProtection().setParent(this.getProtection());;
			this.plots.add(p);
			setChanged(true);
			return true;
		}
	}
	/**
	 * Removes a plot from this settlement.
	 * @param p
	 * @return true if the plot has been remove, false otherwise.
	 */
	public boolean removePlot(Plot p){
		if(this.plots.contains(p)){
			this.plots.remove(p);
			p.getProtection().setParent(null);
			setChanged(true);
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Gets the location of this settlement.
	 * @return Location
	 */
	public Location getLocation() {return location;}
	/**
	 * Sets the location of this settlement.
	 * @param location
	 */
	public void setLocation(Location location) {
		this.location = location;
		setChanged(true);
	}
	/**
	 * Gets the distance between the closest element of the settlement and the given location.
	 * @param location
	 * @return
	 */
	public double distance(Location location){
		if(protects(location)) return 0.0;
		double distance = location.distance(getLocation());
		for(Shape s : this.getProtection().getShapes()){
			distance = Math.min(s.distance(location), distance);
		}
		for(Plot p : getPlots()){
			for(Shape s : p.getProtection().getShapes()){
				distance = Math.min(s.distance(location), distance);
			}
		}
		
		return distance;
	}
	/**
	 * Checks if this settlement is upgradable.
	 * @return true if it can be upgraded, false otherwise.
	 */
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean upgrade(){
		return false;
	}
	
	static public boolean hasUpgradeRequirements(Settlement s){
		return false;
	}
	/**
	 * Updates this settlement.
	 */
	@Override
	public void update(){
		DynmapHook.updateMap(this);
		super.update();
	}
	/**
	 * Destroys this settlement.
	 * @return true if the settlement has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			getProtection().hide(p);
		}
		for(Plot p : getPlots()){
			p.remove();
		}
		Cache.settlementListChanged = true;
		return super.remove();
	}
	/**
	 * Gets the list of all the settlements.
	 * @return Returns the cached list.
	 * @see Cache
	 */
	public static List<Settlement> getSettlementList() {
		return Cache.getSettlementList();
	}
	/**
	 * Checks if the given location is under the protection of this settlement.
	 * @param l
	 * @return true if the location is protected, false otherwise.
	 */
	public boolean protects(Location l){
		if(getProtection().isInside(l)) return true;
		for(Plot p : getPlots()){
			if(p.protects(l)) return true;
		}
		return false;
	}
	/**
	 * Gets the Settlement at the given location
	 * @param location
	 * @return Settlement or null if no settlement could be found.
	 */
	public static Settlement getAt(Location location) {
		for(Settlement s : getSettlementList()){
			if(s.protects(location)) return s;
		}
		return null;
	}
	/**
	 * Checks if the given location is under the protection of any Settlement.
	 * @param l
	 * @return true if the location is protected, false otherwise.
	 */
	public static boolean isProtected(Location l){
		for(Settlement s : getSettlementList()){
			if(s.protects(l)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Gets the closest settlement from the given location.
	 * @param l
	 * @return Settlement or null if no settlement could be found.
	 */
	public static Settlement getClosest(Location l){
		Double distance = null;
		Settlement closest = null;
		for(Settlement s : getSettlementList()){
			if(distance == null){
				closest = s;
			} else if(distance > s.distance(l)) {
				distance = s.distance(l);
				closest = s;
			}
			if(s.protects(l)){
				closest = s;
				break;
			}
		}
		return closest;
	}
	/**
	 * Gets the members of this settlement.
	 * @return List<UUID>
	 */
	@Override
	public List<UUID> getMembers(){
		List<UUID> list = new ArrayList<UUID>();
		for(Plot p : getPlots()){
			for(UUID id : p.getMembers()){
				if(!list.contains(id)){
					list.add(id);
				}
			}
		}
		for(UUID id : super.getMembers()){
			if(!list.contains(id)){
				list.add(id);
			}
		}
		return list;
	}
	
	/**
	 * Gets the tax percent on stalls rent.
	 * @return double [0 - 1]
	 */
	public double getStallRentTax() {
		return 0.01;
	}
	/**
	 * Gets the tax percent on transactions.
	 * @return double [0 - 1]
	 */
	public double getTransactionTax() {
		return 0.01;
	}
	
	public boolean canMergeWith(Shape s) {
		double distance = s.getLocation().distance(this.getLocation());
		for(Plot p : getPlots()){
			for(Shape shape : p.getProtection().getShapes()){
				distance = Math.min(shape.distance(s), distance);
			}
		}
		return distance <= Civilizations.settlementMergeRadius;
	}
}
