package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.shapes.Hull;
import com.kylantraynor.civilizations.shapes.Shape;

public class SettlementProtection extends Protection{
	protected boolean hullNeedsUpdate;
	private Settlement settlement;
	protected Hull hull;
	
	public SettlementProtection(Settlement settlement) {
		this.settlement = settlement;
	}
	
	public void hullNeedsUpdate(){
		hullNeedsUpdate = true;
	}
	
	/**
	 * Checks if the given location is inside of the protection.
	 * @param location
	 * @return
	 */
	@Override
	public boolean isInside(Location location){
		if(getHull().exists()){
			return getHull().isInside(location);
		}
		return false;
	}

	public Hull getHull(){
		if(hull == null) hull = new Hull(getSettlement().getLocation());
		if(hullNeedsUpdate){
			hull.clear();
			for(Plot p : getSettlement().getPlots()){
				for(Shape s : p.getProtection().getShapes()){
					hull.addPoints(s.getVertices());
				}
			}
			hull.updateHull();
			hullNeedsUpdate = false;
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
