package com.kylantraynor.civilizations.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Permissions {
	private Map<PermissionType, Boolean> types = new HashMap<PermissionType, Boolean>();
	PermissionTarget target;
	
	public Permissions(Map<PermissionType, Boolean> types){
		if(types != null) this.setTypes(types);
	}
	
	public boolean set(PermissionType type, Boolean bool){
		if(bool == null){
			getTypes().remove(type);
			return true;
		}
		getTypes().put(type, bool);
		return true;
	}
	
	public PermissionTarget getTargetType(){return target;}
	public boolean isTarget(PermissionTarget targetType, String id){
		if(target.equals(targetType)){
			return true;
		} else {return false;}
	}
	public boolean setTarget(PermissionTarget target){
		this.target = target;
		return true;
	}
	/*
	public boolean check(PermissionType type, Object request){
		if(isTargetMatching(request)){
			// Needs to check when permissions are valid
		}
		return false;
	}
	
	public boolean isTargetMatching(Object o){
		switch(this.target.getType()){
		case GROUP:
			// Permission checking if a player is part of the group
			if(o instanceof Player){
				if(((GroupTarget)target).isPartOf((Player) o)) return true;
			}
			break;
		case OUTSIDERS:
			// Permission checking if a player isn't part of the settlement
			if(o instanceof Player){
				if(!protectedEntity.isMember((Player) o)) return true;
			}
			break;
		case ALLIES:
			if(o instanceof Player){
				return true;
			}
			break;
		case NEUTRAL:
			if(o instanceof Player){
				return true;
			}
			break;
		case ENEMIES:
			if(o instanceof Player){
				return true;
			}
			break;
		case SETTLEMENT:
			if(o instanceof Player) return true;
			if(o instanceof Settlement) return true;
			break;
		case NATION:
			if(o instanceof Player) return true;
			if(o instanceof Settlement) return true;
			//if(o instanceof Nation) return true;
			break;
		case PLAYER:
			if(o instanceof Player) return true;
			break;
		case WORLD:
			if(o instanceof World) return true;
			break;
		case SERVER:
			return true;
		case RANK:
			return true;
		default:
		}
		return false;
	}
	*/
	
	public Map<PermissionType, Boolean> getTypes() {
		return types;
	}
	
	public boolean hasType(PermissionType type){
		return types.containsKey(type);
	}

	public void setTypes(Map<PermissionType, Boolean> types) {
		this.types = types;
	}

	public boolean contains(PermissionType type) {
		return types.containsKey(type) ?
				(types.get(type) == null ?
						false :
						types.containsKey(type)) :
				false;
	}

	public boolean get(PermissionType type) {
		return types.get(type);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("");
		for(Entry<PermissionType, Boolean> e : types.entrySet()){
			if(e.getValue()){
				sb.append(" " + ChatColor.GREEN + e.getKey().toString() + "");
			} else {
				sb.append(" " + ChatColor.RED + e.getKey().toString() + "");
			}
		}
		return sb.toString();
	}

	public Object getTypesAsString() {
		Map<String, Boolean> output = new HashMap<String, Boolean>();
		for(Entry<PermissionType, Boolean> e : getTypes().entrySet()){
			output.put(e.getKey().toString(), e.getValue());
		}
		return output;
	}

	public void remove(PermissionType type) {
		types.remove(type);
	}
}