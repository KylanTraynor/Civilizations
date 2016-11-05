package com.kylantraynor.civilizations.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;

public class SelectionManager {
	static private Map<UUID, Selection> selections;
	static private Map<UUID, Location> primaryPoints;
	static private Map<UUID, Location> secondaryPoints;
	
	public static void init() {
		selections = new HashMap<UUID, Selection>();
		primaryPoints = new HashMap<UUID, Location>();
		secondaryPoints = new HashMap<UUID, Location>();
	}
	
	public static void free() {
		selections.clear();
		selections = null;
		primaryPoints.clear();
		primaryPoints = null;
		secondaryPoints.clear();
		secondaryPoints = null;
	}
	
	public static void setPrimary(Player p, Location l){
		primaryPoints.put(p.getUniqueId(), l);
		
		updateSelection(p);
	}
	
	public static void setSecondary(Player p, Location l){
		secondaryPoints.put(p.getUniqueId(), l);
		
		updateSelection(p);
	}

	private static void updateSelection(Player p) {
		UUID id = p.getUniqueId();
		if(!primaryPoints.containsKey(id)){
			return;
		}
		if(!secondaryPoints.containsKey(id)){
			return;
		}
		selections.put(id, new Selection(primaryPoints.get(id), secondaryPoints.get(id)));
	}
	
	public static Selection getSelection(Player p){
		if(hasSelection(p)){
			return selections.get(p.getUniqueId());
		}
		return null;
	}
	
	public static boolean hasSelection(Player p){
		if(selections == null) return false;
		return selections.containsKey(p.getUniqueId());
	}

	public static void clear(Player player) {
		if(primaryPoints.containsKey(player.getUniqueId())){
			primaryPoints.remove(player.getUniqueId());
		}
		if(secondaryPoints.containsKey(player.getUniqueId())){
			secondaryPoints.remove(player.getUniqueId());
		}
		if(selections.containsKey(player.getUniqueId())){
			selections.remove(player.getUniqueId());
		}
	}
}
