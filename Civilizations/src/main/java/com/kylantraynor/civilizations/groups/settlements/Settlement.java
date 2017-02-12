package com.kylantraynor.civilizations.groups.settlements;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.builder.Blueprint;
import com.kylantraynor.civilizations.builder.BuildProject;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.Budget;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.plots.House;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.Warehouse;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.protection.SettlementProtection;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.settings.SettlementSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.util.Util;

public class Settlement extends Group implements HasBuilder{
	
	private List<Plot> plots = new ArrayList<Plot>();
	private Location location;
	private Builder builder;
	
	@Override
	public String getType() {
		if(getHouses().size() >= 100 && getMarketStalls().size() >= 10){
			return "City";
		} else if(getHouses().size() >= 50 && getMarketStalls().size() >= 5){
			return "Large Town";
		} else if(getHouses().size() >= 25 && getMarketStalls().size() >= 5){
			return "Town";
		} else if(getHouses().size() >= 10 && getMarketStalls().size() >= 5){
			return "Small Town";
		} else if(getHouses().size() >= 50){
			return "Large Village";
		} else if(getHouses().size() >= 25){
			return "Village";
		} else if(getHouses().size() >= 10){
			return "Small Village";
		} else if(getHouses().size() >= 5){
			return "Hamlet";
		} else if(getHouses().size() == 1){
			return "Private Property";
		}
		return "Settlement";
	}
	
	/**
	 * Gets the icon to be displayed on the dynmap.
	 * @return String
	 */
	public String getIcon(){return null;}
	
	public Settlement() {
		super();
		CacheManager.settlementListChanged = true;
	}
	
	public Settlement(Location l){
		super();
		setLocation(l);
		CacheManager.settlementListChanged = true;
	}
	
	@Override
	public void init(){
		super.init();
		setChatColor(ChatColor.GRAY);
		super.setProtection(new SettlementProtection(this));
	}
	
	@Override
	public void initSettings(){
		setSettings(new SettlementSettings());
	}
	
	@Override
	public SettlementSettings getSettings() {
		return (SettlementSettings)super.getSettings();
	}
	
