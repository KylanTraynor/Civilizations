package com.kylantraynor.civilizations.hook.draggyrpg;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.draggyrpg.entities.RPGLevelCenter;

public class DraggyRPGHook {
	
	public static Map<Object, RPGLevelCenter> levelCenters = new HashMap<Object, RPGLevelCenter>();
	
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
	
	public static boolean createTaggedLevelCenter(Object key, Location location, int startLevel, double a, double b, double c, boolean persist){
		if(levelCenters.containsKey(key)){
			return false;
		} else {
			levelCenters.put(key, RPGLevelCenter.create(location,  startLevel, a, b, c, persist));
			return true;
		}
	}
	
	public static boolean updateTaggedLevelCenter(Object key, Location location, int startLevel, double a, double b, double c){
		if(levelCenters.containsKey(key)){
			levelCenters.get(key).setLocation(location);
			levelCenters.get(key).setLevel(startLevel);
			levelCenters.get(key).setThirdOrder(a);
			levelCenters.get(key).setSecondOrder(b);
			levelCenters.get(key).setFirstOrder(c);
			return true;
		} else {
			return false;
		}
	}
	
	public static RPGLevelCenter getTaggedLevelCenter(Object key){
		if(levelCenters.containsKey(key)){
			return levelCenters.get(key);
		} else {
			return null;
		}
	}
	
	public static boolean removeTaggedLevelCenter(Object key){
		if(levelCenters.containsKey(key)){
			return RPGLevelCenter.remove(levelCenters.remove(key));
		} else {
			return false;
		}
	}

	public static void loadLevelCenters() {
		for(Settlement s : Cache.getSettlementList()){
			int number = s.getMembers().size();
			if(number > 0){
				createTaggedLevelCenter(s, s.getLocation(), 1, 0, 0.01 / number, 0, false);
				Civilizations.log("INFO", "Loaded LevelCenter in " + s.getLocation().getWorld().getName() + " for " + s.getName() + ".");
			}
		}
	}
	
	public static void updateLevelCenters(){
		for(Settlement s : Cache.getSettlementList()){
			if(levelCenters.containsKey(s)){
				int number = s.getMembers().size();
				if(number > 0){
					if(updateTaggedLevelCenter(s, s.getLocation(), 1, 0, 0.01 / number, 0)){
						Civilizations.log("INFO", "Updated LevelCenter in " + s.getLocation().getWorld().getName() + " for " + s.getName() + ".");
					}
				}
			}
		}
	}
	
}