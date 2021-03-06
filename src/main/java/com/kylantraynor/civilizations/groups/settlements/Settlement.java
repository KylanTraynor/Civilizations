package com.kylantraynor.civilizations.groups.settlements;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.kylantraynor.civilizations.shapes.Hull;
import com.kylantraynor.civilizations.utils.Identifier;
import mkremins.fanciful.civilizations.FancyMessage;

import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Blueprint;
import com.kylantraynor.civilizations.builder.BuildProject;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.economy.TaxInfo;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.settings.SettlementSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.utils.MutableInteger;
import com.kylantraynor.civilizations.utils.Utils;

public class Settlement extends Group implements HasBuilder{
	
	private List<Plot> plots = new ArrayList<Plot>();
	private Builder builder;
	private Hull hull;
	protected boolean hullNeedsUpdate = true;
	
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
	}
	
	public Settlement(Location l){
		super();
		setLocation(l);
	}
	
	public Settlement(SettlementSettings settings){
		super(settings);
	}
	
	@Override
	public void init(){
		super.init();
		setChatColor(ChatColor.GRAY);
	}
	
	@Override
	public void initSettings(){
		setSettings(new SettlementSettings());
	}
	
	@Override
	public SettlementSettings getSettings() {
		return (SettlementSettings)super.getSettings();
	}

	/**
	 * Gets the file where this camp is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getSettlementDirectory(), "" + this.getIdentifier().toString() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}

    public boolean isInside(Location location){
        if(!getLocation().getWorld().equals(location.getWorld())) return false;
        if(getHull().exists()){
            return getHull().isInside(location);
        }
        return false;
    }

    public Hull getHull(){
        if(hull == null) hull = new Hull(getLocation());
        if(hullNeedsUpdate){
            hull.clear();
            for(Shape s : getShapes()){
                hull.addPoints(s.getVertices());
            }
            for(Plot p : getPlots()){
                for(Shape s : p.getShapes()){
                    hull.addPoints(s.getVertices());
                }
            }
            hull.updateHull();
            hullNeedsUpdate = false;
        }
        return hull;
    }

    public List<Shape> getShapes(){
	    return getSettings().getShapes();
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
			.then(Utils.durationToString(getSettings().getCreationDate(), Instant.now())).color(ChatColor.GOLD)
			.then(" ago.").color(ChatColor.GRAY)
			.then("\nHouses: ").color(ChatColor.GRAY)
			.then("" + getHouses().size()).color(ChatColor.GOLD)
			.then("\nWarehouses: ").color(ChatColor.GRAY)
			.then("" + getWarehouses().size()).color(ChatColor.GOLD)
			.then("/").color(ChatColor.GRAY)
			.then("" + (int) Math.floor(getHouses().size() / 10.0)).color(ChatColor.GOLD).tooltip("The number of wharehouses available increases every 10 Houses built.")
			.then("\nStalls: ").color(ChatColor.GRAY)
			.then("" + getMarketStalls().size()).color(ChatColor.GOLD)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getIdentifier().toString() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getIdentifier().toString() + " members")
			.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	/**
	 * Gets the list of plots of this settlement.
	 * @return List<Plot> of Plots.
	 */
	public List<Plot> getPlots() {
		if(plots == null){
			plots = new ArrayList<>();
		}
		return plots;
	}
	/**
	 * Sets the list of plots of this settlement.
	 * @param plts the new list of plots.
	 */
	public void setPlots(List<Plot> plts) {
	    Validate.notNull(plts, "New list of plots cannot be %s.", null);
		this.plots = plts;
		hullNeedsUpdate = true;
		setChanged(true);
	}
	/**
	 * Adds a plot to this settlement but does not update the plot itself.
	 * @param p
	 * @return true if the plot has been added, false otherwise.
	 */
	@Deprecated
	public boolean addPlot(Plot p){
	    Validate.notNull(p, "The added plot cannot be %s.", null);
		if(this.plots.contains(p)){
			return false;
		} else {
			this.plots.add(p);
			hullNeedsUpdate = true;
			setChanged(true);
			return true;
		}
	}
	/**
	 * Removes a plot from this settlement but does not update the plot itself.
	 * @param p
	 * @return true if the plot has been remove, false otherwise.
	 */
	@Deprecated
	public boolean removePlot(Plot p){
	    Validate.notNull(p, "The removed plot cannot be %s.", null);
		if(this.plots.contains(p)){
			this.plots.remove(p);
			hullNeedsUpdate = true;
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
        Validate.notNull(location, "Given location should not be %s.", null);
		return Math.sqrt(distanceSquared(location));
	}
	/**
	 * Gets the square of the distance between the closest element of the settlement and the given location.
	 * @param location
	 * @return
	 */
	public double distanceSquared(Location location){
	    Validate.notNull(location, "Given location should not be %s.", null);
        if(!getLocation().getWorld().equals(location.getWorld())) return Double.NaN;
		if(protects(location)) return 0.0;
		double distanceSquared = location.distanceSquared(getLocation());
		if(getHull().exists()){
			distanceSquared = Math.min(getHull().distance(location), distanceSquared);
		} else {
			for(Shape s : getShapes()){
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

    public Location getCenter(){
        World w = null;
        Double minX = null;
        Double minY = null;
        Double minZ = null;
        Double maxX = null;
        Double maxY = null;
        Double maxZ = null;
        for(Shape s : getShapes()){
            if(w == null){
                w = s.getWorld();
                minX = s.getMinX();
                minY = s.getMinY();
                minZ = s.getMinZ();
                maxX = s.getMaxX();
                maxY = s.getMaxY();
                maxZ = s.getMaxZ();
            } else {
                minX = Math.min(minX, s.getMinX());
                minY = Math.min(minY, s.getMinY());
                minZ = Math.min(minZ, s.getMinZ());
                maxX = Math.max(maxX, s.getMaxX());
                maxY = Math.max(maxY, s.getMaxY());
                maxZ = Math.max(maxZ, s.getMaxZ());
            }
        }
        if(w == null) throw new NullPointerException("World can't be null.");
        return new Location(w, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
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
		if(isChanged()) DynmapHook.updateMap(this);
		super.update();
	}
	/**
	 * Destroys this settlement.
	 * @return true if the settlement has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		for(Plot p : getPlots()){
			p.remove();
		}
		return super.remove();
	}
	/**
	 * Destroys this settlement, but leaves the plots behind.
	 * @return true if the settlement has been removed, false otherwise.
	 */
	public boolean softRemove(){
		return super.remove();
	}
	/**
	 * Gets the list of all the settlements.
	 * @return {@link List} of {@link Settlement Settlements} extracted from {@link Group#getList()}.
	 */
	public static List<Settlement> getAll() {
		List<Settlement> result = new ArrayList<Settlement>();
		for(Group g : Group.getList()){
			if(g instanceof Settlement){
				result.add((Settlement) g);
			}
		}
		return result;
	}
	/**
	 * Checks if the given location is under the protection of this settlement.
	 * @param l
	 * @return true if the location is protected, false otherwise.
	 */
	public boolean protects(Location l){
        Validate.notNull(l, "Given location should not be %s.", null);
		if(isInside(l)) return true;
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
        Validate.notNull(location, "Given location should not be %s.", null);
		for(Settlement s : getAll()){
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
        Validate.notNull(l, "Given location should not be %s.", null);
		for(Settlement s : getAll()){
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
        Validate.notNull(l, "Given location should not be %s.", null);
		Double distanceSquared = null;
		Settlement closest = null;
		for(Settlement s : getAll()){
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
	public Set<UUID> getMembers(){
		Set<UUID> list = new TreeSet<>();
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
        Validate.notNull(s, "Given shape should not be %s.", null);
		return Math.sqrt(distanceSquared(s));
	}
	
	public double distanceSquared(Shape s){
        Validate.notNull(s, "Given shape should not be %s.", null);
        if(!getLocation().getWorld().equals(s.getWorld())) return Double.NaN;
		double distanceSquared = s.getLocation().distanceSquared(this.getLocation());
		if(this.getHull().exists()){
			return distanceSquared = Math.min(this.getHull().distanceSquared(s), distanceSquared);
		}
		/*
		for(Shape shape : this.getProtection().getShapes()){
			distanceSquared = Math.min(shape.distanceSquared(s), distanceSquared);
		}
		*/
		for(Plot p : getPlots()){
			for(Shape shape : p.getShapes()){
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
        Validate.notNull(s, "Given shape should not be %s.", null);
		return distanceSquared(s) <= Civilizations.getSettings().getSettlementMergeDistanceSquared();
	}
	
	public int getAmountOfWarehouses(){
		int count = 0;
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
				count++;
			}
		}
		return count;
	}
	
	public int getTotalWarehousesSpace(){
		int space = 0;
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
				space += p.getSize();
			}
		}
		return space;
	}
	
	public int getTotalUsedWarehousesSpace(){
		int space = 0;
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
				space += p.getUsedSize();
			}
		}
		return space;
	}
	
	public Map<String, Integer> getPlotCounts(){
		Map<String, MutableInteger> temp = new HashMap<String, MutableInteger>();
		Map<String, Integer> result = new HashMap<String, Integer>();
		for(Plot p : getPlots()){
			String key = p.getType();
			MutableInteger count = temp.get(key);
			if(count != null){
				count.value++;
			} else {
				temp.put(key, new MutableInteger(1));
			}
		}
		for(Entry<String, MutableInteger> e : temp.entrySet()){
			result.put(e.getKey(), e.getValue().value);
		}
		return result;
	}
	
	public List<Plot> getWarehouses(){
		List<Plot> result = new ArrayList<Plot>();
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
				result.add(p);
			}
		}
		return result;
	}
	
	public List<Plot> getHouses(){
		List<Plot> result = new ArrayList<Plot>();
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.HOUSE){
				result.add(p);
			}
		}
		return result;
	}
	
	public List<Plot> getMarketStalls(){
		List<Plot> result = new ArrayList<Plot>();
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.MARKETSTALL){
				result.add(p);
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
	public ItemStack getSupplies(BlockData blockData) {
		if(!canBuild()) return null;
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
				int i =  p.getInventory().first(blockData.getMaterial());
				if(i == -1) continue;
				ItemStack is = p.getInventory().getItem(i);
				if(is.getAmount() >= 1){
					return is;
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
        Validate.notNull(selection, "Given selection should not be %s.", null);
        Validate.notNull(cbp, "Given blueprint should not be %s.", null);

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
			if(p.getPlotType() == PlotType.WAREHOUSE){
				return true;
			}
		}
		return false;
	}

	/**
	 * Get supplies and remove it from the warehouses.
	 */
	@Override
	public boolean removeSupplies(BlockData blockData) {
		if(!canBuild()) return false;
		Civilizations.DEBUG("Checking if warehouse contains " + blockData.getMaterial().toString());
		for(Plot p : getPlots()){
			if(p.getPlotType() == PlotType.WAREHOUSE){
			    for(ItemStack is : p.getInventory().getContents()){
			        if(is == null) continue;
			        if(Utils.isSameBlock(blockData, is.getType())){
			            if(is.getAmount() >= 1){
			                is.setAmount(is.getAmount() - 1);
                            Civilizations.DEBUG("Found!");
			                return true;
                        }
                    }
                }
				/*
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
		return false;
	}

	@Override
	public double calculateTax(TaxInfo taxInfo){
		switch(taxInfo.getBase()){
		case FromBalance:
			if(taxInfo.isPercent()){
				return this.getBalance() * (taxInfo.getValue() / 100.0);
			} else {
				return taxInfo.getValue();
			}
		case PerMember:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * getMembers().size();
			} else {
				return taxInfo.getValue() * getMembers().size();
			}
		case PerPlot:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * getPlots().size();
			} else {
				return taxInfo.getValue() * getPlots().size();
			}
		case PerArea:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * this.getHull().getArea();
			} else {
				return taxInfo.getValue() * getHull().getArea();
			}
		case PerVolume:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * this.getHull().getVolume();
			} else {
				return taxInfo.getValue() * getHull().getVolume();
			}
		default:
			break;
		}
		return 0;
	}
	
	@Override
	public void sendNotification(Level type, String message) {
		// TODO Auto-generated method stub
		
	}
}
