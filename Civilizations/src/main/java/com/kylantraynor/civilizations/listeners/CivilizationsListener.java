package com.kylantraynor.civilizations.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer() == null) return;
		if(event.getPlayer().getInventory().getItemInMainHand() != null){
			if(Civilizations.isSelectionTool(event.getPlayer().getInventory().getItemInMainHand())){
				if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_AIR){
					Civilizations.selectTargetProtection(event.getPlayer());
					event.setCancelled(true);
					return;
				} else if(event.getPlayer() != null && event.getAction() == Action.LEFT_CLICK_BLOCK){
					String selectionMode = SelectionManager.getSelectionMode(event.getPlayer());
					if(selectionMode.equals("HULL")){
						SelectionManager.addPoint(event.getPlayer(), event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage(Civilizations.messageHeader + "Point " + SelectionManager.getPoints(event.getPlayer()).size() + " Set.");
						event.setCancelled(true);
					} else {
						SelectionManager.setPrimary(event.getPlayer(), event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage(Civilizations.messageHeader + "Position 1 Set.");
						event.setCancelled(true);
					}
					return;
				} else if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK){
					String selectionMode = SelectionManager.getSelectionMode(event.getPlayer());
					if(selectionMode.equals("HULL")){
						if(SelectionManager.removeLastPoint(event.getPlayer())){
							event.getPlayer().sendMessage(Civilizations.messageHeader + "Last point removed from point cloud selection.");
						}
					} else {
						SelectionManager.setSecondary(event.getPlayer(), event.getClickedBlock().getLocation());
						event.getPlayer().sendMessage(Civilizations.messageHeader + "Position 2 Set.");
						event.setCancelled(true);
					}
					return;
				}
			}
		}
		if(event.getPlayer() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block b = event.getClickedBlock();
			if(b == null) return;
			if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST){
				BlockState state = b.getState();
				Sign sign = (Sign) state;
				if(sign.getLine(0).equalsIgnoreCase("~BOARD~")){
					event.setCancelled(true);
					Protection p = ProtectionManager.getProtectionAt(sign.getLocation());
					if(p == null){
						event.getPlayer().sendMessage(ChatColor.RED + "There is no protected area here.");
						return;
					}
					Civilizations.DEBUG("Opening menu for " + event.getPlayer().getName() + ".");
					p.getGroup().openMenu(event.getPlayer());
				}
			}
		}
	}
}
