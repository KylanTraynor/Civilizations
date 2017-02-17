package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;

import mkremins.fanciful.civilizations.FancyMessage;

public class PermissionSet extends HashMap<PermissionTarget, Permissions>{
	/**
	 * Gets Permission for the given Target.
	 * @param target
	 * @return Permission
	 */
	public Permissions get(PermissionTarget target){
		return this.get(target);
	}
	
	public Set<PermissionTarget> getTargets(){
		return this.keySet();
	}
	
	public boolean isSet(PermissionType type, PermissionTarget target){
		if(hasTarget(target)){
			if(this.get(target).contains(type)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Adds a permission with the given target to the list of permissions.
	 * @param target
	 * @param permission
	 * @return Returns the last Permission for this target, or null.
	 */
	public Permissions add(PermissionTarget target, Permissions permission){
		return this.put(target, permission);
	}
	/**
	 * Checks if this set has Permissions for the given target.
	 * @param target
	 * @return true if Permissions exist for this target, false otherwise.
	 */
	public boolean hasTarget(PermissionTarget target) {
		if(this.get(target) != null){
			return true;
		}
		return false;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(ChatColor.GOLD + "PERMISSIONS:\n");
		for(Entry<PermissionTarget, Permissions> e : this.entrySet()){
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
		for(Entry<PermissionTarget, Permissions> e : this.entrySet()){
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
		}
		for(Entry<PermissionType, List<PermissionTarget>> e : permissionTypes.entrySet()){
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < e.getValue().size(); i++){
				if(e.getValue().get(i).getType() == TargetType.PLAYER){
					sb.append("" + ChatColor.GREEN + ((PlayerTarget)e.getValue().get(i)).getPlayer().getName().toString());
					if(i < e.getValue().size() - 1){
						sb.append("\n");
					}
				} else if (e.getValue().get(i).getType() == TargetType.RANK){
					sb.append("" + ChatColor.GOLD + ((Rank) e.getValue().get(i)).getName().toString());
					if(i < e.getValue().size() - 1){
						sb.append("\n");
					}
				} else {
					sb.append("" + e.getValue().get(i).getType().toString());
					if(i < e.getValue().size() - 1){
						sb.append("\n");
					}
				}
			}
			fm.then(e.getKey().toString() + " ").tooltip(sb.toString());
		}
		return fm;
	}
}
