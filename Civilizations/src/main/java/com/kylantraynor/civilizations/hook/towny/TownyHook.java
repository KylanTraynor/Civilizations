package com.kylantraynor.civilizations.hook.towny;

import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.kylantraynor.civilizations.Civilizations;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyHook {
	private static Plugin plugin;

	/**
	 * Tries to load the TitleManager plugin. Returns true if successfully loaded, returns false otherwise.
	 * @param manager
	 * @return boolean
	 */
	public static boolean load(PluginManager manager) {
		if((plugin = manager.getPlugin("Towny")) != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the state of the TitleManager hook. Returns true if the plugin is loaded and enabled, returns false otherwise.
	 * @return boolean
	 */
	public static boolean isEnabled(){
		if(plugin != null){
			return plugin.isEnabled();
		} else {
			return false;
		}
	}
	
	/**
	 * Loads all Towns data from Towny to Civilizations.
	 */
	public static void loadTownyTowns() {
		if(isEnabled()){
			List<Town> tl = TownyUniverse.getDataSource().getTowns();
			for(Town t : tl){
				Civilizations.log("INFO", "Loading " + t.getName() + ".");
				try {
					new TownyTown(t);
				} catch (TownyException e) {
					Civilizations.log("WARNING", t.getName() + " couldn't be loaded.");
				}
			}
		}
	}
}
