package com.kylantraynor.civilizations;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.achievements.Achievement;
import com.kylantraynor.civilizations.groups.House;

public class PlayerData {
	
	private static Map<UUID, PlayerData> all = new HashMap<UUID, PlayerData>();

	public static PlayerData get(UUID id){
		if(all.containsKey(id)){
			return all.get(id);
		} else {
			new PlayerData(id);
			return all.get(id);
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
			all.put(id, this);
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
		if(Math.random() < 0.01){
			takeSkillExperience("Alcohol", 1);
			takeSkillExperience("Lock Picking", 1);
		}
		if(hasChanged){
			try{save();} catch (Exception e){e.printStackTrace();}
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
		return Math.max((int) (Math.floor(Math.sqrt(exp) / 10.0)), 1);
	}
	
	public int getSkillExperience(String skill){
		if(config.contains("Skills." + skill)){
			return Math.max(config.getInt("Skills." + skill), 1);
		} else {
			return 1;
		}
	}
	
	public void giveSkillExperience(String skill, int amount){
		int oldLevel = getSkillLevel(skill);
		int currentExp = getSkillExperience(skill);
		config.set("Skills." + skill, Math.max(currentExp + amount, 1));
		hasChanged = true;
		if(oldLevel != getSkillLevel(skill)){
			Player p = Bukkit.getPlayer(playerId);
			if(p != null){
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				p.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + skill + ChatColor.GOLD + "] Skill leveled up!");
			}
		}
	}
	
	public int getSkillExperienceForLevel(int level){
		return (int) ((level * 10.0) * (level * 10.0));
	}
	
	public int getSkillExpToNextLevel(String skill){
		int level = getSkillLevel(skill);
		if(level <= 0) return 1;
		return getSkillExperienceForLevel(level + 1) - getSkillExperienceForLevel(level);
	}
	
	public int getSkillExpToNextLevel(int level){
		if(level <= 0) return 1;
		return getSkillExperienceForLevel(level + 1) - getSkillExperienceForLevel(level);
	}
	
	public int getSkillLevelExp(String skill){
		return getSkillExperience(skill) - getSkillExperienceForLevel(getSkillLevel(skill));
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
	
	public String getName(){
		if(this.config.contains("General.Name")){
			return this.config.getString("General.Name");
		}
		return null;
	}
	
	public void setName(String newName){
		this.config.set("General.Name", newName);
	}
	
	public String getSurname(){
		House h = House.get(Bukkit.getOfflinePlayer(playerId));
		if(h != null){
			return h.getName();
		} else {
			if(this.config.contains("General.Surname")){
				return this.config.getString("General.Surname");
			}
		}
		return null;
	}
	
	public int getCampsCreated(){
		if(this.config.contains("General.Stats.CampsCreated")){
			return this.config.getInt("General.Stats.CampsCreated");
		}
		return 0;
	}
	
	public void setCampsCreated(int newCount){
		this.config.set("General.Stats.CampsCreated", newCount);
		hasChanged = true;
	}
	
	public boolean hasAchievement(Achievement a){
		return this.config.contains("Achievements." + a.getId());
	}
	
	public boolean giveAchievement(Achievement a){
		if(!hasAchievement(a)){
			this.config.set("Achievements." + a.getId(), Instant.now().toString());
			hasChanged = true;
			return true;
		} else {
			return false;
		}
	}
}
