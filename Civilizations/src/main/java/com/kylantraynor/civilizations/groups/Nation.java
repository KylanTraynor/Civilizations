package com.kylantraynor.civilizations.groups;

import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.BannerOwner;

public class Nation extends GroupContainer implements BannerOwner{

	private Banner banner;
	
	@Override
	public Banner getBanner() {
		return this.banner;
	}

	@Override
	public void setBanner(Banner newBanner) {
		this.banner = newBanner;
	}
	
}
