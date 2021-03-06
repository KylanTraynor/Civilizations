package com.kylantraynor.civilizations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.territories.PlayerMoveData;
import com.kylantraynor.civilizations.utils.Utils;

public class TerritoryListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.isCancelled()) return;
		if(event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
		
		InfluenceMap map = Civilizations.getInfluenceMap(event.getTo().getWorld());
		if(map == null) return;
		PlayerMoveData data = map.processPlayerMove(event.getPlayer(), event.getFrom().getBlock().getLocation(), event.getTo().getBlock().getLocation());
		
		if(data.changedRegion() && data.getTo() != null){
			ChatColor color = ChatColor.WHITE;
			if(data.getTo().getNation() == null){
				TitleManagerHook.sendActionBar("Independant Territory", event.getPlayer(), false);
			} else {
				color = Utils.getChatColor(Utils.getColor(data.getTo().getNation().getBanner().getBase()));
				TitleManagerHook.sendActionBar(color + data.getTo().getNation().getName() + " Nation", event.getPlayer(), false);
				//data.getTo().getNation().getBanner().getBaseColor();
			}
			// Prettyfy text for towny names, to get rid of "_" .
			TitleManagerHook.sendTitle("", "" + color + Utils.prettifyText(data.getTo().getName()), 5, 30, 10, event.getPlayer());
		}
		/*Fort f = InfluenceMap.getInfluentFortAt(event.getTo());
		/Fort oldFort = null;
		if(playerLocations.containsKey(event.getPlayer())){
			oldFort = playerLocations.get(event.getPlayer());
		}
		boolean territoryHasChanged = false;
		if(f != null){
			if(oldFort == null){
				territoryHasChanged = true;
			} else if(!oldFort.equals(f)) {
				territoryHasChanged = true;
			}
		} else {
			if(oldFort != null){
				territoryHasChanged = true;
			}
		}
		if(territoryHasChanged){
			PlayerTerritoryChangeEvent e = new PlayerTerritoryChangeEvent(event.getPlayer(), oldFort, f);
			Civilizations.callEvent(e);
			if(!e.isCancelled()){
				if(e.hasNewFort()){
					playerLocations.put(event.getPlayer(), f);
					TitleManagerHook.sendActionBar("House " + f.getHouse().getName() + "'s territory.", event.getPlayer(), false);
				} else {
					if(playerLocations.containsKey(event.getPlayer())){
						playerLocations.remove(event.getPlayer());
					}
				}
			} else {
				event.setCancelled(true);
			}
		}
		*/
	}

}