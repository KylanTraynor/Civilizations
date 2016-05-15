package com.kylantraynor.civilizations.hook.lwc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.model.Protection.Type;
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
	
	public static Type getLockType(Block block){
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null){
				return LWC.getInstance().findProtection(block).getType();
			}
		}
		return null;
	}
	
	public static boolean unlock(Block block){
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null){
				if(LWC.getInstance().findProtection(block).getType() == Type.PRIVATE){
					LWC.getInstance().findProtection(block).remove();
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public static void tempUnlock(Block block, Player p) {
		if(isActive()){
			if(LWC.getInstance().findProtection(block) != null){
				
			}
		}
	}
}
