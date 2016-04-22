package com.kylantraynor.civilizations.hook.dynmap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;

public class DynmapHook {
	private static Plugin plugin;
	private static DynmapAPI api;
	private static MarkerAPI markerAPI;
	private static boolean reload;
	private static MarkerSet campMarkerSet;
	private static MarkerSet stallsMarkerSet;
	private static HashMap<String, Marker> markerList = new HashMap<String, Marker>();
	/**
	 * Tries to load the dynmap plugin. Returns true if successfully loaded, returns false otherwise.
	 * @param manager
	 * @return boolean
	 */
	public static boolean load(PluginManager manager) {
		if((plugin = manager.getPlugin("dynmap")) != null){
			api = (DynmapAPI)plugin;
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the state of the Dynmap hook. Returns true if the plugin is loaded and enabled, returns false otherwise.
	 * @return boolean
	 */
	public static boolean isEnabled(){
		if(plugin != null){
			return plugin.isEnabled();
		} else {
			return false;
		}
	}
	
	/**
	 * Activates the Dynmap for Civilizations
	 */
	public static void activateDynmap() {
		try{
			markerAPI = api.getMarkerAPI();
			if (markerAPI == null) {
				Civilizations.log("SEVERE", "Error loading dynmap marker API!");
				return;
		    }
			if (reload){
				Civilizations.currentInstance.reloadConfig();
				if (campMarkerSet != null){
					campMarkerSet.deleteMarkerSet();
					campMarkerSet = null;
				}
				if (stallsMarkerSet != null){
					stallsMarkerSet.deleteMarkerSet();
					stallsMarkerSet = null;
				}
		    } else {
		    	reload = true;
		    }
			loadCampMarkerSet();
			loadStallsMarkerSet();
		} catch (Exception e) {
			Civilizations.log("SEVERE", "Something went wrong activating Dynmap for Civilizations. Is it up to date?");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the marker set for Camps
	 */
	private static void loadCampMarkerSet() {
		if(!isEnabled()) return;
		campMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.camps");
		if (campMarkerSet == null) {
			campMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.camps", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Name", "Camps"), null, false);
		} else {
			campMarkerSet.setMarkerSetLabel(Civilizations.getInstanceConfig().getString("Dynmap.Layer.Camp.Name", "Camps"));
		}
		if (campMarkerSet == null){
			Civilizations.log("SEVERE", "Error creating marker set");
			return;
		}
		int minzoom = Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Camp.MinZoom", 5);
		if (minzoom > 0) {
		   campMarkerSet.setMinZoom(minzoom);
		}
		campMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Camp.LayerPrio", 10));
	    campMarkerSet.setHideByDefault(Civilizations.getInstanceConfig().getBoolean("Dynmap.Layer.Camp.HideByDefault", false));
	}
	
	/**
	 * Loads the marker set for Stalls
	 */
	private static void loadStallsMarkerSet() {
		if(!isEnabled()) return;
		stallsMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.stalls");
		if (stallsMarkerSet == null) {
			stallsMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.stalls", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Name", "Market Stalls"), null, false);
		} else {
			stallsMarkerSet.setMarkerSetLabel(Civilizations.getInstanceConfig().getString("Dynmap.Layer.Stalls.Name", "Market Stalls"));
		}
		if (stallsMarkerSet == null){
			Civilizations.log("SEVERE", "Error creating marker set");
			return;
		}
		int minzoom = Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Stalls.MinZoom", 5);
		if (minzoom > 0) {
		   stallsMarkerSet.setMinZoom(minzoom);
		}
		stallsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Stalls.LayerPrio", 10));
	    stallsMarkerSet.setHideByDefault(Civilizations.getInstanceConfig().getBoolean("Dynmap.Layer.Stalls.HideByDefault", false));
	}
	
	/**
	 * Update a settlement on the dynmap.
	 * @param settlement to update.
	 */
	public static void updateMap(Group group){
		if(!DynmapHook.isEnabled()) return;
		Civilizations.DEBUG("Updating Dynmap for " + group.getName());
		if(group instanceof Camp){
			Camp c = (Camp) group;
			DynmapHook.updateCamp(c);
		} else if (group instanceof MarketStall){
			MarketStall m = (MarketStall) group;
			DynmapHook.updateStall(m);
		}
	}
	
