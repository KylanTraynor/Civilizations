package com.kylantraynor.civilizations.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;

public class ProtectionListener implements Listener{
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer() == null) return;
		if(Settlement.isProtected(event.getBlock().getLocation())){
			
			// Checks if the protection belongs to a Market Stall.
			for(MarketStall ms : Cache.getMarketstallList()){
				if(ms.protects(event.getBlock().getLocation())){
					if(ms.isOwner(event.getPlayer())){
						event.setCancelled(false);
					} else if(event.getPlayer().equals(ms.getRenter())){
						if(event.getBlock().getType() == Material.CHEST ||
							event.getBlock().getType() == Material.TRAPPED_CHEST ||
							event.getBlock().getType() == Material.SIGN ||
							event.getBlock().getType() == Material.SIGN_POST){
							event.setCancelled(false);
						} else {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer() == null) return;
		if(Settlement.isProtected(event.getBlock().getLocation())){
			
			// Checks if the protection belongs to a Market Stall.
			for(MarketStall ms : Cache.getMarketstallList()){
				if(ms.protects(event.getBlock().getLocation())){
					if(ms.isOwner(event.getPlayer())){
						event.setCancelled(false);
					} else if(event.getPlayer().equals(ms.getRenter())){
						if(event.getBlock().getType() == Material.CHEST ||
							event.getBlock().getType() == Material.TRAPPED_CHEST ||
							event.getBlock().getType() == Material.SIGN ||
							event.getBlock().getType() == Material.SIGN_POST){
							event.setCancelled(false);
						} else {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

}
