package com.kylantraynor.civilizations.achievements;

import java.util.HashMap;
import java.util.Map;

import mkremins.fanciful.FancyMessage;

import org.bukkit.entity.Player;

public class AchievementManager {
	
	public static Map<String, Achievement> achievements = new HashMap<String, Achievement>();
	
	public static boolean registerAchievement(Achievement achievement){
		if(achievements.containsKey(achievement.getId())){
			return false;
		} else {
			achievements.put(achievement.getId(), achievement);
			return true;
		}
	}
	
	public static void broadcast(Player player, Achievement achievement){
		FancyMessage fm = new FancyMessage("");
	}
	
}
