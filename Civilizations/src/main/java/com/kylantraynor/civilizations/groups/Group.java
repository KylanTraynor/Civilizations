package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.protection.Permission;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.PlayerTarget;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;

public class Group {
	
	private static ArrayList<Group> list = new ArrayList<Group>();
	public static ArrayList<Group> getList() {return list;}
	public static void setList(ArrayList<Group> list) {Group.list = list;}
	
	public static void clearAll(){
		list.clear();
		list = null;
	}
	
	private int id;
	private List<UUID> members;
	private Instant creationDate;
	private boolean hasChanged;
	private String name = "Group";
	private Protection protection;
	private ChatColor chatColor;
	
	public Group(){
		list.add(this);
		this.setId(list.size() - 1);
		members = new ArrayList<UUID>();
		Cache.groupListChanged = true;
		chatColor = ChatColor.WHITE;
		protection = new Protection(this);
	}
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + getName() + "] "; 
	}
	/**
	 * Gets the group's name.
	 * @return String
	 */
	public String getName(){return name;}
	/**
	 * Sets the group's name.
	 * @param newName
	 */
	public void setName(String newName){name = newName;} 
	
	/**
	 * Gets the color of this group's chat.
	 * @return ChatColor
	 */
	public ChatColor getChatColor(){return chatColor;}
	/**
	 * Sets the color of this group's chat.
	 * @param newColor
	 */
	public void setChatColor(ChatColor newColor){chatColor = newColor;}
	/**
	 * Gets the unique ID of this group.
	 * @return Integer
	 */
	public int getId() {return id;}
	/**
	 * Sets the unique ID of this group.
	 * @param id
	 */
	public void setId(int id) {this.id = id;}
	/**
	 * Get the group with the given ID.
	 * @param id
	 * @return Group
	 */
	public static Group get(int id){
		for(Group g : list){
			if(g.getId() == id) return g;
		}
		return null;
	}
	/**
	 * Gets the protection of this group.
	 * @return¨Protection
	 */
	public Protection getProtection() {return protection;}
	/**
	 * Sets the protection of this group.
	 * @param protection
	 */
	public void setProtection(Protection protection) {this.protection = protection;}
	/**
	 * Gets the list of all the members of this group.
	 * @return List<UUID> of the members
	 */
	public List<UUID> getMembers() {return members;}
	/**
	 * Sets the list of all the members of this group.
	 * @param members
	 */
	public void setMembers(List<UUID> members) {this.members = members;}
	/**
	 * Adds the given player to the list of members of this group.
	 * @param member
	 * @return true if the player wasn't already in the list, false otherwise.
	 */
	public boolean addMember(OfflinePlayer member){
		if(this.members.contains(member.getUniqueId())) return false;
		this.members.add(member.getUniqueId());
		return true;
	}
	/**
	 * Removes the given player from the list of members of this group.
	 * @param member
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(OfflinePlayer member){
		if(this.members.contains(member.getUniqueId())){
			this.members.remove(member.getUniqueId());
			return true;
		}
		return false;
	}
	/**
	 * Checks if the given player is a member of this group.
	 * @param p
	 * @return true if the player is a member, false otherwise.
	 */
	public boolean isMember(Player p){return getMembers().contains(p.getUniqueId());}
	/**
	 * Checks if at least one players in this list of members of this group is online.
	 * @return true if at least one player is online, false otehrwise.
	 */
	public boolean hasOneMemberOnline(){
		for(UUID i : getMembers()){
			OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(i);
			if(op.isOnline()){
				return true;
			}
		}
		return false;
	}
	/**
	 * Gets a list of all the online players of this group.
	 * @return List<Player> of online members.
	 */
	public List<Player> getOnlinePlayers(){
		List<Player>  l = new ArrayList<Player>();
		for(UUID i : getMembers()){
			OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(i);
			if(op.isOnline()){
				l.add(op.getPlayer());
			}
		}
		return l;
	}
	/**
	 * Destroys this Group.
	 * @return true if the group has been removed, false otherwise.
	 */
	public boolean remove() {
		File f = getFile();
		if(f != null){
			if(f.exists()) f.delete();
		}
		Cache.groupListChanged = true;
		return list.remove(this);
	}
	/**
	 * Gets the File where this Group is saved.
	 * @return File
	 */
	public File getFile(){
		File f = new File(Civilizations.getGroupDirectory(), "" + this.getId() + ".yml");
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
	 * Loads group from its configuration file.
	 * @param cf
	 * @return Group
	 */
	public static Group load(YamlConfiguration cf){
		if(cf == null) return null;
		Instant creation;
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		
		Group g = new Group();
		g.setCreationDate(creation);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			g.getMembers().add(UUID.fromString((cf.getString("Members."+i))));
			i+=1;
		}
		
		return g;
	}
	/**
	 * Saves the group to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Creation", getCreationDate().toString());
		
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
	/**
	 * Updates the group.
	 */
	public void update(){
		if(isChanged()) save();
	}
	/**
	 * Checks if the group has changed.
	 * @return
	 */
	public boolean isChanged() {
		return hasChanged;
	}
	/**
	 * Sets if the groups has changed or not.
	 * @param hasChanged
	 */
	public void setChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	/**
	 * Gets the creation date of this group.
	 * @return Instant
	 */
	public Instant getCreationDate() {return creationDate;}
	/**
	 * Gets the creation date of this group as a string.
	 * @return String
	 */
	public String getCreationDateString(){return creationDate.toString();}
	/**
	 * Sets the creation date of this group.
	 * @param creationDate
	 */
	public void setCreationDate(Instant creationDate) {this.creationDate = creationDate;}
	/**
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage("========== " + getName().toUpperCase() + " ==========").color(ChatColor.GOLD);
		DateFormat format = new SimpleDateFormat("MMMM, dd of yyyy");
		if(creationDate != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(creationDate)).color(ChatColor.GOLD);
		}
		fm.then("Members: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
	/**
	 * Gets an interactive list of the members of this group.
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveMembersList(){
		return getInteractiveMembersList(1);
	}
	/**
	 * Gets an interactive list of the members of this group.
	 * @param page
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveMembersList(int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage("========== MEMBERS ==========").color(ChatColor.GOLD);
		for(int i = 8 * (page - 1); i < getMembers().size() && i < 8 * (page); i+=1){
			OfflinePlayer p = Civilizations.currentInstance.getServer().getOfflinePlayer(getMembers().get(i));
			fm.then("\n" + p.getName());
			if(p.isOnline()){
				fm.color(ChatColor.GREEN);
			} else {
				fm.color(ChatColor.GRAY);
			}
			fm.command("/p " + p.getName());
			Rank pr = getProtection().getRank(p);
			if(pr != null){
				fm.then(" (" + pr.getName() + ")");
				fm.color(ChatColor.GOLD);
				fm.command("/group " + this.getId() + " rank " + pr.getName() + " members");
			}
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getId() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getId() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
	public FancyMessage getInteractiveRankMembers(Rank r, int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage("========== " + r.getName().toUpperCase() + " ==========").color(ChatColor.GOLD);
		for(int i = 8 * (page - 1); i < r.getPlayers().size() && i < 8 * (page); i+=1){
			OfflinePlayer p = r.getPlayers().get(i);
			fm.then("\n" + p.getName());
			if(p.isOnline()){
				fm.color(ChatColor.GREEN);
			} else {
				fm.color(ChatColor.GRAY);
			}
			fm.command("/p " + p.getName());
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getId() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getId() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message
	 * @param permission
	 */
	public void sendMessage(FancyMessage message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!hasPermission(permission, null, p)) continue;
			}
			message.send(p);
		}
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message
	 * @param permission
	 */
	public void sendMessage(String message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!hasPermission(permission, null, p)) continue;
			}
			p.sendMessage(message);;
		}
	}
	/**
	 * Checks if the given player has a certain permission.
	 * @param perm
	 * @param block
	 * @param player
	 * @return
	 */
	public boolean hasPermission(PermissionType perm, Block block, Player player) {
		boolean result = false;
		if(player != null){
			return getProtection().hasPermission(player, perm);
		} else {
			result = getProtection().getPermission(perm, new PermissionTarget(TargetType.SERVER));
		}
		return result;
	}
	/**
	 * Checks if the given player has a certain rank.
	 * @param targetId of the rank.
	 * @param player
	 * @return
	 */
	public boolean hasRank(String targetId, Player player) {
		return getProtection().getRank(targetId).includes(player);
	}
	public void getInteractiveRankPanel(Rank playerRank) {
		
	}
	public void openMenu(Player player){
		new GroupMenu(this).open(player, GroupMenu.Page.MAIN);
	}
	public String getType() {
		return "Group";
	}
}