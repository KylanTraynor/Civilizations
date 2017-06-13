package com.kylantraynor.civilizations.hook.quickshop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.maxgamer.quickshop.QuickShop;
import org.maxgamer.quickshop.Shop.Shop;

import com.kylantraynor.civilizations.shops.ShopType;

public class QuickshopHook {
	
	public static boolean isActive(){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("QuickShop");
		if(plugin == null){
			return false;
		} else {
			return plugin.isEnabled();
		}
	}
	
	public static boolean isQuickShop(Location location){
		if(QuickShop.instance.getShopManager().getShop(location) != null){
			return true;
		} else {
			return false;
		}
	}
	
	public static QuickShopShop getShop(Location location){
		Shop shop = QuickShop.instance.getShopManager().getShop(location);
		if(shop == null) return null;
		QuickShopShop s = new QuickShopShop(shop);
		return s;
	}
	
}
