package com.kylantraynor.civilizations.groups;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.BannerOwner;
import com.kylantraynor.civilizations.settings.NationSettings;

public class Nation extends GroupContainer implements BannerOwner{

	public Nation(String name, Banner banner) {
		super();
		setName(name);
		setBanner(banner);
	}

	public static Nation get(String string) {
		for(Group g : Cache.getGroupList()){
			if(g instanceof Nation){
				if(g.getName().equalsIgnoreCase(string)){
					return (Nation) g;
				}
			}
		}
		return null;
	}
	
	@Override
	public Banner getBanner() {
		return this.getSettings().getBanner();
	}

	@Override
	public void setBanner(Banner newBanner) {
		this.getSettings().setBanner(newBanner);
	}
	
	@Override
	public NationSettings getSettings() {
		return (NationSettings)super.getSettings();
	}

	public String getWords() {
		return this.getSettings().getWords();
	}

	public void setWords(String words) {
		this.getSettings().setWords(words);
	}

	public static Nation get(Banner banner) {
		for(Group g : Cache.getGroupList()){
			if(g instanceof Nation){
				if(((Nation) g).getBanner().isSimilar(banner)) return (Nation) g;
			}
		}
		return null;
	}

	public static Nation get(Player p) {
		for(Group g : Cache.getGroupList()){
			if(g instanceof Nation){
				if(g.isMember(p)) return (Nation) g;
			}
		}
		return null;
	}

	
}
