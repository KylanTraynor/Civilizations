package com.kylantraynor.civilizations.hook.quickshop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.Shop;

public class QuickshopHook {
	
	public boolean isActive(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("QuickShop");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public boolean isQuickShop(Location location){
		if(QuickShop.instance.getShopManager().getShop(location) != null){
			return true;
		} else {
			return false;
		}
	}
	
	public QuickShopShop getShop(Location location){
		Shop shop = QuickShop.instance.getShopManager().getShop(location);
		if(shop == null) return null;
		QuickShopShop s = new QuickShopShop(shop);
		return s;
	}
	
}
