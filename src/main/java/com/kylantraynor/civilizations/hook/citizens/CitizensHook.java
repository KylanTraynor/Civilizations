package com.kylantraynor.civilizations.hook.citizens;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CitizensHook {
	
	public boolean isEnabled(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Citizens");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public boolean isSentryEnabled(){
		if(!isEnabled()) return false;
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Sentry");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
}
