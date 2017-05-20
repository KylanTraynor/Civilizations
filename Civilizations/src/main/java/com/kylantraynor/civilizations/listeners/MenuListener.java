package com.kylantraynor.civilizations.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.LockpickMenu;

public class MenuListener implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		ItemStack item = event.getCurrentItem();
		Button btn = ButtonManager.getButton(item, event.getWhoClicked());
		if(btn != null){
			Civilizations.DEBUG("Clicked on button \"" + btn.getName() + "\" by player " + event.getWhoClicked().getName() + ".");
			event.setCancelled(true);
			ButtonManager.run(btn);
		}
		if (MenuManager.getMenus().containsKey(event.getWhoClicked())){
			Civilizations.DEBUG("Couldn't find a button for \"" + event.getCurrentItem().getItemMeta().getDisplayName() + "\" by player " + event.getWhoClicked().getName() + ".");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getView() instanceof GroupMenu || event.getView() instanceof LockpickMenu){
			MenuManager.clearMenu(event.getPlayer());
			ButtonManager.clearButtons(event.getPlayer());
			if(event.getView() instanceof LockpickMenu){
				LockManager.stopLockpicking(event.getPlayer());
			}
		}
	}
}
