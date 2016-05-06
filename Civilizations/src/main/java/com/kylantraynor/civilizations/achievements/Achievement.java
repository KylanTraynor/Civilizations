package com.kylantraynor.civilizations.achievements;

import java.util.List;

public class Achievement {
	private String id;
	private String name;
	private Achievement requirement;
	private List<String> description;
	
	public Achievement(String id, String name, Achievement requirement, List<String> description){
		this.id = id;
		this.name = name;
		this.requirement = requirement;
		this.description = description;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Achievement getRequirement(){
		return requirement;
	}
	
	public List<String> getDescription(){
		return description;
	}
}
