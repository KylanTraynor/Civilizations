package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;

import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.*;
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
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.managers.SettlementManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.managers.ShopManager;
import com.kylantraynor.civilizations.shops.ShopType;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.utils.Utils;

public class Plot extends Group implements Rentable, HasInventory {
	private boolean persistent = false;
	private int beds;
	private int workbenches;
	private List<Chest> chests;
	private List<String> waresStrings;
	
	/**
	 * Reloads the plot from the given {@linkplain PlotSettings}.
	 * @param settings
	 */
	public Plot(PlotSettings settings){
		super(settings);
	}
	
	public Plot(String name, Shape shape, Settlement settlement){
		this(shape, settlement);
		this.setName(name);
		setChanged(true);
	}
	
	public Plot(String name, List<Shape> shapes, Settlement settlement){
		this(name, shapes);
        if(settlement != null) {
            getSettings().setSettlementId(settlement.getIdentifier());
        } else {
            getSettings().setSettlementId(null);
        }
		setChanged(true);
	}
	
	public Plot(Shape shape, Settlement settlement){
		this(shape);
		if(settlement != null) {
		    getSettings().setSettlementId(settlement.getIdentifier());
        } else {
		    getSettings().setSettlementId(null);
        }
		setChanged(true);
	}
	
	public Plot(String name, List<Shape> shapes){
		super();
		this.setName(name);
		getSettings().setShapes(shapes);
		setChanged(true);
	}
	
	public Plot(Shape shape){
		super();
		getSettings().addShape(shape);
		setChanged(true);
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
		case SMITHY:
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

	/**
	 * Gets the list of {@linkplain Shape} that defines the plot.
	 * @return List of {@link Shape}
	 */
	public List<Shape> getShapes(){
		return getSettings().getShapes();
	}

    public Group getOwnerGroup(){
        SimpleIdentifier id = getSettings().getOwnerGroupId();
        if(id == null){
            Group g = GroupManager.createGroup("Owner", this);
            getSettings().setOwnerGroupId(g.getIdentifier());
            getSettings().setPermissionLevel(g.getIdentifier(), 0);
            return g;
        } else {
            Group g = Group.get(id);
            if(g== null){
                g = GroupManager.createGroup("Owner", this);
                getSettings().setOwnerGroupId(g.getIdentifier());
                getSettings().setPermissionLevel(g.getIdentifier(), 0);
            }
            return g;
        }
    }

    public Group getRenterGroup(){
        SimpleIdentifier id = getSettings().getRenterGroupId();
        if(id == null){
            Group g = GroupManager.createGroup("Renter", this);
            getSettings().setRenterGroupId(g.getIdentifier());
            getSettings().setPermissionLevel(g.getIdentifier(), 10);
            return g;
        } else {
            Group g = Group.get(id);
            if(g==null){
                g = GroupManager.createGroup("Renter", this);
                getSettings().setRenterGroupId(g.getIdentifier());
                getSettings().setPermissionLevel(g.getIdentifier(), 10);
            }
            return g;
        }
    }
	
	private void setDefaultPermissions() {
	    SimpleIdentifier ownerId = getOwnerGroup().getIdentifier();
	    SimpleIdentifier renterId = getRenterGroup().getIdentifier();

	    getSettings().setPermission(ownerId, PermissionType.MANAGE.toString(), true);
        getSettings().setPermission(ownerId, PermissionType.BREAK.toString(), true);
        getSettings().setPermission(ownerId, PermissionType.PLACE.toString(), true);
        getSettings().setPermission(ownerId, PermissionType.INVITE.toString(), true);
        getSettings().setPermission(ownerId, PermissionType.KICK.toString(), true);

        getSettings().setPermission(renterId, PermissionType.MANAGE.toString(), false);
        getSettings().setPermission(renterId, PermissionType.BREAK.toString(), true);
        getSettings().setPermission(renterId, PermissionType.PLACE.toString(), true);
        getSettings().setPermission(renterId, PermissionType.INVITE.toString(), true);
        getSettings().setPermission(renterId, PermissionType.KICK.toString(), true);

        getSettings().setSelfPermission(PermissionType.MANAGE.toString(), false);
        getSettings().setSelfPermission(PermissionType.INVITE.toString(), false);
        getSettings().setSelfPermission(PermissionType.KICK.toString(), false);
		getSettings().setSelfPermission(PermissionType.PLACE.toString(), true);
		getSettings().setSelfPermission(PermissionType.BREAK.toString(), true);

		getSettings().setOutsidersPermission(PermissionType.BREAK.toString(), false);
		getSettings().setOutsidersPermission(PermissionType.PLACE.toString(), false);

		getSettings().setServerPermission(PermissionType.EXPLOSION.toString(), false);
        getSettings().setServerPermission(PermissionType.ERODE.toString(), false);
        getSettings().setServerPermission(PermissionType.FIRE.toString(), true);
        getSettings().setServerPermission(PermissionType.FIRESPREAD.toString(), false);
        getSettings().setServerPermission(PermissionType.DEGRADATION.toString(), false);
        getSettings().setServerPermission(PermissionType.MOBSPAWNING.toString(), false);
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
			Settlement s = Settlement.getClosest(getCenter());
			if(s.canMergeWith(getShapes().get(0))){
				SettlementManager.addPlot(s, this);
			}
		}
		if(!getIcon().isEmpty()){
			DynmapHook.updateMap(this);
		}
		super.update();
	}
	
