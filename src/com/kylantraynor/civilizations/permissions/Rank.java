package com.kylantraynor.civilizations.permissions;

public class Rank {
	private String name = "";
	private String inherit = "";
	
	public Rank(String name, String inherit){
		this.name = name;
		this.inherit = name;
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getInherit() { return inherit; }
	public void setInherit(String inherit) { this.inherit = inherit; }
}
