package com.kylantraynor.civilizations.hook.quickshop;

import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.shops.ShopType;

public class QuickShopShop extends Shop{

	public QuickShopShop(org.maxgamer.quickshop.Shop.Shop shop) {
		this.setLocation(shop.getLocation());
		this.setItem(shop.getItem());
		this.setPrice(shop.getPrice());
		if(shop.isBuying()){
			this.setType(ShopType.BUYING);
		} else {
			this.setType(ShopType.SELLING);
		}
	}

}
