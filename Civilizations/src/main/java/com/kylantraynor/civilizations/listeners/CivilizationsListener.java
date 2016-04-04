package com.kylantraynor.civilizations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.protection.PermissionType;

public class CivilizationsListener implements Listener{
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement != null){
			if(settlement instanceof TownyTown) return;
			if(!settlement.hasPermission(PermissionType.BREAK, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here.");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement != null){
			if(settlement instanceof TownyTown) return;
			if(!settlement.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks here.");
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement == null) return;
		if(settlement instanceof TownyTown) return;
		switch(event.getCause()){
		case SPREAD:
			if(!settlement.hasPermission(PermissionType.FIRESPREAD, event.getBlock(), null)){
				event.setCancelled(true);
			}
			break;
		case FLINT_AND_STEEL:
			if(!settlement.hasPermission(PermissionType.FIRE, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't start a fire here.");
			}
			break;
		default:
			if(!settlement.hasPermission(PermissionType.FIRE, event.getBlock(), null)){
				event.setCancelled(true);
			}
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntitySpawn(EntitySpawnEvent event){
		Settlement settlement = Settlement.getAt(event.getLocation());
		if(settlement == null) return;
		if(settlement instanceof TownyTown) return;
		if(event.getEntity() instanceof LivingEntity){
			LivingEntity entity = (LivingEntity) event.getEntity();
			switch(entity.getType()){
			case ZOMBIE: case SKELETON: case CREEPER:
				if(!settlement.hasPermission(PermissionType.MOBSPAWNING, event.getLocation().getBlock(), null)){
					event.setCancelled(true);
				}
				break;
			default:
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(item != null && item.getType() == Material.STICK){
			if(!Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
				Civilizations.getPlayersInProtectionMode().add(event.getPlayer());
				/*for(Settlement s : Settlement.getSettlementList()){
					updateProtectionVisibility(event.getPlayer(), s.getProtection());
					for(Plot p : s.getPlots()){
						updateProtectionVisibility(event.getPlayer(), p.getProtection());
					}
				}*/
			}
		} else {
			if(Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
				Civilizations.getPlayersInProtectionMode().remove(event.getPlayer());
				/*for(Settlement s : Settlement.getSettlementList()){
					updateProtectionVisibility(event.getPlayer(), s.getProtection());
					for(Plot p : s.getPlots()){
						updateProtectionVisibility(event.getPlayer(), p.getProtection());
					}
				}*/
			}
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		if(event.getPlayer() != null){
			if(Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer() != null){
			if(Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
				Civilizations.selectTargetProtection(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Civilizations.displayProtectionStatus(event.getFrom(), event.getTo(), event.getPlayer());
	}
}
