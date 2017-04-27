package com.kylantraynor.civilizations.hook.dynmap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.NationMember;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.territories.InfluentSite;
import com.kylantraynor.civilizations.territories.Region;
import com.kylantraynor.civilizations.util.Util;
import com.kylantraynor.voronoi.VCell;

public class DynmapHook {
	private static Plugin plugin;
	private static DynmapAPI api;
	private static MarkerAPI markerAPI;
	private static boolean reload;
	private static MarkerSet campMarkerSet;
	private static MarkerSet stallsMarkerSet;
	private static MarkerSet regionsMarkerSet;
	private static HashMap<String, Marker> markerList = new HashMap<String, Marker>();
	private static MarkerSet settlementsMarkerSet;
	/**
	 * Tries to load the dynmap plugin. Returns true if successfully loaded, returns false otherwise.
	 * @param manager
	 * @return boolean
	 */
	public boolean load(PluginManager manager) {
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
	public void activateDynmap() {
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
				if(settlementsMarkerSet != null){
					settlementsMarkerSet.deleteMarkerSet();
					settlementsMarkerSet = null;
				}
				if (stallsMarkerSet != null){
					stallsMarkerSet.deleteMarkerSet();
					stallsMarkerSet = null;
				}
				if(regionsMarkerSet != null){
					regionsMarkerSet.deleteMarkerSet();
					regionsMarkerSet = null;
				}
		    } else {
		    	reload = true;
		    }
			loadCampMarkerSet();
			loadStallsMarkerSet();
			loadRegionsMarkerSet();
			loadSettlementsMarkerSet();
		} catch (Exception e) {
			Civilizations.log("SEVERE", "Something went wrong activating Dynmap for Civilizations. Is it up to date?");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Loads the marker set for Regions.
	 */
	private static void loadRegionsMarkerSet() {
		if(!isEnabled()) return;
		regionsMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.regions");
		if(regionsMarkerSet == null)
			regionsMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.regions", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Regions.Name", "Regions"), null, false);
		else
			regionsMarkerSet.setMarkerSetLabel(Civilizations.getInstanceConfig().getString("Dynmap.Layer.Regions.Name", "Regions"));
		if(regionsMarkerSet == null){
			Civilizations.log("SEVERE", "Error creating Regions MarkerSet.");
			return;
		}
		int minZoom = Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Regions.MinZoom", 0);
		if(minZoom > 0){
			regionsMarkerSet.setMinZoom(minZoom);
		}
		regionsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Regions.LayerPrio", 50));
		regionsMarkerSet.setHideByDefault(Civilizations.getInstanceConfig().getBoolean("Dynmap.Layer.Regions.HideByDefault", false));
	}
	
	/**
	 * Loads the marker set for Settlements.
	 */
	private static void loadSettlementsMarkerSet() {
		if(!isEnabled()) return;
		settlementsMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.settlements");
		if(settlementsMarkerSet == null)
			settlementsMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.settlements", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Settlements.Name", "Settlements"), null, false);
		else
			settlementsMarkerSet.setMarkerSetLabel(Civilizations.getInstanceConfig().getString("Dynmap.Layer.Settlements.Name", "Settlements"));
		if(settlementsMarkerSet == null){
			Civilizations.log("SEVERE", "Error creating Settlements MarkerSet.");
			return;
		}
		int minZoom = Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Settlements.MinZoom", 0);
		if(minZoom > 0){
			settlementsMarkerSet.setMinZoom(minZoom);
		}
		settlementsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Settlements.LayerPrio", 9));
		settlementsMarkerSet.setHideByDefault(Civilizations.getInstanceConfig().getBoolean("Dynmap.Layer.Settlements.HideByDefault", false));
	}

	/**
	 * Loads the marker set for Camps
	 */
	private static void loadCampMarkerSet() {
		if(!isEnabled()) return;
		campMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.camps");
		if (campMarkerSet == null) {
			campMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.camps", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Camp.Name", "Camps"), null, false);
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
		campMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Camp.LayerPrio", 8));
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
		stallsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Stalls.LayerPrio", 1));
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
		} else if (group instanceof Settlement){
			Settlement s = (Settlement) group;
			DynmapHook.updateSettlement(s);
		} else if (group instanceof MarketStall){
			MarketStall m = (MarketStall) group;
			DynmapHook.updateStall(m);
		}
	}
	
	private static void updateSettlement(Settlement s) {
		if(s.getProtection().getHull() == null) return;
		if(!s.getProtection().getHull().exists()) return;
		String id = "" + s.getLocation().getBlockX() + "_" + s.getLocation().getBlockY() + "_" + s.getLocation().getBlockZ() + "_settlement";
		AreaMarker m = settlementsMarkerSet.createAreaMarker(id, Util.prettifyText(s.getName()), false, s.getLocation().getWorld().getName(), s.getProtection().getHull().getVerticesX(), s.getProtection().getHull().getVerticesZ(), false);
		if(m == null){
			m = settlementsMarkerSet.findAreaMarker(id);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
		}
		m.setLabel(Util.prettifyText(s.getName()));
		if(s instanceof NationMember){
			if(((NationMember) s).getNation() != null){
				if(((NationMember) s).getNation().getBanner() != null){
					m.setFillStyle(0.1, ((NationMember) s).getNation().getBanner().getBaseColor().getColor().asRGB());
					m.setLineStyle(2,  1, ((NationMember) s).getNation().getBanner().getBaseColor().getColor().asRGB());
				}
			} else {
				m.setFillStyle(0.1, 0x999999);
				m.setLineStyle(2 ,1, 0x999999);
			}
		} else {
			m.setFillStyle(0.1, 0x999999);
			m.setLineStyle(2 ,1, 0x999999);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<h1>"+s.getName()+"</h1><br />");
		if(Civilizations.getSettings().getWikiUrl() != null){
			sb.append("<a href=\"");
			sb.append(Civilizations.getSettings().getWikiUrl());
			sb.append(s.getName().replace(" ", "_"));
			sb.append("\" target=\"_blank\">Wiki</a><br />");
		}
		sb.append("Area: " + s.getProtection().getHull().getArea() + "m²");
		m.setDescription(sb.toString());
	}

	/**
	 * Updates the display of the given Stall.
	 * @param c
	 */
	private static void updateStall(MarketStall m) {
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
	    	String description = Civilizations.getInstanceConfig().getString("Dynmap.Layer.Stalls.InfoBubble", "%Name%");
	    	description = "<div class=\"regioninfo\">" + description + "</div>";
	    	description = description.replace("%Name%", m.getName());
	    	StringBuilder sb = new StringBuilder();
	    	if(m.isForRent()){
	    		if(m.getRenter() == null){
		    		sb.append("Available For Rent");
		    	} else {
		    		sb.append(m.getRenter().getName());
		    	}
	    	} else {
	    		if(m.getOwner() != null){
	    			sb.append(m.getOwner().getName());
	    		} else {
	    			if(m.getSettlement() != null){
	    				sb.append(m.getSettlement().getName());
	    			}
	    		}
	    	}
	    	String rent = m.isForRent() ? Economy.format(m.getRent()) : "Not for rent";
	    	description = description.replace("%RentStatus%", sb.toString());
	    	description = description.replace("%Rent%", rent);
	    	// Taxes
	    	String taxes = "Transaction Taxes:<ul>";
	    	if(m.getSettlement() != null){
	    		taxes += "<li>" + m.getSettlement().getName() + ": " + (m.getSettlement().getSettings().getTransactionTax() * 100) + "%</li>";
	    	}
	    	/*Fort f = InfluenceMap.getInfluentFortAt(m.getProtection().getCenter());
	    	if(f != null){
	    		taxes += "<li>" + f.getName() + ": " + (f.getSettings().getTransactionTax() * 100) + "%</li>";
	    	}*/
	    	taxes += "</ul>";
	    	description = description.replace("%Taxes%", taxes);
	    	// Wares
	    	StringBuilder sb1 = new StringBuilder();
	    	sb1.append("<ul>");
	    	for(String s : m.getWaresToString()){
	    		sb1.append("<li>" + s + "</li>");
	    	}
    		sb1.append("</ul>");
    		description = description.replace("%WaresList%", sb1.toString());
	    	stall.setDescription(description);
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
	    		camp = campMarkerSet.createMarker(id, c.getName(), c.getLocation().getWorld().getName(), c.getLocation().getBlockX(), c.getLocation().getBlockY(), c.getLocation().getBlockZ(), campIcon, false);
	    	} else {
	    		camp.setLocation(c.getLocation().getWorld().getName(),
	    				c.getLocation().getBlockX(),
	    				c.getLocation().getBlockY(),
	    				c.getLocation().getBlockZ());
	            camp.setLabel(c.getName());
	            camp.setMarkerIcon(campIcon);
	    	}
	    	StringBuilder sb = new StringBuilder();
	    	for(UUID uid : c.getMembers()){
	    		sb.append("<br />" + Bukkit.getServer().getOfflinePlayer(uid).getName());
	    	}
	    	camp.setDescription("Expire in " + ChronoUnit.HOURS.between(Instant.now(), c.getExpireOn()) + " hours."
	    			+ "<br />Members: " + sb.toString());
	    	markerList.put(id, camp);
	    }
	}
	
	public static void updateInfluenceMap(InfluenceMap influenceMap) {
		for(InfluentSite c : influenceMap.getSites())
			updateRegion(influenceMap, c.getRegion());
	}
	
	public static void updateRegion(InfluenceMap influenceMap, Region region){
		String polyID = "region_";
		polyID = polyID + region.getSite().getX() + "_" + region.getSite().getZ();
		AreaMarker m = regionsMarkerSet.createAreaMarker(polyID, Util.prettifyText(region.getName()), false, influenceMap.getWorld().getName(), region.getCell(influenceMap).getVerticesX(), region.getCell(influenceMap).getVerticesZ(), false);
		if(m == null){
			m = regionsMarkerSet.findAreaMarker(polyID);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
		}
		m.setLabel(Util.prettifyText(region.getName()));
		if(region.getNation() != null){
			if(region.getNation().getBanner() != null){
				m.setFillStyle(0.1, region.getNation().getBanner().getBaseColor().getColor().asRGB());
				m.setLineStyle(2,  1, region.getNation().getBanner().getBaseColor().getColor().asRGB());
			}
		} else {
			m.setFillStyle(0.1, 0x999999);
			m.setLineStyle(1 ,1, 0x999999);
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
