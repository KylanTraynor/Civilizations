package com.kylantraynor.civilizations.groups.settlements;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.TaxInfo;
import com.kylantraynor.civilizations.groups.ActionType;
import com.kylantraynor.civilizations.groups.GroupAction;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Permissions;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.settings.CampSettings;
import com.kylantraynor.civilizations.shapes.Sphere;
import com.kylantraynor.civilizations.util.Util;

public class Camp extends Settlement{
	
	public static int campDuration = 48;
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + "CAMP" + ChatColor.GOLD + "] ";
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + ChatColor.GREEN + ChatColor.BOLD + getName() + ChatColor.GOLD + "] "; 
	}
	
	public Camp() {
		super();
		CacheManager.campListChanged = true;
	}
	
	public Camp(CampSettings settings){
		super(settings);
		this.getProtection().add(new Sphere(getLocation(), Camp.getSize()), false);
		this.setDefaultPermissions();
	}
	
	@Override
	public void postLoad(){
		this.getProtection().add(new Sphere(getLocation(), Camp.getSize()), false);
		this.setDefaultPermissions();
		CacheManager.campListChanged = true;
		super.postLoad();
	}
	
	public Camp(Location l) {
		super(l);
		this.getProtection().add(new Sphere(getLocation(), Camp.getSize()), false);
		this.setExpireOn(Instant.now().plus(campDuration, ChronoUnit.HOURS));
		this.setDefaultPermissions();
		CacheManager.campListChanged = true;
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
		return false;
	}
	
	@Override
	public boolean upgrade(){/*
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
		*/
		return false;
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
		if(this.getHouses().size() >= 1){
			for(Plot h : getHouses()){
				if(h.isValid()){
					if(GroupManager.convertToSettlement(this) != null){
						this.softRemove();
						CacheManager.campListChanged = true;
					}
					return;
				}
			}
		}
	}
	
	@Override
	public boolean remove(){
		CacheManager.campListChanged = true;
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
		Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
		
		resPerm.put(PermissionType.MANAGE, true);
		resPerm.put(PermissionType.MANAGE_RANKS, true);
		resPerm.put(PermissionType.MANAGE_PLOTS, true);
		resPerm.put(PermissionType.UPGRADE, true);
		resPerm.put(PermissionType.BREAK, true);
		resPerm.put(PermissionType.PLACE, true);
		resPerm.put(PermissionType.FIRE, true);
		resPerm.put(PermissionType.INVITE, true);
		
		outsiderPerm.put(PermissionType.BREAK, false);
		outsiderPerm.put(PermissionType.PLACE, false);
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		p.setPermissions(new GroupTarget(this), new Permissions(resPerm));
		p.setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permissions(outsiderPerm));
		p.setPermissions(new PermissionTarget(TargetType.SERVER), new Permissions(serverPerm));
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
			.then(Util.durationToString(Instant.now(), getSettings().getExpiryDate())).color(ChatColor.GOLD)
			.then(".").color(ChatColor.GRAY)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getUniqueId().toString() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getUniqueId().toString() + " members")
			.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
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
			if(getMembers().size() > 0){
				list.add(new GroupAction("Join", "Ask online members of this camp to join", ActionType.COMMAND, "/camp join", hasOneMemberOnline()));
			} else {
				list.add(new GroupAction("Claim", "Claim this abandonned camp", ActionType.COMMAND, "/camp join", true));
			}
		}
		list.add(new GroupAction("Rename", "Rename this camp", ActionType.SUGGEST, "/group " + this.getUniqueId().toString() + " rename <NEW NAME>", this.hasPermission(PermissionType.MANAGE, null, player)));
		list.add(new GroupAction("Renew", "Renew the camp for " + campDuration + " hours", ActionType.COMMAND, "/camp renew", this.hasPermission(PermissionType.MANAGE, null, player)));
		list.add(new GroupAction("Upgrade", "Upgrade the camp", ActionType.COMMAND, "/group " + this.getUniqueId().toString() + " upgrade", this.hasPermission(PermissionType.UPGRADE, null, player) && isUpgradable()));
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
	}
	/**
	 * Gets the CacheManagerd list of camps.
	 * @return List<Camp> of camps.
	 * @see CacheManager
	 */
	public static List<Camp> getCampList(){
		return CacheManager.getCampList();
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
	/**
	 * Gets the closest camp around the given location.
	 * @param l
	 * @return Camp or null if no camp could be found.
	 */
	public static Camp getClosest(Location l){
		Double distanceSquared = null;
		Camp closest = null;
		for(Camp s : getCampList()){
			if(distanceSquared == null){
				closest = s;
			} else if(distanceSquared > l.distanceSquared(s.getLocation())) {
				distanceSquared = l.distanceSquared(s.getLocation());
				closest = s;
			}
		}
		return closest;
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
			return 0;
		case PerArea:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * getSize() * getSize() * Math.PI;
			} else {
				return taxInfo.getValue() * (getSize() * getSize()) * Math.PI;
			}
		case PerVolume:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * (getSize() * getSize() * getSize() * ((4.0/3.0) * Math.PI));
			} else {
				return taxInfo.getValue() * (getSize() * getSize() * getSize() * ((4.0/3.0) * Math.PI));
			}
		default:
			break;
		}
		return 0;
	}
}