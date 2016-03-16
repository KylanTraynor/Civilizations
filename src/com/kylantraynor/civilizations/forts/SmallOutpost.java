package com.kylantraynor.civilizations.forts;

import org.bukkit.Location;

import com.kylantraynor.civilizations.Camp;
import com.kylantraynor.civilizations.Settlement;
import com.kylantraynor.civilizations.shapes.Prism;

public class SmallOutpost extends Settlement {
	public SmallOutpost(Location l) {
		super(l);
	}
	
	public SmallOutpost(Camp c) {
		super(c.getLocation());
		this.setMembers(c.getMembers());
		this.getProtection().add(new Prism(this.getLocation().add(-8, -8, -8), 16, 16, 16));
		c.remove();
		
	}
}