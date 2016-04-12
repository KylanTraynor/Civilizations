package com.kylantraynor.civilizations.groups;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.IHasBanner;

/**
 * Family House, with all the members of the family.
 * @author Baptiste
 *
 */
public class House extends Group implements IHasBanner{
	
	private static List<House> all = new ArrayList<House>();
	private Banner banner;
	
	public House(String name, org.bukkit.block.Banner b) {
		super();
		setName(name);
		this.banner = Banner.get(b);
		all.add(this);
	}
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + getName() + ChatColor.GOLD + "] "; 
	}

	@Override
	public Banner getBanner() {
		return banner;
	}

	@Override
	public void setBanner(Banner newBanner) {
		this.banner = newBanner;
	}

	public static House get(Banner banner) {
		for(House h : all){
			if(h.getBanner().equals(banner)){
				return h;
			}
		}
		return null;
	}
	
	public static House get(Player p){
		for(House h : all){
			if(h.isMember(p)){
				return h;
			}
		}
		return null;
	}
	
	public static House get(String name){
		for(House h : all){
			if(h.getName().equalsIgnoreCase(name)){
				return h;
			}
		}
		return null;
	}
	
}
