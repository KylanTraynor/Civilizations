package com.kylantraynor.civilizations.territories;

import org.bukkit.Location;

import com.kylantraynor.civilizations.groups.settlements.forts.Fort;

public class Territory {

	static int size = 128;
	
	private int x;
	private int z;
	
	public Territory(int x, int z){
		this.x = x;
		this.z = z;
	}
	
	public Fort getFort(){
		return InfluenceMap.getInfluentFortAt(this.getCenter());
	}
	
	public Location getCenter(){
		
		return null;
	}
}
