package com.kylantraynor.civilizations;

import java.util.ArrayList;
import java.util.List;

import com.kylantraynor.civilizations.towns.TownyTown;

public class Cache {
	
	public static boolean groupListChanged = true;
	public static boolean settlementListChanged = true;
	public static boolean campListChanged = true;
	public static boolean townyTownListChanged;
	
	private static List<Group> groupList;
	private static List<Settlement> settlementList;
	private static List<Camp> campList;
	private static List<TownyTown> townyTownList;
	
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
