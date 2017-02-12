package com.kylantraynor.civilizations.hook.towny;

import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.SettlementProtection;
import com.kylantraynor.civilizations.shapes.Hull;
import com.kylantraynor.civilizations.shapes.Shape;

public class TownyTownProtection extends SettlementProtection {

	public TownyTownProtection(TownyTown town) {
		super(town);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Hull getHull(){
		if(hull == null) hull = new Hull(getSettlement().getLocation());
		if(this.hullNeedsUpdate){
			hull.clear();
			for(Plot p : getSettlement().getPlots()){
				for(Shape s : p.getProtection().getShapes()){
					hull.addPoints(s.getVertices());
				}
			}
			for(Shape s : ((TownyTown)getSettlement()).getTownyPlots()){
				hull.addPoints(s.getVertices());
			}
			hull.updateHull();
		}
		return hull;
	}

}
