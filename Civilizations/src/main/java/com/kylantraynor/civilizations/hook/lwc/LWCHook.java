package com.kylantraynor.civilizations.hook.lwc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.event.LWCAccessEvent;

public class LWCHook implements Listener{
	
	public static boolean isActive(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("LWC");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public static boolean canAccessProtection(Player player, Block block){
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null){
				return LWC.getInstance().canAccessProtection(player, block);
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
	
	public static boolean hasPortection(Block block){
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null) return true;
		}
		return false;
	}
	
	public static boolean unlock(Block block){
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null){
				LWC.getInstance().findProtection(block).remove();
				return true;
			}
		}
		return false;
	}
}
