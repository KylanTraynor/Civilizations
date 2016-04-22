package com.kylantraynor.civilizations;

import java.util.ArrayList;
import java.util.List;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.hook.towny.TownyTown;

public class Cache {
	
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
	
	/**
	 * Gets the list of Groups.
	 * @return List<Group> of cached groups.
	 */
	public static List<Group> getGroupList(){
		Civilizations.DEBUG("Getting Groups from Cache.");
		if(groupListChanged || groupList == null){
			Civilizations.DEBUG("Groups list needs update. Updating... ");
			groupListChanged = false;
			groupList = new ArrayList<Group>();
			for(Group g : Group.getList()){
				groupList.add(g);
			}
			
		}
		Civilizations.DEBUG("Returning a list of " + groupList.size() + " groups.");
		return groupList;
	}
	
	/**
	 * Gets the list of Settlements.
	 * @return List<Settlement> of cached settlements.
	 */
	public static List<Settlement> getSettlementList(){
		Civilizations.DEBUG("Getting Settlements from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + settlementList.size() + " settlements.");
		return settlementList;
	}
	
	/**
	 * Gets the list of Camps.
	 * @return List<Camp> of cached Camps.
	 */
	public static List<Camp> getCampList(){
		Civilizations.DEBUG("Getting Camps from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + campList.size() + " camps.");
		return campList;
	}
	
	/**
	 * Gets the list of Plots.
	 * @return List<Plot> of cached Plots.
	 */
	public static List<Plot> getPlotList(){
		Civilizations.DEBUG("Getting Plots from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + plotList.size() + " plots.");
		return plotList;
	}

	/**
	 * Gets the list of Towns from Towny.
	 * @return List<TownyTown> of cached Towns.
	 */
	public static List<TownyTown> getTownyTownList() {
		Civilizations.DEBUG("Getting Towny Towns from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + townyTownList.size() + " Towny towns.");
		return townyTownList;
	}
	/**
	 * Gets the list of Houses.
	 * @return List<House> of cached Houses.
	 */
	public static List<House> getHouseList(){
		Civilizations.DEBUG("Getting Houses from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + houseList.size() + " Houses.");
		return houseList;
	}
	/**
	 * Gets the list of Forts.
	 * @return List<Fort> of cached Forts.
	 */
	public static List<Fort> getFortList(){
		Civilizations.DEBUG("Getting Forts from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + fortList.size() + " forts.");
		return fortList;
	}
	
	/**
	 * Gets the list of MarketStalls.
	 * @return List<MarketStall> of cached MarketStalls.
	 */
	public static List<MarketStall> getMarketstallList(){
		Civilizations.DEBUG("Getting Stalls from Cache.");
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
		Civilizations.DEBUG("Returning a list of " + marketstallList.size() + " stalls.");
		return marketstallList;
	}
}