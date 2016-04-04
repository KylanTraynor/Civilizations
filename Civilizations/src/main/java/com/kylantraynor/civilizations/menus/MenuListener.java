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
		Button btn = ButtonManager.getButton(item, event.getInventory().getViewers());
		if(btn != null){
			event.setCancelled(true);
			ButtonManager.run(btn);
		}
	}
}
