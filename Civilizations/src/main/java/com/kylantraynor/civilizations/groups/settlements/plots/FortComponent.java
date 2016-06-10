package com.kylantraynor.civilizations.groups.settlements.plots;

import com.kylantraynor.civilizations.groups.settlements.forts.Fort;

public interface FortComponent {
	
	/**
	 * Gets the fort this component is attached to.
	 * @return
	 */
	public Fort getFort();
}