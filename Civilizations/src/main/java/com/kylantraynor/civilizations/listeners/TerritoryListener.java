package com.kylantraynor.civilizations.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.territories.PlayerMoveData;

public class TerritoryListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(event.isCancelled()) return;
		if(event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
		
		InfluenceMap map = Civilizations.getInfluenceMap(event.getTo().getWorld());
		PlayerMoveData data = map.processPlayerMove(event.getPlayer(), event.getFrom(), event.getTo());
		if(Civilizations.currentInstance.isDEBUG()){
			String from = data.getFrom() == null ? "NullRegion" : data.getFrom().getName();
			String to = data.getTo() == null ? "NullRegion" : data.getTo().getName();
			Civilizations.DEBUG("Moving from " + from + " to " + to + ".");
		}
		if(data.changedRegion() && data.getTo() != null){
			TitleManagerHook.sendTitle("", data.getTo().getName(), 0, 1, 5, event.getPlayer());
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