package com.kylantraynor.civilizations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;

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
		} else {
			if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK){
				Block b = event.getClickedBlock();
				if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST){
					BlockState state = b.getState();
					Sign sign = (Sign) state;
					if(sign.getLine(0).equalsIgnoreCase("~BOARD~")){
						event.setCancelled(true);
						Protection p = ProtectionManager.getProtectionAt(sign.getLocation());
						if(p == null){
							event.getPlayer().sendMessage(ChatColor.RED + "There is no protected area here.");
							return;
						}
						p.getGroup().openMenu(event.getPlayer());
					}
				}
			}
		}
	}
}
