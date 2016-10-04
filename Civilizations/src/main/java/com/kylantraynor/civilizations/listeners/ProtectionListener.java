package com.kylantraynor.civilizations.listeners;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.protection.PermissionType;

public class ProtectionListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
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
							if(ms.getSettlement() instanceof TownyTown){
								TownyHook.bypassPermsFor(event.getBlock());
							}
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
	
	@EventHandler(priority = EventPriority.LOWEST)
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
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(!Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
				
				if(LockManager.isLockable(event.getClickedBlock())){
					if(LockManager.isLocked(event.getClickedBlock())){
						if(!LockManager.hasAccess(event.getPlayer(), event.getClickedBlock())){
							if(event.getPlayer().getInventory().containsAtLeast(LockManager.getLockpick(1), 1)){
								LockManager.startLockpicking(event.getPlayer(), event.getClickedBlock());
							} else {
								event.getPlayer().sendMessage("You don't have any lockpick.");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntityType() == EntityType.VILLAGER){
			Plot p = Plot.getAt(event.getEntity().getLocation());
			if(p instanceof MarketStall && event.getDamager() instanceof Player){
				if(((MarketStall)p).isOwner((OfflinePlayer) event.getDamager())){
					
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
}
