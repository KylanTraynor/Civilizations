package com.kylantraynor.civilizations;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.permissions.Permission;
import com.kylantraynor.civilizations.permissions.PermissionTarget;
import com.kylantraynor.civilizations.permissions.PermissionType;

public class Group {
	
	public static void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"Null", "INFO"};
		Integer id = null;
		try{
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException e){}
		Civilizations.currentInstance.log(Level.INFO, "Group ID: " + id);
		if(id != null && args.length >= 2){
			Group g = Group.get(id);
			if(g == null) return;
			Civilizations.currentInstance.log(Level.INFO, "Group: " + g.getName());
			switch(args[1].toUpperCase()){
			case "INFO":
				
				break;
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length > 2){
						g.getInteractiveMembersList(Integer.parseInt(args[2])).send(p);
					} else {
						g.getInteractiveMembersList().send(p);
					}
				} else {
					for(UUID i : g.getMembers()){
						sender.sendMessage(Bukkit.getServer().getOfflinePlayer(i).getName());
					}
				}
				break;
			}
		}
	}
	
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
	private String name;
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
	
	public String getName(){return name;}
	public void setName(String newName){name = newName;} 
	
	public ChatColor getChatColor(){return chatColor;}
	public void setChatColor(ChatColor newColor){chatColor = newColor;}
	
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public static Group get(int id){
		for(Group g : list){
			if(g.getId() == id) return g;
		}
		return null;
	}
	
	public Protection getProtection() {return protection;}
	public void setProtection(Protection protection) {this.protection = protection;}
	
	public List<UUID> getMembers() {return members;}
	public void setMembers(List<UUID> members) {this.members = members;}
	public boolean addMember(OfflinePlayer member){
		if(this.members.contains(member.getUniqueId())) return false;
		this.members.add(member.getUniqueId());
		return true;
	}
	public boolean removeMember(OfflinePlayer member){
		if(this.members.contains(member.getUniqueId())){
			this.members.remove(member.getUniqueId());
			return true;
		}
		return false;
	}
	public boolean isMember(Player p){return getMembers().contains(p.getUniqueId());}
	
	public boolean hasOneMemberOnline(){
		for(UUID i : getMembers()){
			OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(i);
			if(op.isOnline()){
				return true;
			}
		}
		return false;
	}
	
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
	
	public boolean remove() {
		File f = getFile();
		if(f != null){
			if(f.exists()) f.delete();
		}
		Cache.groupListChanged = true;
		return list.remove(this);
	}
	
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
	
	public static Group load(YamlConfiguration cf){
		if(cf == null) return null;
		Instant creation;
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.currentInstance.log(Level.WARNING, "Couldn't find creation date for a group. Replacing it by NOW.");
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
	
	public void update(){
		if(isChanged()) save();
	}
	
	public boolean isChanged() {
		return hasChanged;
	}

	public void setChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	
	public Instant getCreationDate() {return creationDate;}
	public String getCreationDateString(){return creationDate.toString();}
	public void setCreationDate(Instant creationDate) {this.creationDate = creationDate;}
	
	public FancyMessage getInteractiveMembersList(){
		return getInteractiveMembersList(1);
	}
	
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
	
	public void sendMessage(FancyMessage message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!hasPermission(permission, null, p)) continue;
			}
			message.send(p);
		}
	}
	
	public void sendMessage(String message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!hasPermission(permission, null, p)) continue;
			}
			p.sendMessage(message);;
		}
	}
	
	public boolean hasPermission(PermissionType perm, Block block, Player player) {
		boolean result = false;
		if(player != null){
			if(player.isOp()) return true;
			if(this.isMember(player)){
				// Do things for resident
				result = getProtection().getType(perm, PermissionTarget.MEMBERS, null);
			} else {
				// Do things for outsiders
				result = getProtection().getType(perm, PermissionTarget.OUTSIDERS, null);
			}
			Permission p = getProtection().getPermission(PermissionTarget.PLAYER, player.getUniqueId().toString());
			if(p != null && p.hasType(perm)){
				result = p.getTypes().get(perm);
			}
		} else {
			result = getProtection().getType(perm, PermissionTarget.SERVER, null);
		}
		return result;
	}
	public boolean hasRank(String targetId, Player o) {
		// TODO Auto-generated method stub
		return false;
	}
}
