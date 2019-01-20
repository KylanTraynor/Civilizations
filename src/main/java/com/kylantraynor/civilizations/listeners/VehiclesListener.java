package com.kylantraynor.civilizations.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

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
				if(event.getFrom().getBlock().getType().isSolid() &&
						event.getTo().getBlock().getType().isSolid() &&
						event.getTo().add(0, 1, 0).getBlock().getType().isSolid()){
					event.getPlayer().getVehicle().eject();
					Boat boat = (Boat) event.getPlayer().getVehicle();
					ItemStack item;
					switch(boat.getWoodType()){
					case ACACIA: item = new ItemStack(Material.ACACIA_BOAT);
						break;
					case BIRCH: item = new ItemStack(Material.BIRCH_BOAT);
						break;
					case DARK_OAK: item = new ItemStack(Material.DARK_OAK_BOAT);
						break;
					case GENERIC: item = new ItemStack(Material.OAK_BOAT);
						break;
					case JUNGLE: item = new ItemStack(Material.JUNGLE_BOAT);
						break;
					case REDWOOD: item = new ItemStack(Material.SPRUCE_BOAT);
						break;
					default: item = new ItemStack(Material.OAK_BOAT);
						break;
					
					}
					event.getPlayer().getVehicle().remove();
					event.getPlayer().getWorld().dropItemNaturally(event.getFrom(), item);
				}
				if(event.getTo().getBlock().getType().isSolid()){
					event.setCancelled(true);
				}
			}
		}
	}
	
}
