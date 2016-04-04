package com.kylantraynor.civilizations.menus;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;

public class MenuListener implements Listener{
	
	private Civilizations plugin;
	
	public MenuListener(Civilizations plugin){
		this.plugin = plugin;
	}
	
	public void onInventoryClick(InventoryClickEvent event){
		ItemStack item = event.getCurrentItem();
		if(item instanceof Button){
			event.setCancelled(true);
			Button button = (Button) item;
			button.run();
		}
	}
}
