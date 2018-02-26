package com.kylantraynor.civilizations.protection;

public class PermissionTarget {
	
	public static final PermissionTarget OUTSIDERS = new PermissionTarget(TargetType.OUTSIDERS);
	public static final PermissionTarget SERVER = new PermissionTarget(TargetType.SERVER);
	final private TargetType type;
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
	
	@Override
	public int hashCode(){
		int base = 11;
		return base * type.hashCode();
	}

	public String getName() {
		return type.toString();
	}
}