	/**
	 * Updates the display of the given Stall.
	 * @param c
	 */
	private static void updateStall(MarketStall m) {
		Civilizations.DEBUG("Updating Dynmap for " + m.getName() + " as a stall.");
		String id = "" + m.getProtection().getCenter().getBlockX() + "_" +
				m.getProtection().getCenter().getBlockY() + "_" +
				m.getProtection().getCenter().getBlockZ() + "_stall";
		String stallMarker = m.getIcon();
		MarkerIcon stallIcon = null;
	    if (stallMarker != null)
	    {
	    	stallIcon = markerAPI.getMarkerIcon(stallMarker);
	        if (stallIcon == null)
	        {
	          Civilizations.log("INFO", "Invalid StallIcon: " + stallMarker);
	          stallIcon = markerAPI.getMarkerIcon("scales");
	        }
	    }
	    if(stallIcon != null){
	    	Marker stall = markerList.remove(id);
	    	if (stall == null){
	    		stall = stallsMarkerSet.createMarker(id, m.getName(), m.getProtection().getCenter().getWorld().getName(),
	    				m.getProtection().getCenter().getBlockX(),
	    				m.getProtection().getCenter().getBlockY(),
	    				m.getProtection().getCenter().getBlockZ(), stallIcon, false);
	    	} else {
	    		stall.setLocation(m.getProtection().getCenter().getWorld().getName(),
	    				m.getProtection().getCenter().getBlockX(),
	    				m.getProtection().getCenter().getBlockY(),
	    				m.getProtection().getCenter().getBlockZ());
	            stall.setLabel(m.getName());
	            stall.setMarkerIcon(stallIcon);
	    	}
	    	StringBuilder sb = new StringBuilder();
	    	if(m.isForRent()){
	    		if(m.getRenter() == null){
		    		sb.append("Available For Rent" + "\n");
		    	} else {
		    		sb.append(m.getRenter().getName() + "\n");
		    	}
	    		sb.append("\nRent: " + Economy.format(m.getRent()));
	    		sb.append("\n\nWares: ");
	    		sb.append("Unknown");
	    	}
	    	stall.setDescription(sb.toString());
	    	markerList.put(id, stall);
	    }
	}

	/**
	 * Updates the display of the given Camp.
	 * @param c
	 */
	public static void updateCamp(Camp c){
		String id = "" + c.getLocation().getBlockX() + "_" +
				c.getLocation().getBlockY() + "_" +
				c.getLocation().getBlockZ() + "_camp";
		String campMarker = c.getIcon();
		MarkerIcon campIcon = null;
	    if (campMarker != null)
	    {
	    	campIcon = markerAPI.getMarkerIcon(campMarker);
	        if (campIcon == null)
	        {
	          Civilizations.log("INFO", "Invalid CampIcon: " + campMarker);
	          campIcon = markerAPI.getMarkerIcon("blueicon");
	        }
	    }
	    if(campIcon != null){
	    	Marker camp = markerList.remove(id);
	    	if (camp == null){
	    		camp = campMarkerSet.createMarker(id, "Camp", c.getLocation().getWorld().getName(), c.getLocation().getBlockX(), c.getLocation().getBlockY(), c.getLocation().getBlockZ(), campIcon, false);
	    	} else {
	    		camp.setLocation(c.getLocation().getWorld().getName(),
	    				c.getLocation().getBlockX(),
	    				c.getLocation().getBlockY(),
	    				c.getLocation().getBlockZ());
	            camp.setLabel("Camp");
	            camp.setMarkerIcon(campIcon);
	    	}
	    	StringBuilder sb = new StringBuilder();
	    	for(UUID uid : c.getMembers()){
	    		sb.append(Bukkit.getServer().getOfflinePlayer(uid).getName() + "\n");
	    	}
	    	camp.setDescription("Expire in " + ChronoUnit.HOURS.between(Instant.now(), c.getExpireOn()) + " hours."
	    			+ "\nMembers: " + sb.toString());
	    	markerList.put(id, camp);
	    }
	}
	
	/**
	 * Clear the Hook and disables it.
	 */
	public static void disable() {
		markerList.clear();
		markerList = null;
		campMarkerSet = null;
	}
}
