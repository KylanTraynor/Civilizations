package com.kylantraynor.civilizations.hook.towny;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Blueprint;
import com.kylantraynor.civilizations.builder.BuildProject;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.Warehouse;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.Permission;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.territories.Influence;
import com.kylantraynor.civilizations.territories.InfluentSite;
import com.kylantraynor.civilizations.territories.Region;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyTown extends Settlement implements InfluentSite, HasBuilder{
	
	static TownyTown get(String string) {
		for(TownyTown t : getTownyTownList()){
			if(t.getName().equalsIgnoreCase(string)){
				return t;
			}
		}
		return null;
	}

	Town townyTown;
	private boolean bypassPlotLoading = true;
	private Influence influence = new Influence();
	private Region region;
	private String lastNotification = "";
	private Instant lastNotificationInstant = Instant.now();
	static final int NOTIFICATION_SPAM_DELAY = 30;
	/**
	 * Gets the CacheManagerd list of Towns from Towny.
	 * @return List<TownyTown> of Towns.
	 */
	public static List<TownyTown> getTownyTownList(){
		return CacheManager.getTownyTownList();
	}

	public TownyTown(Location l) {
		super(l);
		CacheManager.townyTownListChanged = true;
	}
	
	@Override
	public void initSettings(){
		this.setSettings(new TownyTownSettings());
	}
	
	public TownyTown(Town t) throws TownyException{
		super(t.getSpawn());
		this.region = new Region(this);
		this.townyTown = t;
		if(getFile().exists())
			try {
				getSettings().load(getFile());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		if(getBuilder() == null){
			((TownyTownSettings) getSettings()).setBuilder(new Builder(this));
		}
		
		List<TownBlock> tl = t.getTownBlocks();
		importTownPermissions();
		int i = 0;
		for(TownBlock tb : tl){
			World w = Bukkit.getServer().getWorld(tb.getWorld().getName());
			if(w == null){
				throw new TownyException();
			} else {
				int x = tb.getX() * Coord.getCellSize();
				int z = tb.getZ() * Coord.getCellSize();
				Location l = new Location(w, x, 0, z);
				int width = Coord.getCellSize();
				int length = Coord.getCellSize();
				Shape s = new Prism(l, width, 255, length);
				if(isPlot(tb)){
					Plot p = new Plot(tb.getName(), s, this);
					
					Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
					Map<PermissionType, Boolean> allyPerm = new HashMap<PermissionType, Boolean>();
					Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
					
					if(tb.getPermissions().residentBuild != t.getPermissions().residentBuild) resPerm.put(PermissionType.PLACE, tb.getPermissions().residentBuild);
					if(tb.getPermissions().residentDestroy != t.getPermissions().residentDestroy) resPerm.put(PermissionType.BREAK, tb.getPermissions().residentDestroy);
					
					if(tb.getPermissions().allyBuild != t.getPermissions().allyBuild) allyPerm.put(PermissionType.PLACE, tb.getPermissions().allyBuild);
					if(tb.getPermissions().allyDestroy != t.getPermissions().allyDestroy) allyPerm.put(PermissionType.BREAK, tb.getPermissions().allyDestroy);
					
					if(tb.getPermissions().outsiderBuild != t.getPermissions().outsiderBuild) outsiderPerm.put(PermissionType.PLACE, tb.getPermissions().outsiderBuild);
					if(tb.getPermissions().outsiderDestroy != t.getPermissions().outsiderDestroy) outsiderPerm.put(PermissionType.BREAK, tb.getPermissions().outsiderDestroy);
					
					p.getProtection().setPermissions(new GroupTarget(p), new Permission(resPerm));
					p.getProtection().setPermissions(new GroupTarget(this), new Permission(allyPerm));
					p.getProtection().setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permission(outsiderPerm));
					this.addPlot(p);
					this.getProtection().add(s);
				} else {
					this.getProtection().add(s, false);
				}
			}
			i++;
			if(i % 50 == 0){
				Civilizations.log("INFO", "Loading " + t.getName() + ": " + Math.round(((double)i/tl.size())*100) + "% (" + i + "/"+ tl.size()+ ")");
			}
		}
		CacheManager.townyTownListChanged = true;
	}
	public TownyTown(Town t, UUID uuid) throws TownyException {
		this(t);
		this.getSettings().setUniqueId(uuid);
	}

	/**
	 * Checks if the given TownBlock is a plot or just part of the town's protection.
	 * @param tb
	 * @return true if the TownBlock is a plot, false otherwise.
	 */
	private boolean isPlot(TownBlock tb){
		tb.getPermissions().residentSwitch = true;
		tb.getPermissions().allySwitch = true;
		tb.getPermissions().outsiderSwitch = true;
		tb.getPermissions().residentItemUse = true;
		
		if(bypassPlotLoading ){
			return false;
		}
		if(tb.getPermissions().residentBuild != townyTown.getPermissions().residentBuild) return true;
		if(tb.getPermissions().residentDestroy != townyTown.getPermissions().residentDestroy) return true;
		if(tb.getPermissions().allyBuild != townyTown.getPermissions().allyBuild) return true;
		if(tb.getPermissions().allyDestroy != townyTown.getPermissions().allyDestroy) return true;
		if(tb.getPermissions().outsiderBuild != townyTown.getPermissions().outsiderBuild) return true;
		if(tb.getPermissions().outsiderDestroy != townyTown.getPermissions().outsiderDestroy) return true;
		if(!tb.getName().isEmpty()) return true;
		if(tb.isForSale()) return true;
		if(tb.hasResident()) return true;
		return false;
		
	}
	/**
	 * Imports permissions from Towny.
	 */
	private void importTownPermissions() {
		Map<PermissionType, Boolean> mayorPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> assistantPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> allyPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		
		
		mayorPerm.put(PermissionType.PLACE, true);
		mayorPerm.put(PermissionType.BREAK, true);
		mayorPerm.put(PermissionType.INVITE, true);
		mayorPerm.put(PermissionType.KICK, true);
		mayorPerm.put(PermissionType.CLAIM, true);
		mayorPerm.put(PermissionType.UNCLAIM, true);
		mayorPerm.put(PermissionType.UPGRADE, true);
		
		mayorPerm.put(PermissionType.MANAGE, true);
		mayorPerm.put(PermissionType.MANAGE_STALLS, true);
		
		
		assistantPerm.put(PermissionType.UPGRADE, false);
		
		resPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().residentBuild);
		resPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().residentDestroy);
		
		allyPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().allyBuild);
		allyPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().allyDestroy);
		
		outsiderPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().outsiderBuild);
		outsiderPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().outsiderDestroy);
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		Rank mayor = new Rank("Mayor", (Rank)null);
		mayor.addPlayer(TownyHook.getPlayer(townyTown.getMayor()));
		
		Rank assistant = new Rank("Assistant", mayor);
		for(Resident r : townyTown.getResidents()){
			for(String rank : r.getTownRanks()){
				if(rank.equalsIgnoreCase(assistant.getName())){
					assistant.addPlayer(TownyHook.getPlayer(r));
				}
			}
		}
		
		getProtection().setPermissions(mayor, new Permission(mayorPerm));
		getProtection().setPermissions(assistant, new Permission(assistantPerm));
		getProtection().setPermissions(new GroupTarget(this), new Permission(resPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.ALLIES), new Permission(allyPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permission(outsiderPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.SERVER), new Permission(serverPerm));
	}

	@Override
	public String getType(){
		return "Town";
	}

	@Override
	public String getIcon(){
		return "townhall";
	}
	
	@Override
	public String getName(){
		return this.townyTown.getName();
	}
	
	@Override
	public void update(){
		removeUnusedTownyPerms();
		if(this.getBuilder() != null){
			if(this.getBuilder().getSettings().hasChanged()) this.getBuilder().getSettings().save(); 
		}
		super.update();
	}
	
	@Override
	public boolean remove(){
		CacheManager.townyTownListChanged = true;
		return super.remove();
	}
	
	@Override
	public File getFile(){
		return new File(Civilizations.getTownyTownsDirectory(), this.getName());
	}
	
	@Override
	public List<UUID> getMembers(){
		List<UUID> list = new ArrayList<UUID>();
		for(Resident r : this.townyTown.getResidents()){
			OfflinePlayer p = TownyHook.getPlayer(r);
			if(p != null){
				list.add(p.getUniqueId());
			}
		}
		return list;
	}
	
	@Override
	public boolean addMember(OfflinePlayer p){
		return false;
	}
	
	@Override
	public boolean hasPermission(PermissionType perm, Block b, Player player){
		return true;
	}
	
	public void removeUnusedTownyPerms(){
		if(townyTown != null){
			
			if(townyTown.getPermissions().residentSwitch &&
					townyTown.getPermissions().allySwitch &&
					townyTown.getPermissions().outsiderSwitch &&
					townyTown.getPermissions().residentItemUse) return;
			
			townyTown.getPermissions().residentSwitch = true;
			townyTown.getPermissions().allySwitch = true;
			townyTown.getPermissions().outsiderSwitch = true;
			townyTown.getPermissions().residentItemUse = true;
			//townyTown.getPermissions().allyItemUse = true;
			//townyTown.getPermissions().outsiderItemUse = true;
			
			// Propagate perms to all unchanged, town owned, townblocks
			for (TownBlock townBlock : townyTown.getTownBlocks()) {
				if ((townyTown instanceof Town) && (!townBlock.hasResident())) {
					if (!townBlock.isChanged()) {
						townBlock.setType(townBlock.getType());
						TownyUniverse.getDataSource().saveTownBlock(townBlock);
					}
				}
			}
			
			Towny.plugin.resetCache();
		}
	}

	@Override
	public Influence getInfluence() {
		return this.influence;
	}

	@Override
	public float getX() {
		return (float) getLocation().getX();
	}

	@Override
	public float getZ() {
		return (float) getLocation().getZ();
	}
	
	@Override
	public Region getRegion(){
		return this.region;
	}

	@Override
	public Builder getBuilder() {
		return ((TownyTownSettings) getSettings()).getBuilder();
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
	 * Checks if the town is able to autobuild.
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
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nInventory: ").color(ChatColor.GRAY);
		if(getAmountOfWarehouses() > 0){
			fm.then("" + getTotalUsedWarehousesSpace() + "/" + getTotalWarehousesSpace()).color(ChatColor.GOLD);
		} else {
			fm.then("No Warehouses");
		}
		fm.then("\nBuild Projects: ").color(ChatColor.GRAY)
			.tooltip("Click here to see the list of projects.")
			.command("/group " + this.getId() + " Builder List");;
		fm.then("" + getBuilder().getProjects().size()).color(ChatColor.GOLD)
			.tooltip("Click here to see the list of projects.")
			.command("/group " + this.getId() + " Builder List");
		fm.then("\nMembers: ").color(ChatColor.GRAY)
			.tooltip("Click here to see the list of all members.")
			.command("/group " + this.getId() + " members");
		fm.then("" + getMembers().size()).color(ChatColor.GOLD)
			.tooltip("Click here to see the list of all members.")
			.command("/group " + this.getId() + " members");
		fm.then("\nActions: (You can click on the action you want to do)\n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}

	/**
	 * Sends a notification to all town members.
	 */
	@Override
	public void sendNotification(Level type, String message) {
		if(lastNotification.equalsIgnoreCase(message) && (lastNotificationInstant.isAfter(Instant.now().minusSeconds(NOTIFICATION_SPAM_DELAY)))) return;
		this.sendMessage(message, null);
		lastNotificationInstant = Instant.now();
		lastNotification = message;
	}
}
