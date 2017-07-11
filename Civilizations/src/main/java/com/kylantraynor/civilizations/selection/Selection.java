package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;

public interface Selection {
	public boolean isValid();
	public String getValidityReason();
	public Location getLocation();
}
