package com.kylantraynor.civilizations.selection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;

public class SelectionManager {
	static private Map<UUID, Shape> selections;
	static private Map<UUID, Location> primaryPoints;
	static private Map<UUID, Location> secondaryPoints;
	
	public static void init() {
		selections = new HashMap<UUID, Shape>();
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
		Location point1 = primaryPoints.get(id);
		Location point2 = secondaryPoints.get(id);
		
		int minX = Math.min(point1.getBlockX(), point2.getBlockX());
		int minY = Math.min(point1.getBlockY(), point2.getBlockY());
		int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
		int width = (int) Math.abs(point2.getBlockX() - point1.getBlockX());
		int height = (int) Math.abs(point2.getBlockY() - point1.getBlockY());
		int length = (int) Math.abs(point2.getBlockZ() - point1.getBlockZ());
		
		Location firstCorner = new Location(point1.getWorld(), minX, minY, minZ);
		selections.put(id, new Prism(firstCorner, width, height, length));
	}
	
	public static Shape getSelection(Player p){
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
