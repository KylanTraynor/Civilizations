package com.kylantraynor.civilizations.banners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;

public class Banner {
	
	List<Pattern> patterns = new ArrayList<Pattern>();
	private org.bukkit.block.Banner banner;
	private DyeColor baseColor;
	
	public Banner(org.bukkit.block.Banner banner){
		patterns = banner.getPatterns();
		baseColor = banner.getBaseColor();
		this.banner = banner;
	}
	
	public ItemStack getItemStack(){
		this.banner.setBaseColor(baseColor);
		this.banner.setPatterns(patterns);
		ItemStack is = new ItemStack(Material.BANNER);
		is.setData(this.banner.getData());
		return is;
	}
}
