package com.kylantraynor.civilizations.groups.settlements.forts;

import org.bukkit.Location;
import org.bukkit.Material;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.shapes.Shape;

public class SmallOutpost extends Fort {

	public SmallOutpost(Camp c) {
		super(c.getLocation());
		this.setMembers(c.getMembers());
		this.setPlots(c.getPlots());
		c.remove();
		setChanged(true);
	}
	
	static public boolean hasUpgradeRequirements(Settlement s){
		for(Plot p : s.getPlots()){
			if(p instanceof Keep){
				for(Shape shape : p.getProtection().getShapes()){
					for(Location b : shape.getBlockLocations()){
						if(b.getBlock().getType() == Material.BANNER){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
