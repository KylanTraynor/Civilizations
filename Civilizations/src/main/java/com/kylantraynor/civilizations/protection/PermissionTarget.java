package com.kylantraynor.civilizations.protection;

public class PermissionTarget {
	
	TargetType type;
	public TargetType getType(){
		return type;
	}
	
	public PermissionTarget(TargetType t){
		this.type = t;
	}
}