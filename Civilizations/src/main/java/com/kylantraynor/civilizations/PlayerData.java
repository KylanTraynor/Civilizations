package com.kylantraynor.civilizations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerData {
	
	private static Map<UUID, PlayerData> all = new HashMap<UUID, PlayerData>();

	public static PlayerData get(UUID id){
		if(all.containsKey(id)){
			return all.get(id);
		} else {
			PlayerData pd = new PlayerData(id);
			all.put(id, pd);
			return pd;
		}
	}

	private UUID playerId;
	private YamlConfiguration config = new YamlConfiguration();
	private boolean hasChanged = true;
	
	public PlayerData(UUID id) {
		playerId = id;
		File f = getFile();
		try {
			config.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File getFile(){
		File f = new File(Civilizations.getPlayerDataDirectory(), playerId.toString() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
	
	public void update(){
		if(hasChanged){
			save();
		}
	}
	
	public void save(){
		File f = getFile();
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getLevelForExperience(int exp){
		return Math.max((int) (Math.floor(Math.sqrt(exp) / 5.0)), 1);
	}
	
	public int getSkillExperience(String skill){
		if(config.contains("Skills." + skill)){
			return Math.max(config.getInt("Skill." + skill), 1);
		} else {
			return 1;
		}
	}
	
	public void giveSkillExperience(String skill, int amount){
		config.set("Skills." + skill, Math.max(getSkillExperience(skill) + amount, 1));
		hasChanged = true;
	}
	
	public void takeSkillExperience(String skill, int amount){
		giveSkillExperience(skill, -amount);
	}

	public static void updateAll() {
		for(PlayerData pd : all.values()){
			pd.update();
		}
	}

	public int getSkillLevel(String skill) {
		return getLevelForExperience(getSkillExperience(skill));
	}
}
