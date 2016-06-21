package com.kylantraynor.civilizations.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.groups.House;

public class HouseSettings extends GroupSettings {
	private OfflinePlayer lord;
	private Banner banner;
	private List<House> vassals;

	/**
	 * Gets the Words of the House.
	 * @return String
	 */
	public String getWords(){
		if(this.contains("General.Words")){
			return this.getString("General.Words");
		}
		return "We don't know what to say, so we just say nothing";
	}
	
	/**
	 * Sets the Words of the House.
	 * @param words
	 */
	public void setWords(String words){
		if(words == null) return;
		this.set("General.Words", words);
		this.setChanged(true);
	}
	
	/**
	 * Gets the current Lord of the House.
	 * @return
	 */
	public OfflinePlayer getLord(){
		if(this.lord != null) return lord;
		if(this.contains("General.Lord")){
			this.lord = Bukkit.getOfflinePlayer(UUID.fromString(this.getString("General.Lord")));
			return lord;
		}
		return null;
	}
	
	/**
	 * Sets the current Lord of the House
	 * @param p
	 */
	public void setLord(OfflinePlayer p){
		this.lord = p;
		if(this.lord != null){
			this.set("General.Lord", lord.getUniqueId().toString());
		} else {
			this.set("General.Lord", null);
		}
		this.setChanged(true);
	}
	
	/**
	 * Gets the banner of the House.
	 * @return
	 */
	public Banner getBanner(){
		if(this.banner != null) return banner;
		if(this.contains("General.Banner")){
			this.banner = Banner.parse(this.getString("General.Banner"));
			return banner;
		}
		return null;
	}
	
	/**
	 * Sets the banner of the House.
	 * @param b
	 */
	public void setBanner(Banner b){
		this.banner = b;
		if(this.banner != null){
			this.set("General.Banner", this.banner.toString());
		} else {
			this.set("General.Banner", null);
		}
		setChanged(true);
	}
	
	/**
	 * Gets the list of Vassals of this House.
	 * @return
	 */
	public List<House> getVassals(){
		if(this.vassals != null) return vassals;
		if(this.contains("Vassals")){
			List<?> list = this.getList("Vassals");
			vassals = new ArrayList<House>();
			for(Object o : list){
				if(o instanceof String){
					vassals.add(House.get((String) o));
				}
			}
		}
		return vassals;
	}
	
	/**
	 * Sets the list of vassals of the House.
	 * @param list
	 */
	public void setVassals(List<House> list){
		if(list == null){
			vassals = new ArrayList<House>();
			this.set("Vassals", null);
		} else {
			this.vassals = list;
			List<String> slist = new ArrayList<String>();
			for(House h : vassals){
				slist.add(h.getName());
			}
			this.set("Vassals", slist);
		}
		setChanged(true);
	}
}
