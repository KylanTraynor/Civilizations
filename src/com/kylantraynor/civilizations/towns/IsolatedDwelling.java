package com.kylantraynor.civilizations.towns;

import org.bukkit.Location;

import com.kylantraynor.civilizations.Camp;
import com.kylantraynor.civilizations.Settlement;
import com.kylantraynor.civilizations.shapes.Prism;

public class IsolatedDwelling extends Settlement{

	public IsolatedDwelling(Location l) {
		super(l);
	}
	
	public IsolatedDwelling(Camp c) {
		super(c.getLocation());
		this.setMembers(c.getMembers());
		this.getProtection().add(new Prism(this.getLocation().add(-8, -8, -8), 16, 16, 16));
		c.remove();
	}

}
