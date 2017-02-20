package com.kylantraynor.civilizations.hook.towny;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.event.TownUnclaimEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class TownyListener implements Listener{
	
	@EventHandler
	public void onNewTown(NewTownEvent event){
		if(event.getTown() != null){
			TownyHook.loadTownyTown(event.getTown().getName());
		}
	}
	
	@EventHandler
	public void onDeleteTown(DeleteTownEvent event){
		for(TownyTown t : CacheManager.getTownyTownList()){
			if(t.getName().equalsIgnoreCase(event.getTownName())){
				t.remove();
			}
		}
	}
	
	public void onTownAddResident(TownAddResidentEvent event){
		
	}
	
	@EventHandler
	public void onTownClaim(TownClaimEvent event){
		TownBlock tb = event.getTownBlock();
		Town town = null;
		try {
			town = tb.getTown();
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		if(town != null){
			for(TownyTown t : TownyTown.getTownyTownList()){
				if(t.getName().equalsIgnoreCase(town.getName())){
					try {
						t.addTownyPlot(tb);
					} catch (TownyException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onTownUnclaim(TownUnclaimEvent event){
		Town town = event.getTown();
		WorldCoord w = event.getWorldCoord();
		if(town != null){
			for(TownyTown t : TownyTown.getTownyTownList()){
				if(t.getName().equalsIgnoreCase(town.getName())){
					try {
						t.removeTownyPlot(w.getTownBlock());
					} catch (TownyException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
}