	/**
	 * Gets the file where this plot is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getPlotDirectory(getPlotType()), "" + getIdentifier().toString() + ".yml");
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
			Location center = getCenter();
			InfluenceMap map = Civilizations.getInfluenceMap(center.getWorld());
			if(map == null) return null;
			return (Settlement) map.getInfluentSiteAt(center);
		} else {
			return (Settlement) Group.get(getSettings().getSettlementId());
		}
	}

    public boolean intersect(Shape s){
        if(!getCenter().getWorld().equals(s.getWorld())) return false;
        for(Shape s1 : getShapes()){
            if(s1.intersect(s)) return true;
        }
        return false;
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
	 * Sets the settlement this plot belongs to, but does not
	 * update the settlement's plots list.
	 * @deprecated Use {@linkplain SettlementManager#addPlotAndUpdate(Settlement, Plot)} instead.
	 * @param settlement
	 */
	public void setSettlement(Settlement settlement) {
		if(settlement != null){
			getSettings().setSettlementId(settlement.getIdentifier());
		} else {
			getSettings().setSettlementId(null);
		}
	}
	/**
	 * Gets an interactive info panel adapted to the given player.
	 * @param player Context
	 * @return {@link FancyMessage}
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), ChatColor.GREEN))
			.then("\nPart of ").color(ChatColor.GRAY)
			.then(getNameOf(getSettlement())).color(ChatColor.GOLD);
		if(getSettlement() != null){
			fm.command("/group " + getSettlement().getIdentifier().toString() + " info");
		}
		String renterCommand = getRenter() == null ? "" : (getRenter().isPlayer() ? "/p " + getRenter().getName() : "/group " + getRenter().getIdentifier().toString() + " info");
		String ownerCommand = getOwner() == null ? "" : (!getOwner().isPlayer() ? "/group " + getOwner().getIdentifier().toString() + " INFO" : "/p " + getOwner().getName());
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
			.command("/group " + this.getIdentifier().toString() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getIdentifier().toString() + " members")
			.then("/").color(ChatColor.GRAY)
			.then("" + getBedCount()).color(ChatColor.GOLD).tooltip("Beds under a roof.");
		} else {
			fm.then(".").color(ChatColor.GRAY)
				.then("\nMembers: ").color(ChatColor.GRAY)
				.command("/group " + this.getIdentifier().toString() + " members")
				.then("" + getMembers().size()).color(ChatColor.GOLD)
				.command("/group " + this.getIdentifier().toString() + " members");
		}
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	@Override
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		list.add(new GroupAction("Rename", "Rename this plot", ActionType.SUGGEST, "/group " + this.getIdentifier().toString() + " rename NEW NAME", this.hasPermission(PermissionType.MANAGE, null, player)));
		if(this instanceof Rentable){
			if(this.isOwner(player)){
				list.add(new GroupAction("For Rent", "Toggle the rentable state of this plot", ActionType.TOGGLE, "/group " + getIdentifier().toString() + " toggleForRent", ((Rentable)this).isForRent()));
				list.add(new GroupAction("Kick", "Kick the player renting this plot", ActionType.COMMAND, "/group " + getIdentifier().toString() + " kick", ((Rentable)this).getRenter() != null));
				list.add(new GroupAction("Rent Price", "Set the rent of this plot", ActionType.SUGGEST, "/group " + getIdentifier().toString() + " setRent " + ((Rentable)this).getRent(), this.isOwner(player)));
			} else if(this.isRenter(player)) {
				list.add(new GroupAction("Leave", "Stop renting this plot", ActionType.COMMAND, "/group " + getIdentifier().toString() + " leave", true));
			} else {
				list.add(new GroupAction("Rent", "Start renting this plot", ActionType.COMMAND, "/group " + getIdentifier().toString() + " rent", ((Rentable)this).isForRent()));
			}
		}
		if(this instanceof Purchasable){
			if(this.isOwner(player)){
				list.add(new GroupAction("For Sale", "Toggle the for sale state of this plot", ActionType.TOGGLE, "/group " + getIdentifier().toString() + " toggleForSale", ((Purchasable)this).isForSale()));
				list.add(new GroupAction("Purchase Price", "Set the purchase price of this plot", ActionType.SUGGEST, "/group " + getIdentifier().toString() + " setPrice " + ((Purchasable)this).getPrice(), this.isOwner(player)));
			} else {
				list.add(new GroupAction("Purchase", "Buy this plot", ActionType.COMMAND, "/group " + getIdentifier().toString() + " buy", ((Purchasable)this).isForSale()));
			}
		}
		list.add(new GroupAction("Remove", "Remove this plot", ActionType.COMMAND, "/group " + getIdentifier().toString() + " remove", ProtectionManager.hasPermission(PermissionType.MANAGE, this, player, false) || isOwner(player)));
		
		return list;
	}
	
	/**
	 * Checks if this {@linkplain Plot} protects the given {@linkplain Location}.
	 * @param location
	 * @return true if the location is protected, false otherwise.
	 */
	public boolean protects(Location location) {
		return isInside(location);
	}

