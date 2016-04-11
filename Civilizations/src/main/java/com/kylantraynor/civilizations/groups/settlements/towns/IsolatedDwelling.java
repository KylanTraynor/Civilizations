package com.kylantraynor.civilizations.groups.settlements.towns;

import org.bukkit.Location;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.House;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;

public class IsolatedDwelling extends Settlement {

	public IsolatedDwelling(Location l) {
		super(l);
		// TODO Auto-generated constructor stub
	}

	static public boolean hasUpgradeRequirements(Settlement s){
		for(Plot p : s.getPlots()){
			if(p instanceof House){
				return true;
			}
		}
		return false;
	}
}
