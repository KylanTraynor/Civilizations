package com.kylantraynor.civilizations.events;

import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.kylantraynor.civilizations.groups.settlements.forts.Fort;

import java.util.UUID;

public class PlayerTerritoryChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private String playerName;
	private UUID oldFortId;
	private UUID newFortId;

	public PlayerTerritoryChangeEvent(Player player, Fort from, Fort to){
		this.playerName = player.getName();
		this.oldFortId = from != null ? from.getIdentifier() : null;
		this.newFortId = to != null ? to.getIdentifier() : null;
	}
	
	/**
	 * Gets the player that changed territories.
	 * @return
	 */
	public Player getPlayer(){
		return Bukkit.getServer().getPlayer(this.playerName);
	}
	
	/**
	 * Gets the fort that protects the previous location.
	 * @return
	 */
	public Fort getOldFort(){
		if(hasOldFort()){
			return (Fort) Fort.get(this.oldFortId);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the fort that protects the new location.
	 * @return
	 */
	public Fort getNewFort(){
		if(hasNewFort()){
			return (Fort) Fort.get(this.newFortId);
		} else {
			return null;
		}
	}
	
	/**
	 * Checks if the old location is protected by a fort.
	 * @return
	 */
	public boolean hasOldFort(){
		return oldFortId != null;
	}
	
	/**
	 * Checks if the new location is protected by a fort.
	 * @return
	 */
	public boolean hasNewFort(){
		return newFortId != null;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