    public boolean isInside(Location location){
        if(!getCenter().getWorld().equals(location.getWorld())) return false;
        for(Shape s : getShapes()){
            if(s.isInside(location)) return true;
        }
        return false;
    }
	/**
	 * Gets a list of all plots registered on the server.
	 * @return {@link List} of {@link Plot Plots} extracted from {@link Group#getList()}.
	 */
	public static List<Plot> getAll(){
		List<Plot> result = new ArrayList<Plot>();
		for(Group g : Group.getList()){
			if(g instanceof Plot){
				result.add((Plot) g);
			}
		}
		return result;
	}
	
	/**
	 * Gets the {@linkplain Plot} protecting the given {@linkplain Location} if it exists.
	 * @param location
	 * @return {@link Plot} or Null.
	 */
	public static Plot getAt(Location location){
		for(Group g : Group.getList()){
			if(g instanceof Plot){
				if(((Plot) g).protects(location)){
					return (Plot) g;
				}
			}
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
		for(Shape s : this.getSettings().getShapes()){
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
					if(Utils.isSameBlock(item, is)){
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
		for(Shape s : getShapes()){
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
		Group owners = getOwnerGroup();
		if(owners.getMembers().isEmpty()){
		    return getSettlement();
        } else {
		    return EconomicEntity.get(owners.getMembersArray()[0]);
        }
	}

	public boolean isOwner(OfflinePlayer player){
	    return getOwnerGroup().isMember(player, true) ||
                ProtectionManager.hasPermission(PermissionType.MANAGE_PLOTS, this, player, true);
    }

	@Override
	public boolean isOwner(EconomicEntity entity) {
        return getOwnerGroup().isMember(entity.getIdentifier(), true) ||
                ProtectionManager.hasPermission(PermissionType.MANAGE_PLOTS, this, entity, true);
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
			setOwner(ecoEntity);
			return result;
		} else {
			result.success = false;
			result.info = this.getChatHeader() + ChatColor.RED + "You don't have enough money to purchase this plot.";
			return result;
		}
	}

	@Override
	public EconomicEntity getRenter() {
	    Group renterGroup = getRenterGroup();
	    if(renterGroup.getMembers().isEmpty()) return null;
	    return EconomicEntity.getOrNull(renterGroup.getMembersArray()[0]);
	}

	public boolean isRenter(OfflinePlayer player){
	    Group renterGroup = getRenterGroup();
	    return renterGroup.isMember(player, true);
    }

	@Override
	public boolean isRenter(EconomicEntity entity) {
	    Group renterGroup = getRenterGroup();
		return renterGroup.isMember(entity.getIdentifier(), true);
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
		Group renterGroup = getRenterGroup();
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
			renterGroup.clearMembers();
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
		Location current = this.getCenter().clone();
		for(Shape s : getShapes()){
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
			return Utils.prettifyText(Utils.getMaterialName(item));
		}
	}

	@Override
	public void setRenter(EconomicEntity entity){
        Group g = getRenterGroup();
        g.clearMembers();
        g.addMember(entity);
	}

	public void setOwner(OfflinePlayer player){
	    setOwner(CivilizationsAccount.getEconomicEntity(player));
	}
	
	public void setOwner(EconomicEntity entity){
	    Group g = getOwnerGroup();
	    g.clearMembers();
	    g.addMember(entity);
	}
}
