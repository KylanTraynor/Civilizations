package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.List;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.shapes.Hull;
import com.kylantraynor.civilizations.shapes.Shape;

public class SettlementProtection extends Protection{
	private boolean hullNeedsUpdate;
	private Settlement settlement;
	private Hull hull = new Hull();
	
	public SettlementProtection(Settlement settlement) {
		this.settlement = settlement;
	}
	
	public void hullNeedsUpdate(){
		hullNeedsUpdate = true;
	}

	public Hull getHull(){
		if(hullNeedsUpdate){
			hull.clear();
			for(Plot p : getSettlement().getPlots()){
				for(Shape s : p.getProtection().getShapes()){
					hull.addPoints(s.getVertices());
				}
			}
			hull.updateHull();
		}
		return hull;
	}
	
	public Settlement getSettlement(){
		if(settlement != null) return settlement;
		for(Settlement s : CacheManager.getSettlementList())
			if(s.getProtection().equals(this)){
				settlement = s;
				return settlement;
			}
		return null;
	}
	
	public List<Shape> getShapes(){
		if(getSettlement() instanceof Camp) return super.getShapes();
		if(getSettlement() instanceof TownyTown) return super.getShapes();
		List<Shape> result = new ArrayList<Shape>();
		result.add(getHull());
		return result;
	}
}
