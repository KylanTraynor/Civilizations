package com.kylantraynor.civilizations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;

public class CivilizationsListener implements Listener{
	
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
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer() == null) return;
		if(Civilizations.getPlayersInProtectionMode().contains(event.getPlayer())){
			if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_AIR){
				Civilizations.selectTargetProtection(event.getPlayer());
				event.setCancelled(true);
			} else if(event.getPlayer() != null && event.getAction() == Action.LEFT_CLICK_BLOCK){
				SelectionManager.setPrimary(event.getPlayer(), event.getClickedBlock().getLocation());
				event.getPlayer().sendMessage(Civilizations.messageHeader + "Position 1 Set.");
				event.setCancelled(true);
			} else if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK){
				SelectionManager.setSecondary(event.getPlayer(), event.getClickedBlock().getLocation());
				event.getPlayer().sendMessage(Civilizations.messageHeader + "Position 2 Set.");
				event.setCancelled(true);
			}
		}
	}
}
