package com.kylantraynor.civilizations.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.events.PlayerTerritoryChangeEvent;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.territories.InfluenceMap;

public class TerritoryListener implements Listener {
	
	Map<Player, Fort> playerLocations = new HashMap<Player, Fort>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		/*
		if(event.isCancelled()) return;
		if(event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
		
		Fort f = InfluenceMap.getInfluentFortAt(event.getTo());
		Fort oldFort = null;
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