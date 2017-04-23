package com.kylantraynor.civilizations.hook.livelyworld;

import org.bukkit.Bukkit;

import com.kylantraynor.civilizations.Civilizations;

public class LivelyWorldHook {
	
	private LivelyWorldListener listener = null;
	
	public LivelyWorldHook(){
		
		listener = new LivelyWorldListener();
		Bukkit.getPluginManager().registerEvents(listener, Civilizations.currentInstance);
		
	}
	
}
