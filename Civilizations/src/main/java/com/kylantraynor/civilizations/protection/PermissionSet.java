package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import mkremins.fanciful.FancyMessage;

public class PermissionSet {
	
	private Map<PermissionTarget, Permission> permissions = new HashMap<PermissionTarget, Permission>();
	/**
	 * Gets Permission for the given Target.
	 * @param target
	 * @return Permission
	 */
	public Permission get(PermissionTarget target){
		return permissions.get(target);
	}
	/**
	 * Adds a permission with the given target to the list of permissions.
	 * @param target
	 * @param permission
	 * @return Returns the last Permission for this target, or null.
	 */
	public Permission add(PermissionTarget target, Permission permission){
		return permissions.put(target, permission);
	}
	/**
	 * Checks if this set has Permissions for the given target.
	 * @param target
	 * @return true if Permissions exist for this target, false otherwise.
	 */
	public boolean hasTarget(PermissionTarget target) {
		if(permissions.containsKey(target)){
			if(permissions.get(target) != null){
				return true;
			}
		}
		return false;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(ChatColor.GOLD + "PERMISSIONS:\n");
		for(Entry<PermissionTarget, Permission> e : permissions.entrySet()){
			switch(e.getKey().getType()){
			case PLAYER:
				PlayerTarget pt = (PlayerTarget) e.getKey();
				sb.append("PLAYER: " + pt.getPlayer().getName() + ": " + e.getValue().toString() + "\n");
				break;
			case RANK:
				break;
			default:
				sb.append(e.getKey().getType().toString() + ": " + e.getValue().toString() + "\n");
				break;
			}
		}
		return sb.toString();
	}
	
	public FancyMessage getFancyMessage(){
		FancyMessage fm = new FancyMessage("PERMISSIONS:\n").color(ChatColor.GOLD);
		Map<PermissionType, List<PermissionTarget>> permissionTypes = new HashMap<PermissionType, List<PermissionTarget>>();
		for(Entry<PermissionTarget, Permission> e : permissions.entrySet()){
			for(Entry<PermissionType, Boolean> e1 : e.getValue().getTypes().entrySet()){
				if(e1.getValue()){
					if(permissionTypes.containsKey(e1.getKey())){
						permissionTypes.get(e1.getKey()).add(e.getKey());
					} else {
						List<PermissionTarget> l = new ArrayList<PermissionTarget>();
						l.add(e.getKey());
						permissionTypes.put(e1.getKey(), l);
					}
				}
			}
			switch(e.getKey().getType()){
			case PLAYER:
				PlayerTarget pt = (PlayerTarget) e.getKey();
				fm.then("PLAYER: " + pt.getPlayer().getName() + ": " + e.getValue().toString() + "\n");
				break;
			case RANK:
				break;
			default:
				fm.then(e.getKey().getType().toString() + ": " + e.getValue().toString() + "\n");
				break;
			}
		}
		for(Entry<PermissionType, List<PermissionTarget>> e : permissionTypes.entrySet()){
			StringBuilder sb = new StringBuilder();
			for(PermissionTarget pt : e.getValue()){
				if(pt.getType() == TargetType.PLAYER){
					sb.append("" + ((PlayerTarget)pt).getPlayer().getName().toString() + ", ");
				} else if (pt.getType() == TargetType.RANK){
					sb.append("" + pt.getType().toString() + ", ");
				} else {
					sb.append("" + pt.getType().toString() + ", ");
				}
			}
			fm.then(e.getKey().toString() + " ").tooltip(sb.toString());
		}
		return fm;
	}
}
