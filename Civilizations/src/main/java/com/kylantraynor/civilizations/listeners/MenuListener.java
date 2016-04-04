package com.kylantraynor.civilizations.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.ButtonManager;

public class MenuListener implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		ItemStack item = event.getCurrentItem();
		Button btn = ButtonManager.getButton(item, event.getInventory().getViewers());
		if(btn != null){
			event.setCancelled(true);
			ButtonManager.run(btn);
		}
	}
}
