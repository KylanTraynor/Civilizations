package com.kylantraynor.civilizations.settings;

import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class CivilizationsSettings extends YamlConfiguration {
	
	public UUID getBlueprintId(String name){
		if(this.contains("UUIDConversions.Blueprints." + name.toUpperCase())){
			try{
				return UUID.fromString(this.getString("UUIDConversions.Blueprints." + name.toUpperCase()));
			} catch (IllegalArgumentException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public void setBlueprintId(String name, UUID id){
		this.set("UUIDConversions.Blueprints." + name, id.toString());
	}
	
}
