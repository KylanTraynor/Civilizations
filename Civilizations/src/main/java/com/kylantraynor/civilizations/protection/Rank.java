package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Rank extends PermissionTarget{
	private String name = "";
	private String parent;
	private List<UUID> players = new ArrayList<UUID>();
	private int level = 1000;
	
	public Rank(String name, String string){
		super(TargetType.RANK);
		this.name = name;
		this.parent = string;
	}
	
	public Rank(String name, Rank parent){
		super(TargetType.RANK);
		this.name = name;
		if(parent != null){
			this.parent = parent.getName();
		}
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public int getLevel() { return level; }
	public void setLevel(int level) { this.level  = level; }
	
	public String getParent(){ return parent; }
	public void setParent(Rank parent) { this.parent = parent.getName(); }
	
	public boolean includes(OfflinePlayer player){
		return players.contains(player.getUniqueId());
	}
	
	public void addId(UUID id){
		if(!players.contains(id)){
			players.add(id);
		}
	}
	
	public void addPlayer(OfflinePlayer player){
		if(players.contains(player.getUniqueId())){
			
		} else {
			players.add(player.getUniqueId());
		}
	}
	
	public void removePlayer(OfflinePlayer player){
		if(players.contains(player.getUniqueId())){
			players.remove(player.getUniqueId());
		}
	}

	public List<OfflinePlayer> getPlayers() {
		List<OfflinePlayer> list = new ArrayList<OfflinePlayer>();
		for(UUID id : players){
			list.add(Bukkit.getServer().getOfflinePlayer(id));
		}
		return list;
	}
	
	public List<UUID> getUniqueIds(){
		return players;
	}
	
	@Override
	public boolean equals(Object pt){
		if(!(pt instanceof Rank)) return false;
		if(((Rank) pt).getName().equals(this.getName())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int base = 11;
		return base * name.hashCode();
	}
}