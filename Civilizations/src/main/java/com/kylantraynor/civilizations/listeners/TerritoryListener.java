package com.kylantraynor.civilizations.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.territories.InfluenceMap;

public class TerritoryListener implements Listener {
	
	Map<Player, Fort> playerLocations = new HashMap<Player, Fort>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		
		if(event.getTo().getBlock().equals(event.getFrom().getBlock())) return;
		
		Fort f = InfluenceMap.getInfluentFortAt(event.getTo());
		if(f != null){
			if(playerLocations.containsKey(event.getPlayer())){
				if(playerLocations.get(event.getPlayer()).equals(f)){
					return;
				} else {
					playerLocations.put(event.getPlayer(), f);
					event.getPlayer().sendMessage("You are on House " + f.getHouse().getName() + "'s territory.");
				}
			} else {
				playerLocations.put(event.getPlayer(), f);
				event.getPlayer().sendMessage("You are on House " + f.getHouse().getName() + "'s territory.");
			}
		} else {
			playerLocations.remove(event.getPlayer());
		}
	}

}