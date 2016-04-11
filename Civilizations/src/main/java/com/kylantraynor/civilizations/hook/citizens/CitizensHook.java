package com.kylantraynor.civilizations.hook.citizens;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CitizensHook {
	
	public static boolean isEnabled(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Citizens");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
}
