package com.kylantraynor.civilizations.protection;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Permission {
	Group protectedEntity;
	private Map<PermissionType, Boolean> types = new HashMap<PermissionType, Boolean>();
	PermissionTarget target;
	String targetId = "";
	
	public Permission(Group protectedEntity, PermissionTarget target, String id, Map<PermissionType, Boolean> types){
		this.protectedEntity = protectedEntity;
		this.target = target;
		this.targetId = id;
		if(types != null) this.setTypes(types);
	}
	
	public boolean set(PermissionType type, boolean bool){
		if(getTypes().containsKey(type)){
			if(getTypes().get(type) != bool){
				getTypes().remove(type);
				getTypes().put(type, bool);
			}
		} else {
			getTypes().put(type, bool);
		}
		return true;
	}
	
	public PermissionTarget getTargetType(){return target;}
	public String getTargetId(){return targetId;}
	public boolean isTarget(PermissionTarget targetType, String id){
		if(this.target == targetType){
			if(this.targetId == null && id == null){
				return true;
			} else {
				if(this.targetId.equals(id)){return true;
				} else { return false;}
			}
		} else {return false;}
	}
	public boolean setTarget(PermissionTarget target){
		this.target = target;
		return true;
	}
	
	public boolean check(PermissionType type, Object request){
		if(isTargetMatching(request)){
			// Needs to check when permissions are valid
		}
		return false;
	}
	
	public boolean isTargetMatching(Object o){
		switch(this.target.getType()){
		case MEMBERS:
			// Permission checking if a player is part of the settlement
			if(o instanceof Player){
				if(protectedEntity.isMember((Player) o)) return true;
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
			if(o instanceof Player){
				return protectedEntity.hasRank(targetId, (Player) o);
			}
		default:
		}
		return false;
	}

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
		return types.containsKey(type);
	}

	public boolean get(PermissionType type) {
		return types.get(type);
	}
	
	
}