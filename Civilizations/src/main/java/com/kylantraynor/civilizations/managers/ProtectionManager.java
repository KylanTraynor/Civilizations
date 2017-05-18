package com.kylantraynor.civilizations.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
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
			result = s.getProtection();
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
	
	public static boolean hasPermission(Protection protection, PermissionType type, OfflinePlayer player, boolean displayResult){
		Protection currentProtection = protection;
		Boolean result = null;
		while(currentProtection != null && result == null && player != null){
			// First, check if the player is op
			if(player.isOp()) return true;
			// If not, check if the protection has a specific permission set for the player
			PlayerTarget pt = new PlayerTarget(player);
			if(currentProtection.getPermissionSet().isSet(type, pt)){
				result = currentProtection.getPermission(type, pt);
				break;
			}
			// If not, check if the protection has a specific permission set for the player's rank
			Rank r = currentProtection.getRank(player);
			if(r != null){
				if(currentProtection.getPermissionSet().isSet(type, r)){
					result = currentProtection.getPermission(type, r);
					break;
				} else {
					while(r.getParentId() != null){
						Rank rParent = currentProtection.getRank(r.getParentId());
						if(currentProtection.getPermissionSet().isSet(type, rParent)){
							result = currentProtection.getPermission(type, rParent);
							break;
						} else {
							r = rParent;
						}
					}
					if(result != null){
						break;
					}
				}
			}
			
			// If not, check if the protection has a permission set for any group the player belongs to
			for(PermissionTarget target : currentProtection.getPermissionSet().getTargets()){
				if(target instanceof GroupTarget){
					if(((GroupTarget) target).isPartOf(player)){
						if(currentProtection.getPermissionSet().isSet(type, target)){
							result = currentProtection.getPermission(type, target);
							break;
						}
						
					}
				}
			}
			if(result != null){
				break;
			}
			// If not, check if the protection has a permission set for outsiders
			PermissionTarget o = new PermissionTarget(TargetType.OUTSIDERS);
			if(currentProtection.getPermissionSet().isSet(type, o)){
				result = currentProtection.getPermission(type, o);
				break;
			}
			
			// If not, just return false.
			if(currentProtection.getParent() != null){
				currentProtection = currentProtection.getParent();
			} else {
				currentProtection = null;
			}
		}
		
		if(player == null){
			while(currentProtection != null){
				PermissionTarget o = new PermissionTarget(TargetType.SERVER);
				if(currentProtection.getPermissionSet().isSet(type, o)){
					result = currentProtection.getPermission(type, o);
				}
				// If not, just return false.
				if(currentProtection.getParent() != null){
					currentProtection = currentProtection.getParent();
				} else {
					currentProtection = null;
				}
			}
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
	
}
