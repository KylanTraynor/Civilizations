package com.kylantraynor.civilizations.hook.dynmap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.NationMember;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.territories.InfluentSite;
import com.kylantraynor.civilizations.territories.Region;
import com.kylantraynor.civilizations.utils.Utils;

public class DynmapHook {
	private static Plugin plugin;
	private static DynmapAPI api;
	private static MarkerAPI markerAPI;
	private static boolean reload;
	private static MarkerSet campMarkerSet;
	private static MarkerSet plotsMarkerSet;
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
				if (plotsMarkerSet != null){
					plotsMarkerSet.deleteMarkerSet();
					plotsMarkerSet = null;
				}
				if(regionsMarkerSet != null){
					regionsMarkerSet.deleteMarkerSet();
					regionsMarkerSet = null;
				}
		    } else {
		    	reload = true;
		    }
			loadCampMarkerSet();
			loadPlotsMarkerSet();
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
		regionsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Regions.LayerPrio", 1));
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
	private static void loadPlotsMarkerSet() {
		if(!isEnabled()) return;
		plotsMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.plots");
		if (plotsMarkerSet == null) {
			plotsMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.plots", Civilizations.getInstanceConfig().getString("Dynmap.Layer.Plots.Name", "Plots"), null, false);
		} else {
			plotsMarkerSet.setMarkerSetLabel(Civilizations.getInstanceConfig().getString("Dynmap.Layer.Plots.Name", "Plots"));
		}
		if (plotsMarkerSet == null){
			Civilizations.log("SEVERE", "Error creating marker set");
			return;
		}
		int minzoom = Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Plots.MinZoom", 5);
		if (minzoom > 0) {
		   plotsMarkerSet.setMinZoom(minzoom);
		}
		plotsMarkerSet.setLayerPriority(Civilizations.getInstanceConfig().getInt("Dynmap.Layer.Plots.LayerPrio", 1));
	    plotsMarkerSet.setHideByDefault(Civilizations.getInstanceConfig().getBoolean("Dynmap.Layer.Plots.HideByDefault", false));
	}
	
	/**
	 * Update a settlement on the dynmap.
	 * @param group to update.
	 */
	public static void updateMap(Group group){
		if(!DynmapHook.isEnabled()) return;
		Civilizations.DEBUG("Updating Dynmap for " + group.getName());
		try{
			if(group instanceof Camp){
				Camp c = (Camp) group;
				DynmapHook.updateCamp(c);
			} else if (group instanceof Settlement){
				Settlement s = (Settlement) group;
				DynmapHook.updateSettlement(s);
			} else if (group instanceof Plot){
				Plot p = (Plot) group;
				DynmapHook.updatePlot(p);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static void updatePlot(Plot p) {
		switch(p.getPlotType()){
		case BANK:
			break;
		case SMITHY:
			break;
		case CONSTRUCTIONSITE:
			break;
		case CROPFIELD:
			updateField(p);
			break;
		case HOUSE:
			break;
		case KEEP:
			break;
		case MARKETSTALL:
			updateStall(p);
			break;
		case ROAD:
			break;
		case SHOP:
			break;
		case SHOPHOUSE:
			break;
		case TOWNHALL:
			break;
		case TOWNVAULT:
			break;
		case WAREHOUSE:
			break;
		case WOODCUTTER:
			break;
		default:
			break;
		
		}
	}

	private static void updateSettlement(Settlement s) {
		if(s.getHull() == null || !s.getHull().exists()) return;
		String id = s.getIdentifier().toString() + "_area";
		String icon_id = s.getIdentifier().toString() + "_icon";
		String icon = s.getIcon();
		AreaMarker m = settlementsMarkerSet.createAreaMarker(id, Utils.prettifyText(s.getName()), false, s.getLocation().getWorld().getName(), s.getHull().getVerticesX(), s.getHull().getVerticesZ(), false);
		MarkerIcon setIcon = null;
	    if (icon != null)
	    {
	    	setIcon = markerAPI.getMarkerIcon(icon);
	        if (setIcon == null)
	        {
	          Civilizations.log("INFO", "Invalid FieldIcon: " + icon);
	          setIcon = markerAPI.getMarkerIcon("sign");
	        }
	    }
		if(m == null){
			m = settlementsMarkerSet.findAreaMarker(id);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
			m.setCornerLocations(s.getHull().getVerticesX(), s.getHull().getVerticesZ());
		}
		if(setIcon != null){
	    	Marker set = markerList.remove(icon_id);
	    	if (set == null){
	    		set = settlementsMarkerSet.createMarker(id, Utils.prettifyText(s.getName()), s.getCenter().getWorld().getName(),
	    				s.getCenter().getBlockX(),
	    				s.getCenter().getBlockY(),
	    				s.getCenter().getBlockZ(), setIcon, false);
	    	} else {
	    		set.setLocation(s.getCenter().getWorld().getName(),
	    				s.getCenter().getBlockX(),
	    				s.getCenter().getBlockY(),
	    				s.getCenter().getBlockZ());
	            set.setLabel(Utils.prettifyText(s.getName()));
	            set.setMarkerIcon(setIcon);
	    	}
	    	StringBuilder sb = new StringBuilder();
    		sb.append("<h1>"+s.getName()+"</h1><br />");
    		if(Civilizations.getSettings().getWikiUrl() != null){
    			sb.append("<a href=\"");
    			sb.append(Civilizations.getSettings().getWikiUrl());
    			sb.append(s.getName().replace(" ", "_"));
    			sb.append("\" target=\"_blank\">Wiki</a><br />");
    		}
    		sb.append("<br/>Bank: " + Economy.format(s.getBalance()));
    		sb.append("<br/>Area: " + s.getHull().getArea() + "m�");
    		sb.append("<h2>Taxes: </h2><br />");
    		sb.append("Daily Server Land Tax: " + Economy.format(s.getNextTaxationAmount("Land")));
    		set.setDescription(sb.toString());
    		markerList.put(icon_id, set);
		}
		m.setLabel(Utils.prettifyText(s.getName()));
		if(s instanceof NationMember){
			if(((NationMember) s).getNation() != null){
				if(((NationMember) s).getNation().getBanner() != null){
					m.setFillStyle(0.1, Utils.getColor(((NationMember) s).getNation().getBanner().getBase()).getColor().asRGB());
					m.setLineStyle(2,  1, Utils.getColor(((NationMember) s).getNation().getBanner().getBase()).getColor().asRGB());
				}
			} else {
				m.setFillStyle(0.1, 0x999999);
				m.setLineStyle(2 ,1, 0x999999);
			}
		} else {
			m.setFillStyle(0.1, 0x999999);
			m.setLineStyle(2 ,1, 0x999999);
		}
	}

	private static void updateField(Plot p){
		String id = "" + p.getIdentifier().toString() + "_icon";
		String areaId = "" + p.getIdentifier().toString() + "_area";
		String icon = p.getIcon();
		MarkerIcon fieldMarker = null;
	    if (icon != null){
	    	fieldMarker = markerAPI.getMarkerIcon(icon);
	        if (fieldMarker == null)
	        {
	          Civilizations.log("INFO", "Invalid FieldIcon: " + icon);
	          fieldMarker = markerAPI.getMarkerIcon("sign");
	        }
	    }
	    AreaMarker m = plotsMarkerSet.createAreaMarker(areaId, Utils.prettifyText(p.getName()), false, p.getCenter().getWorld().getName(), p.getShapes().get(0).getVerticesX(), p.getShapes().get(0).getVerticesZ(), false);
		if(m == null){
			m = plotsMarkerSet.findAreaMarker(areaId);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
		} else {
			m.setFillStyle(0.25, DyeColor.YELLOW.getColor().asRGB());
			m.setLineStyle(1, 1, DyeColor.YELLOW.getColor().asRGB());
		}
	    if(fieldMarker != null){
	    	Marker field = markerList.remove(id);
	    	if (field == null){
	    		field = plotsMarkerSet.createMarker(id, p.getName(), p.getCenter().getWorld().getName(),
	    				p.getCenter().getBlockX(),
	    				p.getCenter().getBlockY(),
	    				p.getCenter().getBlockZ(), fieldMarker, false);
	    	} else {
	    		field.setLocation(p.getCenter().getWorld().getName(),
	    				p.getCenter().getBlockX(),
	    				p.getCenter().getBlockY(),
	    				p.getCenter().getBlockZ());
	            field.setLabel(p.getName());
	            field.setMarkerIcon(fieldMarker);
	    	}
	    	String description = Civilizations.getInstanceConfig().getString("Dynmap.Layer.Plots.Fields.InfoBubble", "%Name%");
	    	description = "<div class=\"regioninfo\">" + description + "</div>";
	    	description = description.replace("%Name%", p.getName());
	    	StringBuilder sb = new StringBuilder();
	    	if(p.isForRent()){
	    		if(p.getRenter() == null){
		    		sb.append("Available For Rent");
		    	} else {
		    		sb.append(p.getRenter().getName());
		    	}
	    	} else {
	    		if(p.getOwner() != null){
	    			sb.append(p.getOwner().getName());
	    		} else {
	    			if(p.getSettlement() != null){
	    				sb.append(p.getSettlement().getName());
	    			}
	    		}
	    	}
	    	String rent = p.isForRent() ? Economy.format(p.getRent()) : "Not for rent";
	    	description = description.replace("%RentStatus%", sb.toString());
	    	description = description.replace("%Rent%", rent);
	    	// Taxes
	    	String taxes = "Transaction Taxes:<ul>";
	    	if(p.getSettlement() != null){
	    		taxes += "<li>" + p.getSettlement().getName() + ": " + (p.getSettlement().getSettings().getTransactionTax() * 100) + "%</li>";
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
	    	for(String s : p.getWaresToString()){
	    		sb1.append("<li>" + s + "</li>");
	    	}
    		sb1.append("</ul>");
    		description = description.replace("%WaresList%", sb1.toString());
	    	field.setDescription(description);
	    	markerList.put(id, field);
	    }
	}
	
	/**
	 * Updates the display of the given Plot.
	 * @param p
	 */
	private static void updateStall(Plot p) {
		String id = "" + p.getIdentifier().toString() + "_icon";
		String areaId = "" + p.getIdentifier().toString() + "_area";
		String stallMarker = p.getIcon();
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
	    AreaMarker m = plotsMarkerSet.createAreaMarker(areaId, Utils.prettifyText(p.getName()), false, p.getCenter().getWorld().getName(), p.getShapes().get(0).getVerticesX(), p.getShapes().get(0).getVerticesZ(), false);
		if(m == null){
			m = plotsMarkerSet.findAreaMarker(areaId);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
		}
	    if(stallIcon != null){
	    	Marker stall = markerList.remove(id);
	    	if (stall == null){
	    		stall = plotsMarkerSet.createMarker(id, p.getName(), p.getCenter().getWorld().getName(),
	    				p.getCenter().getBlockX(),
	    				p.getCenter().getBlockY(),
	    				p.getCenter().getBlockZ(), stallIcon, false);
	    	} else {
	    		stall.setLocation(p.getCenter().getWorld().getName(),
	    				p.getCenter().getBlockX(),
	    				p.getCenter().getBlockY(),
	    				p.getCenter().getBlockZ());
	            stall.setLabel(p.getName());
	            stall.setMarkerIcon(stallIcon);
	    	}
	    	String description = Civilizations.getInstanceConfig().getString("Dynmap.Layer.Plots.Stalls.InfoBubble", "%Name%");
	    	description = "<div class=\"regioninfo\">" + description + "</div>";
	    	description = description.replace("%Name%", p.getName());
	    	StringBuilder sb = new StringBuilder();
	    	if(p.isForRent()){
	    		if(p.getRenter() == null){
		    		sb.append("Available For Rent");
		    	} else {
		    		sb.append(p.getRenter().getName());
		    	}
	    	} else {
	    		if(p.getOwner() != null){
	    			sb.append(p.getOwner().getName());
	    		} else {
	    			if(p.getSettlement() != null){
	    				sb.append(p.getSettlement().getName());
	    			}
	    		}
	    	}
	    	String rent = p.isForRent() ? Economy.format(p.getRent()) : "Not for rent";
	    	description = description.replace("%RentStatus%", sb.toString());
	    	description = description.replace("%Rent%", rent);
	    	// Taxes
	    	String taxes = "Transaction Taxes:<ul>";
	    	if(p.getSettlement() != null){
	    		taxes += "<li>" + p.getSettlement().getName() + ": " + (p.getSettlement().getSettings().getTransactionTax() * 100) + "%</li>";
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
	    	for(String s : p.getWaresToString()){
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
	    		sb.append("<br />" + EconomicEntity.get(uid).getName());
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
		AreaMarker m = regionsMarkerSet.createAreaMarker(polyID, Utils.prettifyText(region.getName()), false, influenceMap.getWorld().getName(), region.getCell(influenceMap).getVerticesX(), region.getCell(influenceMap).getVerticesZ(), false);
		if(m == null){
			m = regionsMarkerSet.findAreaMarker(polyID);
			if(m == null){
				Civilizations.log("SEVERE", "Failed to create marker area.");
				return;
			}
		}
		m.setLabel(Utils.prettifyText(region.getName()));
		if(region.getNation() != null){
			if(region.getNation().getBanner() != null){
				m.setFillStyle(0.1, Utils.getColor(region.getNation().getBanner().getBase()).getColor().asRGB());
				m.setLineStyle(2,  1, Utils.getColor(region.getNation().getBanner().getBase()).getColor().asRGB());
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
