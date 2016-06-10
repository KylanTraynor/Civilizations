package com.kylantraynor.civilizations.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class VehiclesListener implements Listener{
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getRightClicked() != null){
			if(event.getRightClicked().getType() == EntityType.BOAT){
				double distance = event.getRightClicked().getLocation().distance(event.getPlayer().getLocation());
				if(distance < 1.5){
				} else {
					event.getPlayer().sendMessage("You're too far from that boat.");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.getPlayer().getVehicle() != null){
			if(event.getPlayer().getVehicle().getType() == EntityType.BOAT){
				if(event.getTo().getBlock().getType().isSolid()){
					event.setCancelled(true);
				}
			}
		}
	}
	
}
