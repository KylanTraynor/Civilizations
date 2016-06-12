package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class GroupSettings extends YamlConfiguration{
	
	private Instant creationDate;
	private List<UUID> members;

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
	
	/**
	 * Gets the list of members of this group.
	 * @return List<UUID>
	 */
	public List<UUID> getMembers(){
		if(this.members == null) {
			this.members = new ArrayList<UUID>();
			if(this.contains("members")){
				List<?> l = this.getList("members");
				for(Object o : l){
					if(o instanceof String){
						this.members.add(UUID.fromString((String) o));
					}
				}
			}
		}
		return this.members;
	}
	
	/**
	 * Sets the list of members of this group.
	 * @param list
	 */
	public void setMembers(List<UUID> list){
		List<String> l = new ArrayList<String>();
		if(list != null){
			for(UUID id : list){
				l.add(id.toString());
			}
		}
		this.members = list;
		this.set("members", l);
	}
}
