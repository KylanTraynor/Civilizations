package com.kylantraynor.civilizations.hook.towny;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyHook {
	
	private static Plugin plugin;

	/**
	 * Gets the state of the TitleManager hook. Returns true if the plugin is loaded and enabled, returns false otherwise.
	 * @return boolean
	 */
	public static boolean isActive(){
		if((plugin = Bukkit.getPluginManager().getPlugin("Towny")) != null){
			if(plugin != null){
				return plugin.isEnabled();
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Loads all Towns data from Towny to Civilizations.
	 */
	public static void loadTownyTowns() {
		if(isActive()){
			List<com.palmergames.bukkit.towny.object.Town> tl = com.palmergames.bukkit.towny.object.TownyUniverse.getDataSource().getTowns();
			for(com.palmergames.bukkit.towny.object.Town t : tl){
				Civilizations.log("INFO", "Loading " + t.getName() + ".");
				try {
					if(!isTownLoaded(t.getName())){
						new TownyTown(t);
					}
				} catch (Exception e) {
					Civilizations.log("WARNING", t.getName() + " couldn't be loaded.");
				}
			}
		}
	}
	
	public static boolean isTownLoaded(String name){
		for(TownyTown t : CacheManager.getTownyTownList()){
			if(t.getName().equalsIgnoreCase(name)){
				return true;
			}
		}
		return false;
	}
	
	public static TownyTown loadTownyTown(String name){
		if(isActive()){
			for(TownyTown t : CacheManager.getTownyTownList()){
				if(t.getName().equalsIgnoreCase(name)){
					return t;
				}
			}
			try {
				com.palmergames.bukkit.towny.object.Town t = com.palmergames.bukkit.towny.object.TownyUniverse.getDataSource().getTown(name);
				return new TownyTown(t);
			} catch (Exception e) {
				Civilizations.log("WARNING", name + " couldn't be loaded.");
				return null;
			}
		} else { return null;}
	}

	private static Map<com.palmergames.bukkit.towny.object.Resident, UUID> residentCacheManager = new HashMap<com.palmergames.bukkit.towny.object.Resident, UUID>();
	
	public static OfflinePlayer getPlayer(com.palmergames.bukkit.towny.object.Resident res){
		if(residentCacheManager.containsKey(res)){
			return Bukkit.getServer().getOfflinePlayer(residentCacheManager.get(res));
		} else {
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer((res.getName()));
			if(p != null){
				residentCacheManager.put(res, p.getUniqueId());
				return p;
			} else {
				Civilizations.log("WARNING", "Couldn't find player for resident " + res.getName() + ".");
			}
		}
		return null;
	}

	public static boolean hasSwitchPerm(Player player, Block block) {
		if(isActive()){
			com.palmergames.bukkit.towny.object.TownBlock tb = TownyUniverse.getTownBlock(block.getLocation());
			if(tb != null){
				if(tb.getPermissions() != null){
					if(tb.getPermissions().outsiderSwitch) return true;
					if(tb.getPermissions().allySwitch) return true;
					return false;
				}
			}
			return true;
		}else{
			return true;
		}
	}

	public static void bypassPermsFor(Block block) {
		if(isActive()){
			final com.palmergames.bukkit.towny.object.TownBlock tb = TownyUniverse.getTownBlock(block.getLocation());
			if(tb != null){
				if(tb.getPermissions() != null){
					tb.getPermissions().setAll(true);
					
					BukkitRunnable bk = new BukkitRunnable(){

						@Override
						public void run() {
							tb.getPermissions().reset();
						}
						
					};
					bk.runTaskLater(Civilizations.currentInstance, 1);
				}
			}
		}
	}
}
