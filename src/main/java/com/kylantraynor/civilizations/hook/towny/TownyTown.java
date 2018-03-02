package com.kylantraynor.civilizations.hook.towny;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import com.kylantraynor.civilizations.managers.GroupManager;
import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.TaxInfo;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.territories.Influence;
import com.kylantraynor.civilizations.territories.InfluentSite;
import com.kylantraynor.civilizations.territories.Region;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyTown extends Settlement implements InfluentSite, HasBuilder{
	
	static TownyTown get(String string) {
		for(TownyTown t : getAllTownyTowns()){
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
	private List<Shape> townyPlots = new ArrayList<Shape>();
	private boolean payTaxes;


	public TownyTown(Location l) {
		super(l);
	}
	
	@Override
	public void init(){
		super.init();
		setChatColor(ChatColor.GRAY);
	}
	
	@Override
	public void initSettings(){
		this.setSettings(new TownyTownSettings());
	}

	@Override
	public TownyTownSettings getSettings(){
	    return (TownyTownSettings) super.getSettings();
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
			addTownyPlot(tb);
			i++;
			if(i % 50 == 0){
				Civilizations.log("INFO", "Loading " + t.getName() + ": " + Math.round(((double)i/tl.size())*100) + "% (" + i + "/"+ tl.size()+ ")");
			}
		}
		hullNeedsUpdate = true;
	}
	public TownyTown(Town t, UUID uuid) throws TownyException {
		this(t);
		try {
		    this.getSettings().load(getFile());
        } catch (InvalidConfigurationException | IOException e){
		    e.printStackTrace();
		    this.getSettings().setUniqueId(uuid);
        }
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

	public Group getMayorGroup(){
	    UUID id = getSettings().getMayorGroupId();
	    if(id == null){
	        Group g = GroupManager.createGroup("Mayor", this);
	        getSettings().setMayorGroupId(g.getUniqueId());
	        getSettings().setPermissionLevel(g.getUniqueId(), 0);
	        return g;
        } else {
	        Group g = Group.get(id);
	        if(g == null){
                g = GroupManager.createGroup("Mayor", this);
                getSettings().setMayorGroupId(g.getUniqueId());
                getSettings().setPermissionLevel(g.getUniqueId(), 0);
            }
            return g;
        }
    }

	public Group getAssistantGroup(){
        UUID id = getSettings().getAssistantGroupId();
        if(id == null){
            Group g = GroupManager.createGroup("Assistant", this);
            getSettings().setAssistantGroupId(g.getUniqueId());
            getSettings().setPermissionLevel(g.getUniqueId(), 10);
            return g;
        } else {
            Group g = Group.get(id);
            if(g == null){
                g = GroupManager.createGroup("Assistant", this);
                getSettings().setAssistantGroupId(g.getUniqueId());
                getSettings().setPermissionLevel(g.getUniqueId(), 10);
            }
            return g;
        }
    }

	/**
	 * Imports permissions from Towny.
	 */
	private void importTownPermissions() {
	    UUID mayorId = getMayorGroup().getUniqueId();
	    UUID assistantId = getAssistantGroup().getUniqueId();

	    getSettings().setPermission(mayorId, PermissionType.PLACE.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.BREAK.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.INVITE.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.KICK.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.CLAIM.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.UNCLAIM.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.UPGRADE.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.MANAGE.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.MANAGE_STALLS.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.MANAGE_PLOTS.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.BUILD_BLUEPRINTS.toString(), true);
        getSettings().setPermission(mayorId, PermissionType.BLUEPRINT_NOTIFICATIONS.toString(), true);

        getSettings().setPermission(assistantId, PermissionType.PLACE.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.BREAK.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.INVITE.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.KICK.toString(), false);
        getSettings().setPermission(assistantId, PermissionType.CLAIM.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.UNCLAIM.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.UPGRADE.toString(), false);
        getSettings().setPermission(assistantId, PermissionType.MANAGE.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.MANAGE_STALLS.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.MANAGE_PLOTS.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.BUILD_BLUEPRINTS.toString(), true);
        getSettings().setPermission(assistantId, PermissionType.BLUEPRINT_NOTIFICATIONS.toString(), true);

        getSettings().setSelfPermission(PermissionType.PLACE.toString(), this.townyTown.getPermissions().residentBuild);
        getSettings().setSelfPermission(PermissionType.BREAK.toString(), this.townyTown.getPermissions().residentDestroy);

        getSettings().setOutsidersPermission(PermissionType.PLACE.toString(), this.townyTown.getPermissions().outsiderBuild);
        getSettings().setOutsidersPermission(PermissionType.BREAK.toString(), this.townyTown.getPermissions().outsiderDestroy);

        getSettings().setServerPermission(PermissionType.EXPLOSION.toString(), false);
        getSettings().setServerPermission(PermissionType.FIRE.toString(), true);
        getSettings().setServerPermission(PermissionType.FIRESPREAD.toString(), false);
        getSettings().setServerPermission(PermissionType.DEGRADATION.toString(), false);
        getSettings().setServerPermission(PermissionType.MOBSPAWNING.toString(), false);

		getMayorGroup().addMember(TownyHook.getPlayer(townyTown.getMayor()));

		getMayorGroup().clearMembers();
		for(Resident r : townyTown.getResidents()){
			for(String rank : r.getTownRanks()){
				if(rank.equalsIgnoreCase("co-mayor")){
					getMayorGroup().addMember(TownyHook.getPlayer(r));
				}
			}
		}

		getAssistantGroup().clearMembers();
		for(Resident r : townyTown.getResidents()){
			for(String rank : r.getTownRanks()){
				if(rank.equalsIgnoreCase("assistant")){
					getAssistantGroup().addMember(TownyHook.getPlayer(r));
				}
			}
		}

		this.payTaxes = townyTown.hasUpkeep();
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
		return super.remove();
	}
	
	@Override
	public File getFile(){
		File f = new File(Civilizations.getTownyTownsDirectory(), this.getName() + ".yml");
		if(!f.exists()){
		    try{
		        f.createNewFile();
            } catch (IOException e){
		        e.printStackTrace();
            }
        }
        return f;
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
	public boolean addMember(EconomicEntity p){
		return false;
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
		return getSettings().getBuilder();
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
			fm.then("\nBuild Projects: ").color(ChatColor.GRAY)
			.tooltip("Click here to see the list of projects.")
			.command("/group " + this.getUniqueId().toString() + " Builder List");;
		fm.then("" + getBuilder().getProjects().size()).color(ChatColor.GOLD)
			.tooltip("Click here to see the list of projects.")
			.command("/group " + this.getUniqueId().toString() + " Builder List");
		} else {
			fm.then("No Warehouses");
		}
		fm.then("\nMembers: ").color(ChatColor.GRAY)
			.tooltip("Click here to see the list of all members.")
			.command("/group " + this.getUniqueId().toString() + " members");
		fm.then("" + getMembers().size()).color(ChatColor.GOLD)
			.tooltip("Click here to see the list of all members.")
			.command("/group " + this.getUniqueId().toString() + " members");
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

	@Override
	public List<Shape> getShapes() {
		return townyPlots;
	}

	public void addTownyPlot(TownBlock tb) throws TownyException {
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
			/*if(isPlot(tb)){
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
				townyPlots.add(p);
				//this.getProtection().add(s);
			} else {
				townyPlots.add(new Plot(tb.getName(), s, this));
				//this.getProtection().add(s, false);
			}*/
			townyPlots.add(s);
		}
		hullNeedsUpdate = true;
	}

	public void removeTownyPlot(TownBlock tb) {
		int x = tb.getX() * Coord.getCellSize();
		int z = tb.getZ() * Coord.getCellSize();
		for(Shape s : townyPlots){
			if(s.isInside(x, 0, z)){
				townyPlots.remove(s);
				break;
			}
		}
		hullNeedsUpdate = true;
	}
	
	@Override
	public double getBalance(){
		try {
			return townyTown.getHoldingBalance();
		} catch (EconomyException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public void giveFunds(double amount){
		try {
			townyTown.collect(amount);
		} catch (EconomyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void takeFunds(double amount){
		try {
			townyTown.pay(amount, "");
		} catch (EconomyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public double calculateTax(TaxInfo taxInfo){
		if(this.payTaxes){
			return super.calculateTax(taxInfo);
		}
		return 0;
	}
	
	/**
	 * Gets the list of all the Towny towns registered on the server.
	 * @return {@link List} of {@link TownyTown TownyTowns} extracted from {@link Group#getList()}.
	 */
	public static List<TownyTown> getAllTownyTowns(){
		List<TownyTown> result = new ArrayList<TownyTown>();
		for(Group g : Group.getList()){
			if(g instanceof TownyTown){
				result.add((TownyTown) g);
			}
		}
		return result;
	}
}
