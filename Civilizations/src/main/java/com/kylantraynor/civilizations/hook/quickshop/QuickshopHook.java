package com.kylantraynor.civilizations.hook.quickshop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.maxgamer.QuickShop.QuickShop;
import org.maxgamer.QuickShop.Shop.Shop;

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
		QuickShopShop s = new QuickShopShop();
		Shop shop = QuickShop.instance.getShopManager().getShop(location);
		if(shop == null) return null;
		s.setLocation(shop.getLocation());
		s.setItem(shop.getItem());
		s.setPrice(shop.getPrice());
		if(shop.isBuying()){
			s.setType(ShopType.BUYING);
		} else {
			s.setType(ShopType.SELLING);
		}
		return s;
	}
	
}
