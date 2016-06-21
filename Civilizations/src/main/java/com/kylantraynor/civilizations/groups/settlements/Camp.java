package com.kylantraynor.civilizations.groups.settlements;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.ActionType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.GroupAction;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.fort.Keep;
import com.kylantraynor.civilizations.groups.settlements.towns.IsolatedDwelling;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.Permission;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.settings.CampSettings;
import com.kylantraynor.civilizations.settings.GroupSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shapes.Sphere;

public class Camp extends Settlement{
	
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + "CAMP" + ChatColor.GOLD + "] ";
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + getName() + ChatColor.GOLD + "] "; 
	}
	
	public Camp() { super(); }
	
	@Override
	public void postLoad(){
		this.getProtection().add(new Sphere(getLocation(), Camp.getSize()), false);
		this.setDefaultPermissions();
		Cache.campListChanged = true;
		super.postLoad();
	}
	
	public Camp(Location l) {
		super(l);
		this.getProtection().add(new Sphere(getLocation(), Camp.getSize()), false);
		this.setExpireOn(Instant.now());
		this.setDefaultPermissions();
		Cache.campListChanged = true;
	}

	@Override
	public void init(){
		setName("Camp");
		super.init();
		setChatColor(ChatColor.GREEN);
	}
	
	@Override
	public void initSettings(){
		setSettings(new CampSettings());
	}
	
	@Override
	public CampSettings getSettings() {
		return (CampSettings)super.getSettings();
	}
	
	
	@Override
	public boolean isUpgradable(){
		if(SmallOutpost.hasUpgradeRequirements(this)) return true;
		if(IsolatedDwelling.hasUpgradeRequirements(this)) return true;
		return false;
	}
	
	@Override
	public boolean upgrade(){
		if(SmallOutpost.hasUpgradeRequirements(this)){
			for(Plot p : getPlots()){
				if(p instanceof Keep){
					for(Shape s : p.getProtection().getShapes()){
						for(Location l : s.getBlockLocations()){
							if(l.getBlock().getType() == Material.BANNER || l.getBlock().getType() == Material.STANDING_BANNER){
								BlockState state = l.getBlock().getState();
								if(state instanceof org.bukkit.block.Banner){
									org.bukkit.block.Banner banner = (org.bukkit.block.Banner) state;
									Banner b = Banner.get(banner);
									House house = House.get(b);
									if(house == null){
										this.sendMessage("The Banner in the Keep doesn't belong to any house.", null);
										return false;
									}
									new SmallOutpost(this, house);
									return true;
								}
							}
						}
					}
					this.sendMessage("No Banner could be found in the keep.", null);
					return false;
				}
			}
			this.sendMessage("No keep could be found in the camp.", null);
			return false;
		} else if(IsolatedDwelling.hasUpgradeRequirements(this)){
			
			return false;
		} else {
			this.sendMessage("Nothing to upgrade to!", null);
			return false;
		}
	}
	
	@Override
	public String getType() {
		return "Camp";
	}

	@Override
	public String getIcon(){
		return "camp";
	}
	
	@Override
	public void update(){
		if(Instant.now().isAfter(getExpireOn())) remove();
		super.update();
	}
	
	@Override
	public boolean remove(){
		Cache.campListChanged = true;
		for(Plot p : getPlots()){
			p.remove();
		}
		return super.remove();
	}
	/**
	 * Sets the default Permissions for this camp.
	 */
	public void setDefaultPermissions() {
		Protection p = this.getProtection();
		Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		
		resPerm.put(PermissionType.MANAGE, true);
		resPerm.put(PermissionType.UPGRADE, true);
		resPerm.put(PermissionType.BREAK, true);
		resPerm.put(PermissionType.PLACE, true);
		resPerm.put(PermissionType.FIRE, true);
		resPerm.put(PermissionType.INVITE, true);
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		p.setPermissions(new GroupTarget(this), new Permission(resPerm));
		p.setPermissions(new PermissionTarget(TargetType.SERVER), new Permission(serverPerm));
	}
	/**
	 * Gets an interactive info panel adapted to the given player.
	 * @param player Context
	 * @return FancyMessage
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), ChatColor.GREEN))
			.then("\nProtection expires in ").color(ChatColor.GRAY)
			.then("" + ChronoUnit.HOURS.between(Instant.now(), getExpireOn()) + " hours").color(ChatColor.GOLD)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getId() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getId() + " members")
			.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		/*
		if(this.isMember(player)){
			fm.then("\nClear").color(ChatColor.GOLD).tooltip("Clear camp").command("/camp clear");
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Leave").color(ChatColor.GOLD).tooltip("Leave the camp").command("/camp leave");
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Rename").color(ChatColor.GOLD).tooltip("Rename the camp").suggest("/group " + this.getId() + " rename");
			fm.then(" - ").color(ChatColor.GRAY);
			if(ChronoUnit.HOURS.between(Instant.now(), this.getExpireOn()) > 22){
				fm.then("Renew").color(ChatColor.GRAY).tooltip("Wait for an hour before renewing");
			} else {
				fm.then("Renew").color(ChatColor.GOLD).tooltip("Keep the camp for one more day").command("/camp renew");
			}
			fm.then(" - ").color(ChatColor.GRAY);
			if(isUpgradable()){
				fm.then("Upgrade").color(ChatColor.GOLD).tooltip("Upgrade the camp").command("/group " + this.getId() + " upgrade");
			} else {
				fm.then("Upgrade").color(ChatColor.GRAY).tooltip("No upgrade available");
			}
		} else {
			fm.then("\nJoin");
			if(hasOneMemberOnline()){
				fm.color(ChatColor.GOLD).tooltip("Ask members of the camp if you can join.").command("/camp join");
			} else {
				fm.color(ChatColor.RED).tooltip("One member needs to be online to join the camp.");
			}
		}
		*/
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	} 
	
	@Override
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		list.add(new GroupAction("Clear", "Clear camp", ActionType.COMMAND, "/camp clear", this.hasPermission(PermissionType.MANAGE, null, player)));
		if(isMember(player)){
			list.add(new GroupAction("Leave", "Leaves this camp", ActionType.COMMAND, "/camp leave", true));
		} else {
			list.add(new GroupAction("Join", "Ask online members of this camp to join", ActionType.COMMAND, "/camp join", hasOneMemberOnline()));
		}
		list.add(new GroupAction("Rename", "Rename this camp", ActionType.SUGGEST, "/group " + this.getId() + " rename <NEW NAME>", this.hasPermission(PermissionType.MANAGE, null, player)));
		list.add(new GroupAction("Renew", "Renew the camp for 24 hours", ActionType.COMMAND, "/camp renew", this.hasPermission(PermissionType.MANAGE, null, player)));
		list.add(new GroupAction("Upgrade", "Upgrade the camp", ActionType.COMMAND, "/group " + this.getId() + " upgrade", this.hasPermission(PermissionType.UPGRADE, null, player) && isUpgradable()));
		return list;
	}
	/**
	 * Gets the camp at the given location.
	 * @param location
	 * @return Camp or null if no camp could be found.
	 */
	public static Camp getCampAt(Location location) {
		for(Camp c : getCampList()){
			if(c.protects(location)){
				return c;
			}
		}
		return null;
	}
	/**
	 * Gets the default radius of all camps.
	 * @return 8
	 */
	public static int getSize() {
		return 8;
	}
	/**
	 * Gets the date this camp will expire on.
	 * @return Instant
	 */
	public Instant getExpireOn() {
		return getSettings().getExpiryDate();
	}
	/**
	 * Sets the date this camp will expire on.
	 * @param expireOn
	 */
	public void setExpireOn(Instant expireOn) {
		getSettings().setExpiryDate(expireOn);
		setChanged(true);
	}
	/**
	 * Gets the cached list of camps.
	 * @return List<Camp> of camps.
	 * @see Cache
	 */
	public static List<Camp> getCampList(){
		return Cache.getCampList();
	}
	/**
	 * Gets the file where this camp is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getCampDirectory(), "" + this.getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	/*
	@Deprecated
	public static Camp load(YamlConfiguration cf){
		World w = Civilizations.currentInstance.getServer().getWorld(cf.getString("Location.World"));
		String name = cf.getString("Name", "Camp");
		double x = cf.getDouble("Location.X");
		double y = cf.getDouble("Location.Y");
		double z = cf.getDouble("Location.Z");
		Instant creation;
		Instant expireOn;
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		if(cf.getString("ExpireOn") != null){
			expireOn = Instant.parse(cf.getString("ExpireOn"));
		} else {
			expireOn = Instant.now().plus(1, ChronoUnit.DAYS);
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by 1 day from NOW.");
		}
		
		Camp c = new Camp(new Location(w, x, y, z));
		c.setName(name);
		c.getSettings().setCreationDate(creation);
		c.setExpireOn(expireOn);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			c.addMember(Bukkit.getServer().getOfflinePlayer(UUID.fromString((cf.getString("Members."+i)))));
			i+=1;
		}
		
		return c;
	}
	*/
	/*
	@Override
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		fc.set("Name", getName());
		fc.set("Location.World", getLocation().getWorld().getName());
		fc.set("Location.X", getLocation().getBlockX());
		fc.set("Location.Y", getLocation().getBlockY());
		fc.set("Location.Z", getLocation().getBlockZ());
		
		fc.set("Creation", getCreationDate().toString());
		fc.set("ExpireOn", getExpireOn().toString());
		
		int i = 0;
		for(UUID id : getMembers()){
			fc.set("Members." + i, id.toString());
			i += 1;
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	*/
	/**
	 * Gets the closest camp around the given location.
	 * @param l
	 * @return Camp or null if no camp could be found.
	 */
	public static Camp getClosest(Location l){
		Double distance = null;
		Camp closest = null;
		for(Camp s : getCampList()){
			if(distance == null){
				closest = s;
			} else if(distance > l.distance(s.getLocation())) {
				distance = l.distance(s.getLocation());
				closest = s;
			}
		}
		return closest;
	}
}