package com.kylantraynor.civilizations.settings;

import java.time.Instant;

import org.bukkit.configuration.file.YamlConfiguration;

public class GroupSettings extends YamlConfiguration{
	
	private Instant creationDate;

	/**
	 * Gets the creation date of the group.
	 * @return Instant
	 */
	public Instant getCreationDate() {
		if(creationDate != null) return creationDate;
		creationDate = Instant.now();
		if(this.contains("general.creationDate")){
			try{
				creationDate = Instant.parse(this.getString("general.creationDate"));
			} catch (Exception e){}
		}
		return creationDate;
	}
	
	/**
	 * Sets the creation date of the group.
	 * @param date
	 */
	public void setCreationDate(Instant date){
		creationDate = date == null ? Instant.now() : date;
		this.set("general.creationDate", creationDate.toString());
	}
	
	/**
	 * Gets the name of the group.
	 * @return String
	 */
	public String getName(){
		if(this.contains("general.name")){
			return this.getString("general.name");
		}
		return "Group";
	}
	
	/**
	 * Sets the name of the group.
	 * @param newName
	 */
	public void setName(String newName){
		if(newName == null) return;
		this.set("general.name", newName);
	}
}
