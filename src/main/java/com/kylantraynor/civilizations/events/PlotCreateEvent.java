package com.kylantraynor.civilizations.events;
import org.bukkit.event.HandlerList;

public class PlotCreateEvent extends GroupCreateEvent{
	
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
