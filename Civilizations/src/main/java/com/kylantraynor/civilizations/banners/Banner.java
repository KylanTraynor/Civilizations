package com.kylantraynor.civilizations.banners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;

public class Banner {
	
	static private Map<org.bukkit.block.Banner, Banner> all = new HashMap<org.bukkit.block.Banner, Banner>();
	
	List<Pattern> patterns = new ArrayList<Pattern>();
	private org.bukkit.block.Banner banner;
	private DyeColor baseColor;
	
	public static Banner get(org.bukkit.block.Banner banner){
		if(all.containsKey(banner)){
			return all.get(banner);
		} else {
			return new Banner(banner);
		}
	}
	
	public Banner(org.bukkit.block.Banner banner){
		patterns = banner.getPatterns();
		baseColor = banner.getBaseColor();
		this.banner = banner;
		all.put(banner, this);
	}
	
	public ItemStack getItemStack(){
		this.banner.setBaseColor(baseColor);
		this.banner.setPatterns(patterns);
		ItemStack is = new ItemStack(Material.BANNER);
		is.setData(this.banner.getData());
		return is;
	}
}
