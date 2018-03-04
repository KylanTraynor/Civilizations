package com.kylantraynor.civilizations.hook.quickshop;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.shops.ShopType;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;

public class QuickShopShop extends Shop{

	public QuickShopShop(org.maxgamer.quickshop.Shop.Shop shop) {
		this.setLocation(shop.getLocation());
		this.setItem(shop.getItem());
		this.setPrice(shop.getPrice());
		this.setOwner(EconomicEntity.get(shop.getOwner()));
		if(shop.isBuying()){
			this.setType(ShopType.BUYING);
		} else {
			this.setType(ShopType.SELLING);
		}
	}

}
