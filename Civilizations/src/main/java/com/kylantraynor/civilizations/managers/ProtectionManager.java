package com.kylantraynor.civilizations.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Permissions;
import com.kylantraynor.civilizations.protection.PlayerTarget;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;

public class ProtectionManager {
	
	public static Protection getProtectionAt(Location location){
		Protection result = null;
		Plot p = Plot.getAt(location);
		if(p == null){
			Settlement s = Settlement.getAt(location);
			if(s != null){
				result = s.getProtection();
			}
		} else {
			result = p.getProtection();
		}
		return result;
	}
	
	public static boolean hasPermissionAt(Location location, PermissionType type, OfflinePlayer player){
		return hasPermissionAt(location, type, player, false);
	}
	
	public static boolean hasPermissionAt(Location location, PermissionType type, OfflinePlayer player, boolean displayResult){
		Protection p = getProtectionAt(location);
		if(p != null){
			return hasPermission(p, type, player, displayResult);
		} else {
			return true;
		}
	}
	
	public static Boolean getPermission(Protection protection, PermissionTarget target, PermissionType type){
		//Permissions perms = protection.getPermissions(target);
		Boolean result = protection.getPermission(target, type);
		if(result == null){
			Protection parent = protection.getParent();
			if(parent != null){
				result = getPermission(parent, target, type);
			}
		}
		return result;
	}
	
	public static void setPermission(Protection protection, PermissionTarget target, PermissionType type, Boolean value){
		Permissions perms = protection.getPermissions(target);
		if(perms != null){
			perms.set(type, value);
			protection.getGroup().getSettings().setChanged(true);
		} else if(value != null) {
			Map<PermissionType, Boolean> perm = new HashMap<PermissionType, Boolean>();
			perm.put(type, value);
			protection.setPermissions(target, new Permissions(perm));
			protection.getGroup().getSettings().setChanged(true);
		}
	}
	
	public static boolean hasPermission(Protection protection, PermissionType type, OfflinePlayer player, boolean displayResult){
		Protection currentProtection = protection;
		Boolean result = null;
		// First, check if the player is op
		if(player != null){
			if(player.isOp()) return true;
		}
		
		
		while(currentProtection != null && result == null && player != null){
			// Check if the protection has a specific permission set for the player
			PlayerTarget pt = new PlayerTarget(player);
			result = currentProtection.getPermission(pt, type);
			if(result != null) break;
			// If not, check if the protection has a specific permission set for the player's rank
			Rank r = currentProtection.getRank(player);
			while(r != null){
				result = currentProtection.getPermission(r, type);
				if(result != null) break; else r = currentProtection.getRank(r.getParentId());
			}
			
			// If not, check if the protection has a permission set for any group the player belongs to
			for(PermissionTarget target : currentProtection.getPermissionSet().getTargets()){
				if(target instanceof GroupTarget){
					if(((GroupTarget) target).isPartOf(player)){
						result = currentProtection.getPermission(target, type);
						if(result != null) break;
					}
				}
			}
			if(result != null) break;
			// If not, check if the protection has a permission set for outsiders
			//result = getPermission(currentProtection, PermissionTarget.OUTSIDERS, type);
			result = currentProtection.getPermission(PermissionTarget.OUTSIDERS, type);
			
			// If not, just return false.
			currentProtection = currentProtection.getParent();
		}
		
		if(player == null){
			result = getPermission(currentProtection, PermissionTarget.SERVER, type);
			if(result == null){
				result = true;
			}
			/*while(currentProtection != null){
				PermissionTarget o = PermissionTarget.SERVER;
				if(currentProtection.getPermissionSet().isSet(type, o)){
					result = currentProtection.getPermission(type, o);
				}
				// If not, just return false.
				if(currentProtection.getParent() != null){
					currentProtection = currentProtection.getParent();
				} else {
					currentProtection = null;
				}
			}*/
		}
		
		if(result == false && displayResult){
			if(player != null){
				if(player.isOnline()){
					player.getPlayer().sendMessage(ChatColor.RED + "You don't have " + ChatColor.GOLD + (type.getDescription()) + ChatColor.RED + " in " + ChatColor.GOLD + currentProtection.getGroup().getName());
				}
			}
		}
		return result;
	}

	public static void unsetPermission(Protection protection, PermissionTarget target, PermissionType pt) {
		setPermission(protection, target, pt, null);
	}
	
}
