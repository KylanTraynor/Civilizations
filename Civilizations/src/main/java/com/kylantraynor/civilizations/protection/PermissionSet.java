package com.kylantraynor.civilizations.protection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
