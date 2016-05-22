package com.kylantraynor.civilizations.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VehiclesListener implements Listener{
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getRightClicked() != null){
			if(event.getRightClicked().getType() == EntityType.BOAT){
				double distance = event.getRightClicked().getLocation().distance(event.getPlayer().getLocation());
				if(distance < 1.5){
					
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
}
