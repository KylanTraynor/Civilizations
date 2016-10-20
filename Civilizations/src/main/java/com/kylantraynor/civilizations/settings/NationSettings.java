package com.kylantraynor.civilizations.settings;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.banners.Banner;

public class NationSettings extends GroupSettings{
	private OfflinePlayer lord;
	private Banner banner;

	/**
	 * Gets the Words of the Nation.
	 * @return String
	 */
	public String getWords(){
		if(this.contains("General.Words")){
			return this.getString("General.Words");
		}
		return "Words are overrated";
	}
	
	/**
	 * Sets the Words of the Nation.
	 * @param words
	 */
	public void setWords(String words){
		if(words == null) return;
		this.set("General.Words", words);
		this.setChanged(true);
	}
	
	/**
	 * Gets the current Lord of the Nation.
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
	 * Sets the current Lord of the Nation
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
	 * Gets the banner of the Nation.
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
	 * Sets the banner of the Nation.
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
}