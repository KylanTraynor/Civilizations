package com.kylantraynor.civilizations.protection;

public class PermissionTarget {
	
	TargetType type;
	public TargetType getType(){
		return type;
	}
	
	public PermissionTarget(TargetType t){
		this.type = t;
	}
	
	@Override
	public boolean equals(Object pt){
		if(!(pt instanceof PermissionTarget)) return false;
		if(((PermissionTarget) pt).getType().equals(this.getType())){
			return true;
		}
		return false;
	}
}