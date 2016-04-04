package com.kylantraynor.civilizations.groups.settlements.plots.market;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.shapes.Shape;

public class MarketStall extends Plot{

	public MarketStall(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getType() {
		return "Market Stall";
	}

}