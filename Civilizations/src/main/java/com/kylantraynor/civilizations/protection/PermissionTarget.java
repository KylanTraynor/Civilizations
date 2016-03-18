package com.kylantraynor.civilizations.protection;

public class PermissionTarget {
	public enum Type{
		MEMBERS,
		ALLIES,
		NEUTRAL,
		ENEMIES,
		SETTLEMENT,
		NATION,
		OUTSIDERS,
		PLAYER,
		WORLD,
		SERVER,
		RANK
	}
	
	Type type;
	public Type getType(){
		return type;
	}
	
	public PermissionTarget(Type t){
		this.type = t;
	}
}