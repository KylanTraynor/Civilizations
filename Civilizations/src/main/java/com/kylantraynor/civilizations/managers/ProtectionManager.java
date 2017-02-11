package com.kylantraynor.civilizations.managers;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.Permission;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;

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
		Protection p = getProtectionAt(location);
		if(p != null){
			return hasPermission(p, type, player);
		} else {
			return true;
		}
	}
	
	public static boolean hasPermission(Protection protection, PermissionType type, OfflinePlayer player){
	}
	
}
