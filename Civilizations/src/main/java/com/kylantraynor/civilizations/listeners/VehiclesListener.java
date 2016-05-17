package com.kylantraynor.civilizations.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class VehiclesListener implements Listener{
	
	@EventHandler
	public void onEntityMount(EntityMountEvent event){
		if(event.isCancelled()) return;
		if(event.getMount().getType() == EntityType.BOAT){
			double distance = event.getMount().getLocation().distance(event.getEntity().getLocation());
			if(distance < 1){
				
			} else {
				event.setCancelled(true);
			}
		}
	}
	
}
