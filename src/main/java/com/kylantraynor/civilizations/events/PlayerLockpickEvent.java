package com.kylantraynor.civilizations.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLockpickEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String playerName;
	private String worldName;
	private int blockX;
	private int blockY;
	private int blockZ;
	public PlayerLockpickEvent(Player player, Block block){
		this.playerName = player.getName();
		this.worldName = block.getWorld().getName();
		this.blockX = block.getX();
		this.blockY = block.getY();
		this.blockZ = block.getZ();
	}
	
	public Player getPlayer(){
		return Bukkit.getServer().getPlayer(playerName);
	}
	
	public Block getLockpickedBlock(){
		return Bukkit.getWorld(worldName).getBlockAt(blockX, blockY, blockZ);
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
