package com.kylantraynor.civilizations.hook.draggyrpg;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.draggyrpg.entities.RPGLevelCenter;

public class DraggyRPGHook {
	
	public static Map<String, RPGLevelCenter> levelCenters = new HashMap<String, RPGLevelCenter>();
	
	public static boolean isActive(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("DraggyRPG");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public static void createLevelCenter(Location location, int startLevel, double a, double b, double c, boolean persist){
		RPGLevelCenter.create(location, startLevel, a, b, c, persist);
	}
	
	public static boolean createTaggedLevelCenter(String key, Location location, int startLevel, double a, double b, double c, boolean persist){
		if(levelCenters.containsKey(key)){
			return false;
		} else {
			levelCenters.put(key, RPGLevelCenter.create(location,  startLevel, a, b, c, persist));
			return true;
		}
	}
	
	public static boolean updateTaggedLevelCenter(String key, Location location, int startLevel, double a, double b, double c){
		RPGLevelCenter lc = levelCenters.get(key);
		if(lc != null){
			lc.setLocation(location);
			lc.setLevel(startLevel);
			lc.setThirdOrder(a);
			lc.setSecondOrder(b);
			lc.setFirstOrder(c);
			return true;
		} else {
			return false;
		}
	}
	
	public static RPGLevelCenter getTaggedLevelCenter(String key){
		return levelCenters.get(key);
	}
	
	public static boolean removeTaggedLevelCenter(Object key){
		if(levelCenters.containsKey(key)){
			return RPGLevelCenter.remove(levelCenters.remove(key));
		} else {
			return false;
		}
	}

	public void loadLevelCenters() {
		for(Settlement s : Settlement.getAll()){
			int number = s.getMembers().size();
			if(number > 0){
				createTaggedLevelCenter(s.getIdentifier().toString(), s.getLocation(), 1, 0, 0.01 / number, 0, false);
				Civilizations.log("INFO", "Loaded LevelCenter in " + s.getLocation().getWorld().getName() + " for " + s.getName() + ".");
			}
		}
	}
	
	public void updateLevelCenters(){
		for(Settlement s : Settlement.getAll()){
			if(levelCenters.containsKey(s.getIdentifier().toString())){
				int number = s.getMembers().size();
				if(number > 0){
					if(updateTaggedLevelCenter(s.getIdentifier().toString(), s.getLocation(), 1, 0, 0.01 / number, 0)){
						Civilizations.DEBUG("Updated LevelCenter in " + s.getLocation().getWorld().getName() + " for " + s.getName() + ".");
					}
				} else {
					removeTaggedLevelCenter(s.getIdentifier().toString());
				}
			} else {
				int number = s.getMembers().size();
				if(number > 0){
					createTaggedLevelCenter(s.getIdentifier().toString(), s.getLocation(), 1, 0, 0.01 / number, 0, false);
					Civilizations.log("INFO", "Created LevelCenter in " + s.getLocation().getWorld().getName() + " for " + s.getName() + ".");
				}
			}
		}
	}
	
}