package com.kylantraynor.civilizations.managers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.kylantraynor.cache.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.voronoi.VTriangle;

public class CacheManager {
	
	private final static int CACHE_CAPACITY = 1000;
	private final static long CACHE_TIMER = 1l;
	private final static long CACHE_LIFESPAN = 30l * 60l;
	
	public static boolean groupListChanged = true;
	public static boolean settlementListChanged = true;
	public static boolean campListChanged = true;
	public static boolean townyTownListChanged = true;
	public static boolean plotListChanged = true;
	public static boolean houseListChanged = true;
	public static boolean fortListChanged = true;
	public static boolean marketstallListChanged = true;
	
	private static List<Group> groupList;
	private static List<Settlement> settlementList;
	private static List<Camp> campList;
	private static List<TownyTown> townyTownList;
	private static List<Plot> plotList;
	private static List<House> houseList;
	private static List<Fort> fortList;
	private static List<MarketStall> marketstallList;
	
	private static Cache<UUID, VTriangle> playerLocations;
	
	public static void init(){
		playerLocations = new Cache<UUID, VTriangle>(CACHE_LIFESPAN, CACHE_TIMER, CACHE_CAPACITY);
	}

	/**
	 * Gets the list of Groups.
	 * @return List<Group> of cached groups.
	 */
	public static List<Group> getGroupList(){
		if(groupListChanged || groupList == null){
			Civilizations.DEBUG("Groups list needs update. Updating... ");
			groupListChanged = false;
			groupList = new ArrayList<Group>();
			for(Group g : Group.getList()){
				groupList.add(g);
			}
			
		}
		return groupList;
	}
	
	/**
	 * Gets the list of Settlements.
	 * @return List<Settlement> of cached settlements.
	 */
	public static List<Settlement> getSettlementList(){
		if(settlementListChanged || settlementList == null){
			Civilizations.DEBUG("Settlements list needs update. Updating... ");
			settlementListChanged = false;
			settlementList = new ArrayList<Settlement>();
			for(Group g : getGroupList()){
				if(g instanceof Settlement){
					settlementList.add((Settlement) g);
				}
			}
		}
		return settlementList;
	}
	
	/**
	 * Gets the list of Camps.
	 * @return List<Camp> of cached Camps.
	 */
	public static List<Camp> getCampList(){
		if(campListChanged || campList == null){
			Civilizations.DEBUG("Camps list needs update. Updating... ");
			campListChanged = false;
			campList = new ArrayList<Camp>();
			for(Settlement s : getSettlementList()){
				if(s instanceof Camp){
					campList.add((Camp) s);
				}
			}
		}
		return campList;
	}
	
	/**
	 * Gets the list of Plots.
	 * @return List<Plot> of cached Plots.
	 */
	public static List<Plot> getPlotList(){
		if(plotListChanged || plotList == null){
			Civilizations.DEBUG("Plots list needs update. Updating... ");
			plotListChanged = false;
			plotList = new ArrayList<Plot>();
			for(Group g : getGroupList()){
				if(g instanceof Plot){
					plotList.add((Plot) g);
				}
			}
		}
		return plotList;
	}

	/**
	 * Gets the list of Towns from Towny.
	 * @return List<TownyTown> of cached Towns.
	 */
	public static List<TownyTown> getTownyTownList() {
		if(townyTownListChanged || townyTownList == null){
			Civilizations.DEBUG("Towny towns list needs update. Updating... ");
			townyTownListChanged = false;
			townyTownList = new ArrayList<TownyTown>();
			for(Settlement s : getSettlementList()){
				if(s instanceof TownyTown){
					townyTownList.add((TownyTown) s);
				}
			}
		}
		return townyTownList;
	}
	/**
	 * Gets the list of Houses.
	 * @return List<House> of cached Houses.
	 */
	public static List<House> getHouseList(){
		if(houseListChanged || houseList == null){
			Civilizations.DEBUG("Houses list needs update. Updating... ");
			houseListChanged = false;
			houseList = new ArrayList<House>();
			for(Group g : getGroupList()){
				if(g instanceof House){
					houseList.add((House) g);
				}
			}
		}
		return houseList;
	}
	/**
	 * Gets the list of Forts.
	 * @return List<Fort> of cached Forts.
	 */
	public static List<Fort> getFortList(){
		if(fortListChanged || fortList == null){
			Civilizations.DEBUG("Forts list needs update. Updating... ");
			fortListChanged = false;
			fortList = new ArrayList<Fort>();
			for(Settlement g : getSettlementList()){
				if(g instanceof Fort){
					fortList.add((Fort) g);
				}
			}
		}
		return fortList;
	}
	
	/**
	 * Gets the list of MarketStalls.
	 * @return List<MarketStall> of cached MarketStalls.
	 */
	public static List<MarketStall> getMarketstallList(){
		if(marketstallListChanged || marketstallList == null){
			Civilizations.DEBUG("Stalls list needs update. Updating... ");
			marketstallListChanged = false;
			marketstallList = new ArrayList<MarketStall>();
			for(Plot p : getPlotList()){
				if(p instanceof MarketStall){
					marketstallList.add((MarketStall) p);
				}
			}
		}
		return marketstallList;
	}
	
	/**
	 * Gets the location of the given player that was cached from the influence map.
	 * @param p
	 * @return
	 */
	public static VTriangle getPlayerTriangulation(Player p){
		return playerLocations.get(p.getUniqueId());
	}
	
	/**
	 * Sets the location of the given player on the influence map to the cache.
	 * @param p
	 * @param t
	 * @return The old value, or Null.
	 */
	public static VTriangle setPlayerTriangulation(Player p, VTriangle t){
		return playerLocations.put(p.getUniqueId(), t);
	}
}