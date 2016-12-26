package com.kylantraynor.civilizations.hook.towny;

import org.bukkit.event.Listener;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;

public class TownyListener implements Listener{
	
	public void onNewTown(NewTownEvent event){
		if(event.getTown() != null){
			TownyHook.loadTownyTown(event.getTown().getName());
		}
	}
	
	public void onDeleteTown(DeleteTownEvent event){
		for(TownyTown t : CacheManager.getTownyTownList()){
			if(t.getName().equalsIgnoreCase(event.getTownName())){
				t.remove();
			}
		}
	}
	
	public void onTownAddResident(TownAddResidentEvent event){
		
	}
	
}
