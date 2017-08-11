package com.kylantraynor.civilizations.shops;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.events.TaxationEvent;
import com.kylantraynor.civilizations.hook.HookManager;
import com.kylantraynor.civilizations.hook.quickshop.QuickShopShop;
import com.kylantraynor.civilizations.hook.quickshop.QuickshopHook;

public class ShopManager {
	
	public List<Shop> shops = new ArrayList<Shop>();
	
	
	public boolean createShop(Location l, double price, ItemStack item, EconomicEntity owner){
		
		return false;
	}


	public static Shop getShop(Location current) {
		if(HookManager.getQuickshop() != null){
			if(HookManager.getQuickshop().isQuickShop(current)){
				return HookManager.getQuickshop().getShop(current);
			}
		}
		return null;
	}


	public static void startPurchase(QuickShopShop shop, EconomicEntity entity, int amount, boolean alreadyHandled) {
		
		double fullPrice = shop.getPrice() * amount;
		
		if(!alreadyHandled){
			// If a plugin didn't already handle the transaction, then let Civs do it.
			Economy.transferFunds(entity, shop.getOwner(), "Transaction for " + amount + " " + shop.getItemName(), fullPrice);
		}
		
		//Takes care of the taxes.
		if(shop.getSettlement() != null){
			TaxationEvent event = new TaxationEvent(fullPrice, shop.getSettlement(), TaxType.TRANSACTION);
			Civilizations.callEvent(event);
			if(!event.isCancelled()){
				// Sends the tax money to the settlement.
				Economy.transferFunds(shop.getOwner(), shop.getSettlement(), shop.getSettlement().getName() + " Transaction Tax for " + amount + " " + shop.getItemName(), event.getTaxedAmount());
			}
		}
		if(shop.getFort() != null){
			TaxationEvent event = new TaxationEvent(fullPrice, shop.getFort(), TaxType.TRANSACTION);
			Civilizations.callEvent(event);
			if(!event.isCancelled()){
				// Sends the tax money to the fort.
				Economy.transferFunds(shop.getOwner(), shop.getFort(), "Fort Transaction Tax for " + amount + " " + shop.getItemName(), event.getTaxedAmount());
			}
		}
		
		if(!alreadyHandled){
			//TODO Do things if the transaction is not already handled by another plugin.
		}
		
	}
}
