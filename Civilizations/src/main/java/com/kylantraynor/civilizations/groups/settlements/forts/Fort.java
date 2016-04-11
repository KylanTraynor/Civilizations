package com.kylantraynor.civilizations.groups.settlements.forts;

import org.bukkit.Location;

import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Fort extends Settlement{

	public Fort(Location l) {
		super(l);
	}
	
	public Fort(Settlement s){
		super(s.getLocation());
	}
}
