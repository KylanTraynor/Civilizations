package com.kylantraynor.civilizations.hook.quickshop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maxgamer.QuickShop.Shop.ShopPurchaseEvent;

public class QuickShopListener implements Listener{

	@EventHandler
	public void onShopPurchase(ShopPurchaseEvent event){
		if(event.isCancelled()) return;
		QuickShopShop shop = new QuickShopShop(event.getShop());
	}
}
