package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.economy.TransactionResult;
import com.kylantraynor.civilizations.groups.ActionType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.GroupAction;
import com.kylantraynor.civilizations.groups.GroupInventory;
import com.kylantraynor.civilizations.groups.HasInventory;
import com.kylantraynor.civilizations.groups.Purchasable;
import com.kylantraynor.civilizations.groups.Rentable;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Permissions;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.shops.ShopManager;
import com.kylantraynor.civilizations.shops.ShopType;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.util.Util;

public class Plot extends Group implements Rentable, HasInventory {
	private boolean persistent = false;
	private int beds;
	private int workbenches;
	private List<Chest> chests;
	private List<String> waresStrings;
	
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
		//this.setProtection(new Protection(settlement.getProtection()));
		this.getProtection().add(shape);
		setSettlement(settlement);
		CacheManager.plotListChanged = true;
		setChanged(true);
	}
	
	@Override
	public void postLoad(){
		this.setProtection(new Protection());
		this.getProtection().setShapes(getSettings().getShapes());
		if(getSettlement() != null){
			this.getProtection().setParent(getSettlement().getProtection());
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
	 * Gets the Icon name.
	 * @return String
	 */
	public String getIcon(){
		switch(getPlotType()){
		case BANK:
			return "bank";
		case BLACKSMITH:
			return "hammer";
		case CONSTRUCTIONSITE:
			return "construction";
		case CROPFIELD:
			return "sign";
		case HOUSE:
			return "house";
		case KEEP:
			return "tower";
		case MARKETSTALL:
			return "scales";
		case ROAD:
			break;
		case SHOP:
			return "scales";
		case SHOPHOUSE:
			return "scales";
		case TOWNHALL:
			return "temple";
		case TOWNVAULT:
			break;
		case WAREHOUSE:
			return "bricks";
		case WOODCUTTER:
			break;
		default:
			break;
		
		}
		return "";
	}
	
	/**
	 * Gets the type of this plot.
	 * @return {@link PlotType}
	 */
	public PlotType getPlotType() {
		return getSettings().getPlotType();
	}
	
	/**
	 * Sets the type of this plot.
	 * @param type as {@link PlotType}
	 */
	public void setPlotType(PlotType type) {
		getSettings().setPlotType(type);
		setDefaultPermissions();
	}
	
	private void setDefaultPermissions() {
		//Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		//Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
		
		/*resPerm.put(PermissionType.MANAGE, true);
		resPerm.put(PermissionType.MANAGE_RANKS, true);
		resPerm.put(PermissionType.MANAGE_PLOTS, true);
		resPerm.put(PermissionType.UPGRADE, true);
		resPerm.put(PermissionType.BREAK, true);
		resPerm.put(PermissionType.PLACE, true);
		resPerm.put(PermissionType.FIRE, true);
		resPerm.put(PermissionType.INVITE, true);*/
		
		/*outsiderPerm.put(PermissionType.BREAK, false);
		outsiderPerm.put(PermissionType.PLACE, false);*/
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.ERODE, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		//p.setPermissions(new GroupTarget(this), new Permissions(resPerm));
		//p.setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permissions(outsiderPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.SERVER), new Permissions(serverPerm));
	}
	
	@Override
	public void update(){
		if(getRenter() != null){
			if(Instant.now().isAfter(getSettings().getNextPayment())){
				TransactionResult r = payRent();
				if(getRenter().isPlayer()){
					if(getRenter().getOfflinePlayer().isOnline()){
						getRenter().getOfflinePlayer().getPlayer().sendMessage(r.getInfo());
					}
				}
				setChanged(true);
			}
		}
		if(getSettlement() == null && getPlotType() != PlotType.CROPFIELD){
			Settlement s = Settlement.getClosest(getProtection().getCenter());
			if(s.canMergeWith(getProtection().getShapes().get(0))){
				setSettlement(s);
			}
		} else if(getSettlement() != null && getPlotType() == PlotType.CROPFIELD){
			getSettlement().removePlot(this);
		}
		if(!getIcon().isEmpty()){
			DynmapHook.updateMap(this);
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
		File f = new File(Civilizations.getPlotDirectory(getPlotType()), "" + getUniqueId().toString() + ".yml");
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
	public Settlement getSettlement() {
		if(getPlotType() == PlotType.CROPFIELD){
			Location center = this.getProtection().getCenter();
			InfluenceMap map = Civilizations.getInfluenceMap(center.getWorld());
			if(map == null) return null;
			return (Settlement) map.getInfluentSiteAt(center);
		} else {
			return getSettings().getSettlement();
		}
	}
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
			this.getProtection().setParent(getSettlement().getProtection());
		} else {
			this.getProtection().setParent(null);
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
			fm.command("/group " + getSettlement().getUniqueId().toString() + " info");
		}
		String renterCommand = getRenter() == null ? "" : (getRenter().isPlayer() ? "/p " + getRenter().getName() : "/group " + getRenter().getUniqueId().toString() + " info");
		String ownerCommand = getOwner() == null ? "" : (!getOwner().isPlayer() ? "/group " + getOwner().getUniqueId().toString() + " INFO" : "/p " + getOwner().getName());
		// Display Renter of the Plot.
		if(isForRent()){
			fm.then("\nRented by: ").color(ChatColor.GRAY).command(renterCommand)
			.then(getRenter() == null ? "Available" : getRenter().getName()).color(ChatColor.GOLD).command(renterCommand);
		}
		// Display Owner of the Plot.
		if(getOwner() == null){
			fm.then("\nNot owned by anyone.").color(ChatColor.GRAY);
		} else {
			Civilizations.currentInstance.getLogger().info("Name: " + getOwner().getName() + "; Command: " + ownerCommand);
			fm.then("\nOwned by: ").color(ChatColor.GRAY).command(ownerCommand);
			fm.then(getOwner().getName()).color(ChatColor.GOLD).command(ownerCommand);
		}
		if(isForRent()){
			fm.then("\nDaily rent: ").color(ChatColor.GRAY).then("" + getRent()).color(ChatColor.GOLD);
		}
		if(isRenter(player)){
			fm.then("\nNext Payment in ").color(ChatColor.GRAY).then("" + ChronoUnit.HOURS.between(Instant.now(), getSettings().getNextPayment()) + " hours").color(ChatColor.GOLD);
		}
		if(getPlotType() == PlotType.HOUSE){
			fm.then(".").color(ChatColor.GRAY)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getUniqueId().toString() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getUniqueId().toString() + " members")
			.then("/").color(ChatColor.GRAY)
			.then("" + getBedCount()).color(ChatColor.GOLD).tooltip("Beds under a roof.");
		} else {
			fm.then(".").color(ChatColor.GRAY)
				.then("\nMembers: ").color(ChatColor.GRAY)
				.command("/group " + this.getUniqueId().toString() + " members")
				.then("" + getMembers().size()).color(ChatColor.GOLD)
				.command("/group " + this.getUniqueId().toString() + " members");
		}
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	@Override
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		list.add(new GroupAction("Rename", "Rename this plot", ActionType.SUGGEST, "/group " + this.getUniqueId().toString() + " rename NEW NAME", this.hasPermission(PermissionType.MANAGE, null, player)));
		if(this instanceof Rentable){
			if(((Rentable)this).isOwner(player)){
				list.add(new GroupAction("For Rent", "Toggle the rentable state of this plot", ActionType.TOGGLE, "/group " + getUniqueId().toString() + " toggleForRent", ((Rentable)this).isForRent()));
				list.add(new GroupAction("Kick", "Kick the player renting this plot", ActionType.COMMAND, "/group " + getUniqueId().toString() + " kick", ((Rentable)this).getRenter() != null));
				list.add(new GroupAction("Rent Price", "Set the rent of this plot", ActionType.SUGGEST, "/group " + getUniqueId().toString() + " setRent " + ((Rentable)this).getRent(), ((Rentable)this).isOwner(player)));
			} else if(((Rentable)this).isRenter(player)) {
				list.add(new GroupAction("Leave", "Stop renting this plot", ActionType.COMMAND, "/group " + getUniqueId().toString() + " leave", true));
			} else {
				list.add(new GroupAction("Rent", "Start renting this plot", ActionType.COMMAND, "/group " + getUniqueId().toString() + " rent", ((Rentable)this).isForRent()));
			}
		}
		if(this instanceof Purchasable){
			if(((Purchasable)this).isOwner(player)){
				list.add(new GroupAction("For Sale", "Toggle the for sale state of this plot", ActionType.TOGGLE, "/group " + getUniqueId().toString() + " toggleForSale", ((Purchasable)this).isForSale()));
				list.add(new GroupAction("Purchase Price", "Set the purchase price of this plot", ActionType.SUGGEST, "/group " + getUniqueId().toString() + " setPrice " + ((Purchasable)this).getPrice(), ((Purchasable)this).isOwner(player)));
			} else {
				list.add(new GroupAction("Purchase", "Buy this plot", ActionType.COMMAND, "/group " + getUniqueId().toString() + " buy", ((Purchasable)this).isForSale()));
			}
		}
		list.add(new GroupAction("Remove", "Remove this plot", ActionType.COMMAND, "/group " + getUniqueId().toString() + " remove", ProtectionManager.hasPermission(this.getProtection(), PermissionType.MANAGE, player, false) || isOwner(player)));
		
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
	
	public boolean isChunkLoaded(){
		for(Shape s : this.getProtection().getShapes()){
			if(!s.getLocation().getWorld().isChunkLoaded(s.getLocation().getBlockX() >> 4, s.getLocation().getBlockZ() >> 4)) return false;
		}
		return true;
	}
	
	/*
	public List<Chest> getAllChests(){
		List<Chest> list = new ArrayList<Chest>();
		Location l = null;
		for(Shape s : getProtection().getShapes()){
			l = s.getLocation().clone();
			for(int x = s.getMinBlockX(); x <= s.getMaxBlockX(); x++){
				for(int y = s.getMinBlockY(); y <= s.getMaxBlockY(); y++){
					for(int z = s.getMinBlockZ(); z <= s.getMaxBlockZ(); z++){
						l.setX(x);
						l.setY(y);
						l.setZ(z);
						if(l.getBlock().getType() == Material.CHEST ||
								l.getBlock().getType() == Material.TRAPPED_CHEST){
							BlockState state = l.getBlock().getState();
							if(state instanceof Chest){
								list.add((Chest) state);
							}
						}
					}
				}
			}
		}
		return list;
	}
	*/
	
	@Override
	public GroupInventory getInventory() {
		List<Chest> list = getChests();
		GroupInventory inv = new GroupInventory(getSize());
		int j = 0;
		for(Chest c : list){
			for(int i = 0; i < c.getBlockInventory().getSize(); i++){
				inv.getContents()[j] = c.getBlockInventory().getContents()[i];
				j++;
			}
		}
		return inv;
	}
	
	public int getUsedSize(){
		List<Chest> list = getChests();
		int used = 0;
		for(Chest c : list){
			for(ItemStack is : c.getBlockInventory().getContents()){
				if(is == null) continue;
				used += is.getAmount() * (64 / is.getMaxStackSize());
			}
		}
		return used;
	}
	
	public int getSize(){
		List<Chest> list = getChests();
		int chestSize = 0;
		if(list.isEmpty()) return 0;
		chestSize = list.get(0).getBlockInventory().getSize();
		return chestSize * list.size() * 64;
	}

	@Override
	public void addItem(ItemStack... items) {
		List<Chest> list = getChests();
		for(Chest c : list){
			if(items.length == 0) return;
			HashMap<Integer, ItemStack> result = c.getBlockInventory().addItem(items);
			items = result.values().toArray(new ItemStack[result.size()]);
		}
	}

	@Override
	public void removeItem(ItemStack... items) {
		List<Chest> list = getChests();
		for(Chest c : list){
			if(items.length == 0) return;
			for(ItemStack is : c.getBlockInventory().getContents()){
				if(is == null) continue;
				for(ItemStack item : items){
					if(item == null) continue;
					if(item.getAmount() == 0) continue;
					if(Util.isSameBlock(item, is)){
						ItemStack temp = is.clone();
						temp.setAmount(item.getAmount());
						item.setAmount(Math.max(item.getAmount() - is.getAmount(), 0));
						c.getBlockInventory().removeItem(temp);
					}
				}
			}
			/*HashMap<Integer, ItemStack> result = c.getBlockInventory().removeItem(items);
			items = result.values().toArray(new ItemStack[result.size()]);*/
		}
	}
	
	@Override
	public boolean containsAtLeast(ItemStack item, int amount){
		/*if(chests == null) chests = getAllChests();
		for(Chest c : chests){
			if(c.getBlockInventory().containsAtLeast(item, amount)) return true;
		}
		return false;*/
		return getInventory().containsAtLeast(item, amount);
	}
	
	public int getBedCount(){
		if(beds >= 0) return beds;
		updateSpecialBlocks();
		return beds;
	}
	
	public int getWorkbenchesCount(){
		if(workbenches >= 0) return workbenches;
		updateSpecialBlocks();
		return workbenches;
	}
	
	public int getChestsCount(){
		return getChests().size();
	}
	
	public List<Chest> getChests(){
		if(chests == null) updateSpecialBlocks();
		return chests;
	}
	
	public void updateSpecialBlocks(){
		beds = 0;
		workbenches = 0;
		chests = new ArrayList<Chest>();
		for(Shape s : getProtection().getShapes()){
			for(Location l : s.getBlockLocations()){
				switch(l.getBlock().getType()){
				case BED_BLOCK:
					BlockState state = l.getBlock().getState();
					Bed bed = (Bed) state.getData();
					if(bed.isHeadOfBed() && l.getBlock().getRelative(BlockFace.UP).getLightFromSky() < 14){
						beds++;
					}
					break;
				case WORKBENCH:
					if(l.getBlock().getRelative(BlockFace.UP).getLightFromSky() < 14){
						workbenches++;
					}
					break;
				case CHEST: case TRAPPED_CHEST:
					BlockState chestState = l.getBlock().getState();
					if(chestState instanceof Chest && l.getBlock().getRelative(BlockFace.UP).getLightFromSky() < 14){
						chests.add((Chest)chestState);
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	public boolean isValid(){
		switch(getPlotType()){
		case HOUSE:
			return getBedCount() > 0 && getChestsCount() > 0 && getWorkbenchesCount() > 0;
		case WAREHOUSE:
			return getChestsCount() > 0 && getWorkbenchesCount() > 0;
		default:
			return true;
		}
	}

	@Override
	public EconomicEntity getOwner() {
		EconomicEntity ee = getSettings().getOwner();
		if(ee == null){
			//getSettings().setOwner(getSettlement());
			ee = getSettlement();
		}
		return ee;
	}

	@Override
	public boolean isOwner(OfflinePlayer player) {
		if(getOwner().isPlayer()){
			return getOwner().getUniqueId().equals(player.getUniqueId());
		} else {
			return ProtectionManager.hasPermission(((Group) getOwner()).getProtection(), PermissionType.MANAGE_PLOTS, player, false);
			//return ((Group) getOwner()).isMember(player);
		}
	}

	@Override
	public double getPrice() {
		return this.getSettings().getPrice();
	}

	@Override
	public void setPrice(double newPrice) {
		this.getSettings().setPrice(newPrice);
	}

	@Override
	public boolean isForSale() {
		return getSettings().isForSale();
	}

	@Override
	public void setForSale(boolean forSale) {
		getSettings().setForSale(forSale);
	}

	@Override
	public TransactionResult purchase(EconomicEntity ecoEntity) {
		TransactionResult result = new TransactionResult();
		if(!isForSale()){
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + "This plot is not for sale.";
			return result;
		}
		if(Economy.tryTransferFunds(ecoEntity, getOwner(), "Purchase of " + this.getName(), getPrice())){
			result.success = true;
			result.info = this.getChatHeader() + ChatColor.GREEN + "Successfully purchased " + this.getName() + "!";
			Economy.playCashinSound(getOwner());
			Economy.playPaySound(ecoEntity);
			return result;
		} else {
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + "You don't have enough money to purchase this plot.";
			return result;
		}
	}

	@Override
	public EconomicEntity getRenter() {
		return getSettings().getRenter();
	}

	@Override
	public boolean isRenter(OfflinePlayer player) {
		if(getRenter() == null) return false;
		if(getRenter().isPlayer()){
			return getRenter().getUniqueId().equals(player.getUniqueId());
		} else {
			return ((Group) getRenter()).isMember(player);
		}
	}

	@Override
	public double getRent() {
		return getSettings().getRent();
	}

	@Override
	public void setRent(double rent) {
		getSettings().setRent(rent);
	}

	@Override
	public boolean isForRent() {
		return getSettings().isForRent();
	}

	@Override
	public void setForRent(boolean forRent) {
		getSettings().setForRent(forRent);
	}

	@Override
	public TransactionResult rent(EconomicEntity ecoEntity) {
		TransactionResult result = new TransactionResult();
		if(!isForRent()){
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + this.getName() + " isn't for rent.";
			return result;
		}
		if(getRenter() != null){
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + this.getName() + " is already rented by someone.";
			return result;
		}
		this.setRenter(ecoEntity);
		if(this.payRent().wasSuccessful()){
			result.success = true;
			result.info = this.getChatHeader() + ChatColor.GREEN + "You are now renting " + this.getName() + ".";
			return result;
		} else {
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + "You can't afford to rent " + this.getName() + ".";
			this.getSettings().setRenter(null);
			return result;
		}
	}

	@Override
	public Instant getNextRentDate() {
		return getSettings().getNextPayment();
	}
	
	@Override
	public void setNextRentDate(Instant next) {
		getSettings().setNextPayment(next);
	}

	@Override
	public TransactionResult payRent() {
		TransactionResult result = new TransactionResult();
		if(getRenter() == null){
			result.success = false;
			return result;
		}
		if(getOwner() != null){
			if(Economy.tryTransferFunds(getRenter(), getOwner(), "Rent for " + getName(), getRent())){
				result.success = true;
				result.info = this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(getRent()) + " in rent.";
				Economy.playCashinSound(getOwner());
				Economy.playPaySound(getRenter());
				setNextRentDate(Instant.now().plus(1, ChronoUnit.DAYS));
				return result;
			} else {
				result.success = false;
				result.info = this.getChatHeader() + ChatColor.RED + "You couldn't pay the rent of " + Economy.format(getRent()) + "!";
				setNextRentDate(Instant.now().plus(1, ChronoUnit.HOURS));
				return result;
			}
		}
		return result;
	}
	
	public Map<ItemStack, Double> getWares(){
		try{
			Civilizations.DEBUG("Trying to find wares in stall.");
		Map<ItemStack, Double> wares = new HashMap<ItemStack, Double>();
		Location current = this.getProtection().getCenter().clone();
		for(Shape s : getProtection().getShapes()){
			for(int x = s.getMinBlockX(); x <= s.getMaxBlockX(); x++){
				for(int y = s.getMinBlockY(); y <= s.getMaxBlockY(); y++){
					for(int z = s.getMinBlockZ(); z <= s.getMaxBlockZ(); z++){
						current.setX(x);
						current.setY(y);
						current.setZ(z);
						if(current.getBlock().getType() == Material.CHEST || current.getBlock().getType() == Material.TRAPPED_CHEST){
							Shop shop = ShopManager.getShop(current);
							if(shop != null){
								double multiplier = 1.0;
								if(shop.getType() == ShopType.BUYING){
									multiplier = -1.0;
								}
								wares.put(shop.getItem(), multiplier * shop.getPrice());
							}
						}
					}
				}
			}
		}
		return wares;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<ItemStack, Double>();
		}
	}
	
	public List<String> getWaresToString(){
		if(isChunkLoaded() || waresStrings == null){
			List<String> result = new ArrayList<String>();
			for(Entry<ItemStack, Double> e : getWares().entrySet()){
				result.add((e.getValue() < 0 ? "Buying: " : "Selling: ") + getNameOf(e.getKey()) + " (for " + Economy.format(Math.abs(e.getValue())) + ")");
			}
			waresStrings = result;
		}
		return waresStrings;
	}
	
	private String getNameOf(ItemStack item) {
		if(item.getItemMeta().getDisplayName() != null){
			return item.getItemMeta().getDisplayName();
		} else {
			return Util.prettifyText(Util.getMaterialName(item));
		}
	}

	@Override
	public void setRenter(EconomicEntity entity) {
		getSettings().setRenter(entity);
	}

	public void setOwner(OfflinePlayer player) {
		setOwner(EconomicEntity.get(player.getUniqueId()));
	}
	
	public void setOwner(EconomicEntity entity){
		getSettings().setOwner(entity);
	}
}
