package com.kylantraynor.civilizations;

import java.util.ArrayList;
import java.util.List;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.hook.towny.TownyTown;

public class Cache {
	
	public static boolean groupListChanged = true;
	public static boolean settlementListChanged = true;
	public static boolean campListChanged = true;
	public static boolean townyTownListChanged;
	
	private static List<Group> groupList;
	private static List<Settlement> settlementList;
	private static List<Camp> campList;
	private static List<TownyTown> townyTownList;
	
	/**
	 * Gets the list of Groups.
	 * @return List<Group> of cached groups.
	 */
	public static List<Group> getGroupList(){
		if(groupListChanged || groupList == null){
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
	 * Gets the list of Towns from Towny.
	 * @return List<TownyTown> of cached Towns.
	 */
	public static List<TownyTown> getTownyTownList() {
		if(townyTownListChanged || townyTownList == null){
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
}