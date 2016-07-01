package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.MenuManager;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.settings.GroupSettings;

/**
 * A group contains members (players).
 * @author Baptiste
 *
 */
public class Group {
	
	private static ArrayList<Group> list = new ArrayList<Group>();
	public static ArrayList<Group> getList() {return list;}
	public static void setList(ArrayList<Group> list) {Group.list = list;}
	
	public static Stack<Integer> availableIds = new Stack<Integer>();
	
	public static void clearAll(){
		list.clear();
		list = null;
	}
	
	private int id;
	private List<UUID> members;
	private boolean hasChanged = true;
	private Protection protection;
	private ChatColor chatColor;
	private GroupSettings settings;
	
	public Group(){
		list.add(this);
		if(availableIds.size() > 0){
			this.setId(availableIds.pop());
		} else {
			this.setId(list.size() - 1);
		}
		initSettings();
		init();
		Cache.groupListChanged = true;
		setChanged(true);
	}
	
	public void init(){
		members = new ArrayList<UUID>();
		chatColor = ChatColor.WHITE;
		protection = new Protection();
		getSettings().setCreationDate(Instant.now());
	}
	
	public void initSettings(){
		setSettings(new GroupSettings());
	}
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + chatColor + getName() + ChatColor.GOLD + "] " + chatColor; 
	}
	
	/**
	 * Gets the group's name.
	 * @return String
	 */
	public String getName(){ return getSettings().getName(); }
	/**
	 * Sets the group's name.
	 * @param newName
	 */
	public void setName(String newName){ getSettings().setName(newName); } 
	
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
	 * Gets the ID of this group.
	 * @return Integer
	 */
	public int getId() {return id;}
	/**
	 * Sets the ID of this group.
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
	 * Gets the group with the given Unique ID.
	 * @param uid
	 * @return Group
	 */
	public static Group get(UUID uid){
		for(Group g : list){
			if(g.getSettings().getUniqueId().equals(uid)){
				return g;
			}
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
	public void setProtection(Protection protection) {
		this.protection = protection;
		setChanged(true);
	}
	/**
	 * Gets the list of all the members of this group.
	 * @return List<UUID> of the members
	 */
	public List<UUID> getMembers() {return this.getSettings().getMembers();}
	/**
	 * Sets the list of all the members of this group.
	 * @param members
	 */
	public void setMembers(List<UUID> members) { this.getSettings().setMembers(members); }
	/**
	 * Adds the given player to the list of members of this group.
	 * @param member
	 * @return true if the player wasn't already in the list, false otherwise.
	 */
	public boolean addMember(OfflinePlayer member){
		if(getMembers().contains(member.getUniqueId())) return false;
		List<UUID> members = getMembers();
		members.add(member.getUniqueId());
		setMembers(members);
		return true;
	}
	/**
	 * Removes the given player from the list of members of this group.
	 * @param member
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(OfflinePlayer member){
		if(getMembers().contains(member.getUniqueId())){
			List<UUID> members = getMembers();
			members.remove(member.getUniqueId());
			setMembers(members);
			return true;
		}
		return false;
	}
	/**
	 * Checks if the given player is a member of this group.
	 * @param player
	 * @return true if the player is a member, false otherwise.
	 */
	public boolean isMember(OfflinePlayer player){return getMembers().contains(player.getUniqueId());}
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
		availableIds.push(this.getId());
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
	 * Loads the data from the file into the given group.
	 * @param file
	 * @param group
	 * @return
	 */
	public static <T extends Group> T load(File file, T group){
		if(file == null) return null;
		try {
			group.getSettings().load(file);
			group.postLoad();
			return group;
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Do things right after the settings of the group are loaded.
	 */
	public void postLoad(){ }
	
	/**
	 * Saves the group to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(getProtection() != null){
			getSettings().saveProtection(getProtection());
		}
		getSettings().save(f);
		return !getSettings().hasChanged();
	}
	
	/**
	 * Updates the group.
	 */
	public void update(){
		if(isChanged() || getSettings().hasChanged()){
			try{save();} catch (Exception e) {e.printStackTrace();};
		}
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
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}

	/**
	 * Adds all the commands of this group to a clickable message.
	 * @param fm Message to add the commands to
	 * @param actions Actions to add
	 * @return Same as fm, with the added commands
	 */
	public FancyMessage addCommandsTo(FancyMessage fm, List<GroupAction> actions){
		if(actions.size() > 0){
			Iterator<GroupAction> it = actions.iterator();
			while(it.hasNext()){
				fm = it.next().addTo(fm);
				if(it.hasNext()){
					fm.then(" - ").color(ChatColor.GRAY);
				}
			}
		} else {
			fm.then("You can't do anything right now").color(ChatColor.GRAY);
		}
		
		return fm;
	}
	/**
	 * Gets all the commands available to this group.
	 * @param player
	 * @return
	 */
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		return list;
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
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("MEMBERS", null));
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
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	public FancyMessage getInteractiveRankMembers(Rank r, int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(r.getName().toUpperCase(), null));
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
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GOLD);
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
			p.sendMessage(getChatHeader() + getChatColor() + message);
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
		MenuManager.openMenu(new GroupMenu(this), player);
	}
	public String getType() {
		return "Group";
	}
	public boolean upgrade() {
		return false;
	}
	public GroupSettings getSettings() {
		return settings;
	}
	public void setSettings(GroupSettings settings) {
		this.settings = settings;
	}
}