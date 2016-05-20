package com.kylantraynor.civilizations.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.events.TaxationEvent;
import com.kylantraynor.civilizations.hook.quickshop.QuickShopShop;
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


	public static void startPurchase(QuickShopShop shop, Player player, int amount, boolean alreadyHandled) {
		
		double fullPrice = shop.getPrice() * amount;
		
		if(!alreadyHandled){
			//TODO Do things if the transaction is not already handled by another plugin.
		}
		
		//Takes care of the taxes.
		if(shop.getSettlement() != null){
			TaxationEvent event = new TaxationEvent(fullPrice, shop.getSettlement(), TaxType.TRANSACTION);
			Civilizations.callEvent(event);
			if(!event.isCancelled()){
				//TODO Send tax to settlement
				fullPrice = event.getRemainingAmount();
			}
		}
		if(shop.getFort() != null){
			TaxationEvent event = new TaxationEvent(fullPrice, shop.getFort(), TaxType.TRANSACTION);
			Civilizations.callEvent(event);
			if(!event.isCancelled()){
				//TODO Send tax to fort
				fullPrice = event.getRemainingAmount();
			}
		}
		//-----------------------
		
		if(!alreadyHandled){
			//TODO Do things if the transaction is not already handled by another plugin.
		}
		
	}
}
