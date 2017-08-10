package com.kylantraynor.civilizations.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.selection.HullSelection;
import com.kylantraynor.civilizations.selection.PrismSelection;
import com.kylantraynor.civilizations.selection.Selection;

public class SelectionManager {
	static private Map<UUID, Selection> selections;
	static private Map<UUID, List<Location>> points;
	static private Map<UUID, String> selectionMode;
	
	public static void init() {
		selections = new HashMap<UUID, Selection>();
		points = new HashMap<UUID, List<Location>>();
		selectionMode = new HashMap<UUID, String>();
	}
	
	public static void free() {
		selections.clear();
		selections = null;
		points.clear();
		points = null;
	}
	
	public static void addPoint(Player p, Location l){
		List<Location> locs = points.get(p.getUniqueId());
		if(locs == null){
			locs = new ArrayList<Location>();
			points.put(p.getUniqueId(), locs);
		}
		locs.add(l);
		updateSelection(p);
	}
	
	public static void setPrimary(Player p, Location l){
		List<Location> locs = points.get(p.getUniqueId());
		if(locs == null){
			locs = new ArrayList<Location>();
			points.put(p.getUniqueId(), locs);
		}
		if(locs.size() > 0){
			locs.set(0, l);
		} else {
			locs.add(l);
		}
		updateSelection(p);
	}
	
	public static void setSecondary(Player p, Location l){
		List<Location> locs = points.get(p.getUniqueId());
		if(locs == null){
			locs = new ArrayList<Location>();
			points.put(p.getUniqueId(), locs);
		}
		if(locs.size() > 1){
			locs.set(1, l);
		} else {
			if(locs.size() == 0){
				locs.add(null);
			}
			locs.add(l);
		}
		updateSelection(p);
	}

	private static void updateSelection(Player p) {
		UUID id = p.getUniqueId();
		List<Location> locs = points.get(id);
		if(locs == null){
			selections.remove(id);
			return;
		}
		if(locs.size() >= 2 && getSelectionMode(p).equals("PRISM")){
			if(locs.get(0) != null && locs.get(1) != null){
				selections.put(id, new PrismSelection(locs.get(0), locs.get(1)));
			}
		} else if(locs.size() >= 2 && getSelectionMode(p).equals("HULL")){
			HullSelection hull = new HullSelection();
			for(Location l : locs){
				hull.addBlock(l.getBlock());
			}
			selections.put(id, hull);
		} else {
			selections.remove(id);
		}
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
		if(selections.containsKey(player.getUniqueId())){
			selections.remove(player.getUniqueId());
		}
		if(points.containsKey(player.getUniqueId())){
			points.remove(player.getUniqueId());
		}
		if(selectionMode.containsKey(player.getUniqueId())){
			selectionMode.remove(player.getUniqueId());
		}
	}

	public static void startHullSelection(Player player) {
		selectionMode.put(player.getUniqueId(), "HULL");
	}
	
	public static void stopHullSelection(Player player){
		selectionMode.remove(player.getUniqueId());
	}

	public static String getSelectionMode(Player player) {
		String s = selectionMode.get(player.getUniqueId());
		if(s == null){
			return "PRISM";
		} else
			return s;
	}
	
	public static List<Location> getPoints(Player player){
		List<Location> locs = points.get(player.getUniqueId());
		if(locs == null){
			return new ArrayList<Location>();
		} else {
			return locs;
		}
	}

	public static boolean removeLastPoint(Player player) {
		List<Location> locs = points.get(player.getUniqueId());
		if(locs == null){
			updateSelection(player);
			return false;
		} else{
			if(locs.size() > 0){
				if(locs.size() > 1){
					locs.remove(locs.size() - 1);
					updateSelection(player);
					return true;
				} else {
					points.remove(player.getUniqueId());
					updateSelection(player);
					return true;
				}
			}
		}
		updateSelection(player);
		return false;
	}
}
