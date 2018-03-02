package com.kylantraynor.civilizations.menus;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class MenuReturnFunction<T> extends BukkitRunnable {
	private T returnedValue = null;
	
	public T getReturnedValue(){
		return returnedValue;
	}
	
	public void setReturnedValue(T value){
		returnedValue = value;
	}
}