	@Override
	public SettlementProtection getProtection(){
		return (SettlementProtection)super.getProtection();
	}
	/**
	 * Gets the file where this camp is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getSettlementDirectory(), "" + this.getUniqueId().toString() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	
	/**
	 * Gets an interactive info panel adapted to the given player.
	 * @param player Context
	 * @return FancyMessage
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), this.getChatColor()))
			.then("\n" + getType() +" created ").color(ChatColor.GRAY)
			.then(Util.durationToString(getSettings().getCreationDate(), Instant.now())).color(ChatColor.GOLD)
			.then(" ago.").color(ChatColor.GRAY)
			.then("\nHouses: ").color(ChatColor.GRAY)
			.then("" + getHouses().size()).color(ChatColor.GOLD)
			.then("\nWarehouses: ").color(ChatColor.GRAY)
			.then("" + getWarehouses().size()).color(ChatColor.GOLD)
			.then("\nStalls: ").color(ChatColor.GRAY)
			.then("" + getMarketStalls().size()).color(ChatColor.GOLD)
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
		for(Plot p : plots){
			p.getProtection().setParent(this.getProtection());
		}
		getProtection().hullNeedsUpdate();
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
			p.getProtection().setParent(this.getProtection());
			this.plots.add(p);
			getProtection().hullNeedsUpdate();
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
			getProtection().hullNeedsUpdate();
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
	public Location getLocation() {return getSettings().getLocation();}
	/**
	 * Sets the location of this settlement.
	 * @param location
	 */
	public void setLocation(Location location) {
		getSettings().setLocation(location);
	}
	/**
	 * Gets the distance between the closest element of the settlement and the given location.
	 * @param location
	 * @return
	 */
	public double distance(Location location){
		return Math.sqrt(distanceSquared(location));
	}
	/**
	 * Gets the square of the distance between the closest element of the settlement and the given location.
	 * @param location
	 * @return
	 */
	public double distanceSquared(Location location){
		if(protects(location)) return 0.0;
		double distanceSquared = location.distanceSquared(getLocation());
		if(this.getProtection().getHull().exists()){
			distanceSquared = Math.min(this.getProtection().getHull().distance(location), distanceSquared);
		} else {
			for(Shape s : this.getProtection().getShapes()){
				distanceSquared = Math.min(s.distanceSquared(location), distanceSquared);
			}
		}
		/*for(Plot p : getPlots()){
			for(Shape s : p.getProtection().getShapes()){
				distanceSquared = Math.min(s.distanceSquared(location), distanceSquared);
			}
		}*/
		
		return distanceSquared;
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
		CacheManager.settlementListChanged = true;
		return super.remove();
	}
	/**
	 * Destroys this settlement, but leaves the plots behind.
	 * @return true if the settlement has been removed, false otherwise.
	 */
	public boolean softRemove(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			getProtection().hide(p);
		}
		CacheManager.settlementListChanged = true;
		return super.remove();
	}
	/**
	 * Gets the list of all the settlements.
	 * @return Returns the cached list.
	 * @see Cache
	 */
	public static List<Settlement> getSettlementList() {
		return CacheManager.getSettlementList();
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
		Double distanceSquared = null;
		Settlement closest = null;
		for(Settlement s : getSettlementList()){
			if(distanceSquared == null){
				closest = s;
			} else if(distanceSquared > s.distanceSquared(l)) {
				distanceSquared = s.distanceSquared(l);
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
	
	public double distance(Shape s){
		return Math.sqrt(distanceSquared(s));
	}
	
	public double distanceSquared(Shape s){
		double distanceSquared = s.getLocation().distanceSquared(this.getLocation());
		for(Shape shape : this.getProtection().getShapes()){
			distanceSquared = Math.min(shape.distanceSquared(s), distanceSquared);
		}
		for(Plot p : getPlots()){
			for(Shape shape : p.getProtection().getShapes()){
				distanceSquared = Math.min(shape.distanceSquared(s), distanceSquared);
			}
		}
		return distanceSquared;
	}
	
	/**
	 * Checks if the given shape is within the merge distance of this settlement.
	 * @param s
	 * @return
	 */
	public boolean canMergeWith(Shape s) {
		return distanceSquared(s) <= Civilizations.getSettings().getSettlementMergeDistanceSquared();
	}
	
	public int getAmountOfWarehouses(){
		int count = 0;
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				count++;
			}
		}
		return count;
	}
	
	public int getTotalWarehousesSpace(){
		int space = 0;
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				space += ((Warehouse) p).getSize();
			}
		}
		return space;
	}
	
	public int getTotalUsedWarehousesSpace(){
		int space = 0;
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				space += ((Warehouse) p).getUsedSize();
			}
		}
		return space;
	}
	
	public List<Warehouse> getWarehouses(){
		List<Warehouse> result = new ArrayList<Warehouse>();
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				result.add((Warehouse) p);
			}
		}
		return result;
	}
	
	public List<House> getHouses(){
		List<House> result = new ArrayList<House>();
		for(Plot p : getPlots()){
			if(p instanceof House){
				result.add((House) p);
			}
		}
		return result;
	}
	
	public List<MarketStall> getMarketStalls(){
		List<MarketStall> result = new ArrayList<MarketStall>();
		for(Plot p : getPlots()){
			if(p instanceof MarketStall){
				result.add((MarketStall) p);
			}
		}
		return result;
	}
	
	public int getTotalAvailableWarehousesSpace(){
		return getTotalWarehousesSpace() - getTotalUsedWarehousesSpace();
	}
	
	/**
	 * Taxes a transaction.
	 * @param taxType
	 * @param preTaxAmount
	 * @return postTax Amount
	 */
	public double taxTransaction(TaxType taxType, double preTaxAmount){
		double taxedAmount = getSettings().getTax(taxType) * preTaxAmount;
		Economy.depositSettlement(this, taxedAmount);
		return preTaxAmount - taxedAmount;
	}

	@Override
	public Builder getBuilder() {
		if(builder == null) builder = new Builder(this);
		return builder;
	}

	@Override
	public ItemStack getSupplies(Material material, short data) {
		if(!canBuild()) return null;
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				Warehouse wh = (Warehouse) p;
				HashMap<Integer, ? extends ItemStack> hm = wh.getInventory().all(material);
				if(hm.isEmpty()) continue;
				for(ItemStack is : hm.values()){
					if(is.getType() == material && is.getData().getData() == data && is.getAmount() >= 1){
						return is;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Adds the given build project to the list of autobuild projects.
	 */
	@Override
	public boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir) {
		if(!canBuild()) return false;
		BuildProject bp = new BuildProject(selection.getLocation(), cbp, true);
		return getBuilder().addProject(bp);
	}

	/**
	 * Checks if the settlement is able to autobuild.
	 */
	@Override
	public boolean canBuild() {
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				return true;
			}
		}
		return false;
	}

	/**
	 * Get supplies and remove it from the warehouses.
	 */
	@Override
	public ItemStack getSuppliesAndRemove(ItemStack supply) {
		if(!canBuild()) return null;
		Civilizations.DEBUG("Checking if warehouse contains " + supply.getData().getItemType().toString() + ":" + supply.getData().getData());
		for(Plot p : getPlots()){
			if(p instanceof Warehouse){
				Warehouse wh = (Warehouse) p;
				if(wh.containsAtLeast(supply, 1)){
					Civilizations.DEBUG("Found!");
					wh.removeItem(supply);
					return supply;
				}/*
				HashMap<Integer, ? extends ItemStack> hm = wh.getInventory().all(material);
				if(hm.isEmpty()) continue;
				for(ItemStack is : hm.values()){
					if(is.getType() == material && is.getData().getData() == data && is.getAmount() >= 1){
						ItemStack result = is.clone();
						result.setAmount(1);
						if(is.getAmount() == 1){
							wh.getInventory().remove(is);
						} else {
							is.setAmount(is.getAmount() - 1);
						}
						return result;
					}
				}
				*/
			}
		}
		Civilizations.DEBUG("Not Found!");
		return null;
	}

	@Override
	public void sendNotification(Level type, String message) {
		// TODO Auto-generated method stub
		
	}
}
