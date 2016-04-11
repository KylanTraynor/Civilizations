package com.kylantraynor.civilizations.groups.settlements.forts;

import org.bukkit.Location;
import org.bukkit.Material;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.PermissionType;
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
		s.sendMessage(s.getChatHeader() + "Checking Plots", PermissionType.UPGRADE);
		for(Plot p : s.getPlots()){
			s.sendMessage(s.getChatHeader() + "Checking Plot", PermissionType.UPGRADE);
			if(p instanceof Keep){
				s.sendMessage(s.getChatHeader() + "Found Keep", PermissionType.UPGRADE);
				for(Shape shape : p.getProtection().getShapes()){
					s.sendMessage(s.getChatHeader() + "Checking Shape", PermissionType.UPGRADE);
					for(Location b : shape.getBlockLocations()){
						if(b.getBlock().getType() == Material.BANNER){
							s.sendMessage(s.getChatHeader() + "Found Banner", PermissionType.UPGRADE);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
