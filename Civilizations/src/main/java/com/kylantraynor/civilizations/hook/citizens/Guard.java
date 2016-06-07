package com.kylantraynor.civilizations.hook.citizens;


import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Guard{

	Long isRespawnable = System.currentTimeMillis();
	public Settlement settlement;
	public GuardTrait myTrait;
	private Civilizations plugin; 
	
	public Guard(Civilizations plugin, Settlement settlement) {
		this.plugin = plugin;
		this.settlement = settlement;
	}
	
}
