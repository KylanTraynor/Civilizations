package com.kylantraynor.civilizations.hook.worldborder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;

public class WorldBorderHook {
	
	public static boolean isActive(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldBorder");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public static BorderData getWorldBorder(World w){
		return Config.Border(w.getName());
	}
	
	public static boolean isWorldCircular(World w){
		if(!isActive()) return false;
		BorderData border = getWorldBorder(w);
		if(border != null){
			return border.getShape();
		} else {
			return false;
		}
	}
	
	public static int getWorldRadiusX(World w){
		if(!isActive()) return 0;
		BorderData border = getWorldBorder(w);
		if(border != null){
			return border.getRadiusX();
		} else {
			return 0;
		}
	}
	
	public static int getWorldRadiusZ(World w){
		if(!isActive()) return 0;
		BorderData border = getWorldBorder(w);
		if(border != null){
			return border.getRadiusZ();
		} else {
			return 0;
		}
	}
	
	public static Location getWorldCenter(World w){
		if(!isActive()) return new Location(w, 0, 0, 0);
		BorderData border = getWorldBorder(w);
		if(border != null){
			return new Location(w, border.getX(), 0, border.getZ());
		} else {
			return new Location(w, 0, 0, 0);
		}
	}
}
