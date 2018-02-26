package com.kylantraynor.civilizations.menus;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class MenuReturnFunction extends BukkitRunnable {
	private int returnedValue = -1;
	
	public int getReturnedValue(){
		return returnedValue;
	}
	
	public void setReturnedValue(int value){
		returnedValue = value;
	}
}
