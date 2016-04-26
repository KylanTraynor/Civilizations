package com.kylantraynor.civilizations.hook.citizens;

import org.bukkit.entity.LivingEntity;

import com.kylantraynor.civilizations.groups.settlements.Settlement;

import net.aufdemrand.sentry.Sentry;
import net.aufdemrand.sentry.SentryInstance;

public class Guard extends SentryInstance{

	Long isRespawnable = System.currentTimeMillis();
	public Settlement settlement;
	public GuardTrait myTrait; 
	
	public Guard(Sentry plugin, Settlement settlement) {
		super(plugin);
		this.settlement = settlement;
	}
	
	@Override
	public boolean isTarget(LivingEntity aTarget){
		return false;
	}
	
}
