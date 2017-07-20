package com.kylantraynor.civilizations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.hook.HookManager;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.protection.PermissionType;

public class ProtectionListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer() == null) return;
		Player player = event.getPlayer();
		boolean canPlace = true;
		String reason = "";
		Plot plot = Plot.getAt(event.getBlock().getLocation());
		if(plot != null){
			if(plot.getPlotType() == PlotType.MARKETSTALL){
				if(plot.isOwner(event.getPlayer())){
					event.setCancelled(false);
				} else if(event.getPlayer().equals(plot.getRenter())){
					if(event.getBlock().getType() == Material.CHEST ||
						event.getBlock().getType() == Material.TRAPPED_CHEST ||
						event.getBlock().getType() == Material.SIGN ||
						event.getBlock().getType() == Material.SIGN_POST){
						if(plot.getSettlement() instanceof TownyTown){
							HookManager.getTowny().bypassPermsFor(event.getBlock());
						}
						event.setCancelled(false);
						
					} else {
						canPlace = false;
						reason = "you can only place chests and signs when renting a stall";
					}
				} else {
					canPlace = false;
					reason = "this stall doesn't belong to you";
				}
			} else if(!ProtectionManager.hasPermission(plot.getProtection(), PermissionType.PLACE, event.getPlayer(), true)){//plot.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
				canPlace = false;
				//reason = "you don't have the PLACE permission in " + plot.getName();
			}
		} else {
			Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
			if(settlement != null){
				if(settlement instanceof TownyTown) return;
				if(!ProtectionManager.hasPermission(settlement.getProtection(), PermissionType.PLACE, event.getPlayer(), true)){//settlement.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
					canPlace = false;
					//reason = "you don't have the PLACE permission in " + settlement.getName();
				}
			}
		}
		
		if(!canPlace){
			event.setCancelled(true);
			if(!reason.equalsIgnoreCase(""))
				player.sendMessage(ChatColor.RED + "You can't place blocks here because " + reason + ".");
		}
		/*
		if(Settlement.isProtected(event.getBlock().getLocation())){
			
			// Checks if the protection belongs to a Market Stall.
			for(MarketStall ms : CacheManager.getMarketstallList()){
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
		*/
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer() == null) return;
		boolean canBreak = true;
		String reason = "";
		Player player = event.getPlayer();
		Plot plot = Plot.getAt(event.getBlock().getLocation());
		if(plot != null){
			if(plot.getPlotType() == PlotType.MARKETSTALL){
				if(plot.isOwner(player)){
					event.setCancelled(false);
				} else if(plot.isRenter(player)){
					if(event.getBlock().getType() == Material.CHEST ||
						event.getBlock().getType() == Material.TRAPPED_CHEST ||
						event.getBlock().getType() == Material.SIGN ||
						event.getBlock().getType() == Material.SIGN_POST){
						if(plot.getSettlement() instanceof TownyTown){
							HookManager.getTowny().bypassPermsFor(event.getBlock());
						}
						event.setCancelled(false);
					} else {
						canBreak = false;
						reason = "this stall doesn't belong to you";
					}
				} else {
					canBreak = false;
					reason = "this stall doesn't belong to you";
				}
			} else if(!ProtectionManager.hasPermission(plot.getProtection(), PermissionType.BREAK, event.getPlayer(), true)){//plot.hasPermission(PermissionType.BREAK, event.getBlock(), player)){
				canBreak = false;
				//reason = "you don't have the BREAK permission in " + plot.getName();
			}
		} else {
			Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
			if(settlement != null){
				if(settlement instanceof TownyTown) return;
				if(!ProtectionManager.hasPermission(settlement.getProtection(), PermissionType.BREAK, event.getPlayer(), true)){//settlement.hasPermission(PermissionType.BREAK, event.getBlock(), event.getPlayer())){
					canBreak = false;
					//reason = "you don't have the BREAK permission in " + settlement.getName();
				}
			}
		}
		
		if(!canBreak){
			event.setCancelled(true);
			if(!reason.equalsIgnoreCase(""))
				player.sendMessage(ChatColor.RED + "You can't break blocks here because " + reason + ".");
		}
		/*
		if(Settlement.isProtected(event.getBlock().getLocation())){
			
			// Checks if the protection belongs to a Market Stall.
			for(MarketStall ms : CacheManager.getMarketstallList()){
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
		*/
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
			if(p.getPlotType() == PlotType.MARKETSTALL && event.getDamager() instanceof Player){
				if(p.isOwner((OfflinePlayer) event.getDamager())){
					
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntitySpawn(EntitySpawnEvent event){
		//Settlement settlement = Settlement.getAt(event.getLocation());
		//if(settlement == null) return;
		//if(settlement instanceof TownyTown) return;
		if(event.getEntity() instanceof LivingEntity){
			LivingEntity entity = (LivingEntity) event.getEntity();
			switch(entity.getType()){
			case ZOMBIE: case SKELETON: case CREEPER: case SPIDER: case CAVE_SPIDER: case WITHER_SKELETON: case HUSK: case ENDERMAN:
				if(!ProtectionManager.hasPermissionAt(event.getLocation(), PermissionType.MOBSPAWNING, null)){
					event.setCancelled(true);
				}
				break;
			default:
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Civilizations.displayProtectionStatus(event.getFrom(), event.getTo(), event.getPlayer());
	}
}
