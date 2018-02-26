package com.kylantraynor.civilizations.groups.settlements.plots.fort;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.FortComponent;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.shapes.Shape;

public class Watchtower extends Plot implements FortComponent{

	public Watchtower(Shape shape, Settlement settlement) {
		super(shape, settlement);
	}

	@Override
	public Fort getFort(){
		if(getSettlement() instanceof Fort){
			return (Fort) getSettlement();
		}
		return null;
	}

}
