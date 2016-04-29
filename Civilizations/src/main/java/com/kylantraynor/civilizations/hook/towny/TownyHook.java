package com.kylantraynor.civilizations.hook.towny;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.kylantraynor.civilizations.Civilizations;

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
			List<com.palmergames.bukkit.towny.object.Town> tl = com.palmergames.bukkit.towny.object.TownyUniverse.getDataSource().getTowns();
			for(com.palmergames.bukkit.towny.object.Town t : tl){
				Civilizations.log("INFO", "Loading " + t.getName() + ".");
				try {
					new TownyTown(t);
				} catch (Exception e) {
					Civilizations.log("WARNING", t.getName() + " couldn't be loaded.");
				}
			}
		}
	}
	
	public static TownyTown loadTownyTown(String name){
		if(isEnabled()){
			try {
				com.palmergames.bukkit.towny.object.Town t = com.palmergames.bukkit.towny.object.TownyUniverse.getDataSource().getTown(name);
				return new TownyTown(t);
			} catch (com.palmergames.bukkit.towny.exceptions.NotRegisteredException e) {
				Civilizations.log("WARNING", name + " couldn't be found.");
				return null;
			} catch (com.palmergames.bukkit.towny.exceptions.TownyException e) {
				Civilizations.log("WARNING", name + " couldn't be loaded.");
				return null;
			}
		} else { return null;}
	}

	private static Map<com.palmergames.bukkit.towny.object.Resident, UUID> residentCache = new HashMap<com.palmergames.bukkit.towny.object.Resident, UUID>();
	
	public static OfflinePlayer getPlayer(com.palmergames.bukkit.towny.object.Resident res){
		if(residentCache.containsKey(res)){
			return Bukkit.getServer().getOfflinePlayer(residentCache.get(res));
		} else {
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer((res.getName()));
			if(p != null){
				residentCache.put(res, p.getUniqueId());
				return p;
			} else {
				Civilizations.log("WARNING", "Couldn't find player for resident " + res.getName() + ".");
			}
		}
		return null;
	}
}
