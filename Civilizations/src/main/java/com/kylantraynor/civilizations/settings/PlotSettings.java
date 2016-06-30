package com.kylantraynor.civilizations.settings;

import java.util.List;
import java.util.UUID;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.shapes.Shape;

public class PlotSettings extends GroupSettings{
	
	private Settlement settlement;

	/**
	 * Gets the settlement this plot belongs to.
	 * @return
	 */
	public Settlement getSettlement() {
		if(settlement != null) return settlement;
		if(this.contains("General.Settlement")){
			Settlement s = (Settlement) Group.get(UUID.fromString(this.getString("General.Settlement")));
			if(s != null){
				settlement = s;
			}
		}
		return settlement;
	}

	/**
	 * Sets the settlement this plot belongs to.
	 * @param settlement
	 */
	public void setSettlement(Settlement settlement) {
		if(settlement != null){
			this.set("General.Settlement", settlement.getSettings().getUniqueId().toString());
		} else {
			this.set("General.Settlement", null);
		}
		this.settlement = settlement;
		setChanged(true);
	}
}
