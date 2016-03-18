package com.kylantraynor.civilizations.protection;

public class Rank extends PermissionTarget{
	private String name = "";
	private Rank parent;
	
	public Rank(String name, Rank parent){
		super(PermissionTarget.Type.RANK);
		this.name = name;
		this.parent = parent;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Rank getParent(){ return parent; }
	public void setParent(Rank parent) { this.parent = parent; }
}
