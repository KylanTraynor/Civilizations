package com.kylantraynor.civilizations.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.managers.GroupManager;

public class CampCreateEvent extends GroupCreateEvent{

	private static final HandlerList handlers = new HandlerList();
	private String playerName;
	private String worldName;
	private double x;
	private double y;
	private double z;
	
	public CampCreateEvent(Player player, Location location){
		this.playerName = player.getName();
		this.worldName = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}
	
	public Player getPlayer(){
		return Bukkit.getServer().getPlayer(playerName);
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
