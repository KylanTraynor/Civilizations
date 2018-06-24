package com.kylantraynor.civilizations.listeners;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.kylantraynor.civilizations.managers.AccountManager;
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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Crops;

import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.hook.HookManager;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.protection.PermissionType;

import mkremins.fanciful.civilizations.FancyMessage;

public class ProtectionListener implements Listener{
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent event){
		if(event.getBlock().getType() == Material.SOIL){
			Plot plot = Plot.getAt(event.getBlock().getLocation());
			if(plot != null){
				if(plot.getPlotType() == PlotType.CROPFIELD){
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getPlayer() == null) return;
		Player player = event.getPlayer();
		boolean canPlace = true;
		String reason = "";
        ProtectionManager.PermissionCheckResult result = null;
		Plot plot = Plot.getAt(event.getBlock().getLocation());
		if(plot != null){
			if(plot.getPlotType() == PlotType.MARKETSTALL){
				if(plot.isOwner(event.getPlayer())){
					if(plot.getSettlement() instanceof TownyTown){
						HookManager.getTowny().bypassPermsFor(event.getBlock());
					}
					event.setCancelled(false);
				} else if(plot.isRenter(event.getPlayer())){
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
			} else if(plot.getPlotType() == PlotType.CROPFIELD){
				if(plot.isOwner(player)){
					return;
				} else if(plot.isRenter(player)){
					canPlace = false;
					reason = "although you're renting this field, only its owner can modify it";
				} else {
					canPlace = false;
					reason = "this field doesn't belong to you";
				}
			} else if(!((result = ProtectionManager.hasPermission(PermissionType.PLACE, plot, event.getPlayer(), true)).getResult())){//plot.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
				canPlace = false;
				reason = "you don't have the PLACE permission in " + plot.getName();
			}
		} else {
			Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
			if(settlement != null){
				//if(settlement instanceof TownyTown) return;
				if(!((result = ProtectionManager.hasPermission(PermissionType.PLACE, settlement, event.getPlayer(), true)).getResult())){//settlement.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
					canPlace = false;
					reason = "you don't have the PLACE permission in " + settlement.getName();
				}
			}
		}
		
		if(!canPlace){
			event.setCancelled(true);
			if(!reason.equalsIgnoreCase("")){
				FancyMessage fm = new FancyMessage("You can't place blocks here because " + reason + ".");
				fm.color(ChatColor.RED);
				if(result != null){
				    fm.tooltip(result.getInfo());
                }
			    fm.send(player);
            }
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
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event){
		if(!ProtectionManager.hasPermissionAt(PermissionType.EXPLOSION, event.getBlock().getLocation(), null, true).getResult()){
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer() == null) return;
		boolean canBreak = true;
		String reason = "";
        ProtectionManager.PermissionCheckResult result = null;
		Player player = event.getPlayer();
		Plot plot = Plot.getAt(event.getBlock().getLocation());
		if(plot != null){
			if(plot.getPlotType() == PlotType.MARKETSTALL){
				if(plot.isOwner(player)){
					if(plot.getSettlement() instanceof TownyTown){
						HookManager.getTowny().bypassPermsFor(event.getBlock());
					}
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
			} else if(plot.getPlotType() == PlotType.CROPFIELD){
				if(plot.isOwner(player)){
					return;
				} else if(plot.isRenter(player)){
					if(!(event.getBlock().getState() instanceof Crops)){
						canBreak = false;
						reason = "although you're renting this field, only its owner can modify it";
					}
				} else {
					canBreak = false;
					reason = "this field doesn't belong to you";
				}
			} else if(!((result = ProtectionManager.hasPermission(PermissionType.BREAK, plot, event.getPlayer(), true)).getResult())){//plot.hasPermission(PermissionType.BREAK, event.getBlock(), player)){
				canBreak = false;
				reason = "you don't have the BREAK permission in " + plot.getName();
			}
		} else {
			Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
			if(settlement != null){
				if(!((result = ProtectionManager.hasPermission(PermissionType.BREAK, settlement, event.getPlayer(), true)).getResult())){//settlement.hasPermission(PermissionType.BREAK, event.getBlock(), event.getPlayer())){
					canBreak = false;
					reason = "you don't have the BREAK permission in " + settlement.getName();
				}
			}
		}

        if(!canBreak){
            event.setCancelled(true);
            if(!reason.equalsIgnoreCase("")){
                FancyMessage fm = new FancyMessage("You can't break blocks here because " + reason + ".");
                fm.color(ChatColor.RED);
                if(result != null){
                    fm.tooltip(result.getInfo());
                }
                fm.send(player);
            }
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
			case SLIME: case WITCH: case ZOMBIE_VILLAGER: case ZOMBIE: case SKELETON: case CREEPER: case SPIDER: case CAVE_SPIDER: case WITHER_SKELETON: case HUSK: case ENDERMAN: case STRAY:
				if(!ProtectionManager.hasPermissionAt(PermissionType.MOBSPAWNING, event.getLocation(), null, true).getResult()){
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
		if(event.getFrom().getBlock() != event.getTo().getBlock()){
			Camp c = Camp.getCampAt(event.getTo());
			if(c != null){
				if(c.isMember(AccountManager.getCurrentIdentifier(event.getPlayer()), true)){
					if(c.getExpireOn().isBefore(Instant.now().plus(Camp.campDuration - 2, ChronoUnit.HOURS))){
						c.setExpireOn(Instant.now().plus(Camp.campDuration, ChronoUnit.HOURS));
					}
				}
			}
		}
	}
}
