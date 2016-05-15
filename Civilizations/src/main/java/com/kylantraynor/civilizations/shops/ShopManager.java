package com.kylantraynor.civilizations.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.hook.quickshop.QuickshopHook;

public class ShopManager {
	
	public List<Shop> shops = new ArrayList<Shop>();
	
	
	public boolean createShop(Location l, double price, ItemStack item, OfflinePlayer owner){
		
		return false;
	}


	public static Shop getShop(Location current) {
		if(QuickshopHook.isActive()){
			if(QuickshopHook.isQuickShop(current)){
				return QuickshopHook.getShop(current);
			}
		}
		return null;
	}
}
