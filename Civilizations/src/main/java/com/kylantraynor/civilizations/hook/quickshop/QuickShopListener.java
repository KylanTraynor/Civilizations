package com.kylantraynor.civilizations.hook.quickshop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maxgamer.QuickShop.Shop.ShopPurchaseEvent;

import com.kylantraynor.civilizations.shops.ShopManager;

public class QuickShopListener implements Listener{

	@EventHandler
	public void onShopPurchase(ShopPurchaseEvent event){
		// What has been checked for the event to be triggered:
		// The shop still exists
		// The amount of blocks in the shop is good (selling)
		// The amount of space in the shop is good (buying)
		// Has enough space in inventory (selling)
		// Has enough items in inventory (buying)
		if(event.isCancelled()) return;
		QuickShopShop shop = new QuickShopShop(event.getShop());
		ShopManager.startPurchase(shop, event.getPlayer(), event.getAmount(), true);
		//What will be checked after:
		//Buyer has enough money.
	}
}
