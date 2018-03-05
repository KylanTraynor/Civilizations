package com.kylantraynor.civilizations.listeners;

import com.kylantraynor.civilizations.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
		Player p = (Player) event.getWhoClicked();
		if(btn != null){
			Civilizations.DEBUG("Clicked on button \"" + btn.getName() + "\" by player " + p.getName() + ".");
			event.setCancelled(true);
			ButtonManager.run(btn);
		} else if (MenuManager.getMenus().containsKey(p)){
			if(event.getCurrentItem() != null){
				String name = event.getCurrentItem().getType().toString();
				if(event.getCurrentItem().getItemMeta() != null){
					name = event.getCurrentItem().getItemMeta().getDisplayName();
				}
				Civilizations.DEBUG("Couldn't find a button for \"" + name + "\" by player " + p.getName() + ".");
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(event.getView() instanceof Menu){
			MenuManager.clearMenu(event.getPlayer());
			ButtonManager.clearButtons(event.getPlayer());
			if(event.getView() instanceof LockpickMenu){
				LockManager.stopLockpicking(event.getPlayer());
			}
		}
	}
}
