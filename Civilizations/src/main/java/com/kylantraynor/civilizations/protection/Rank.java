package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class Rank extends PermissionTarget{
	private String name = "";
	private Rank parent;
	private List<UUID> players = new ArrayList<UUID>();
	
	public Rank(String name, Rank parent){
		super(TargetType.RANK);
		this.name = name;
		this.parent = parent;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Rank getParent(){ return parent; }
	public void setParent(Rank parent) { this.parent = parent; }
	public boolean includes(OfflinePlayer player){
		return players.contains(player.getUniqueId());
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
}