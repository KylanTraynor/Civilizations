package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.IHasBanner;

/**
 * Family House, with all the members of the family.
 * @author Baptiste
 *
 */
public class House extends Group implements IHasBanner{
	
	private Banner banner;
	
	public House(String name, Banner b) {
		super();
		setName(name);
		this.banner = b;
		setChanged(true);
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
		setChanged(true);
	}
	
	public static List<House> getAll(){
		return Cache.getHouseList();
	}

	public static House get(Banner banner) {
		for(House h : getAll()){
			if(h.getBanner().equals(banner)){
				return h;
			}
		}
		return null;
	}
	
	public static House get(Player p){
		for(House h : getAll()){
			if(h.isMember(p)){
				return h;
			}
		}
		return null;
	}
	
	public static House get(String name){
		for(House h : getAll()){
			if(h.getName().equalsIgnoreCase(name)){
				return h;
			}
		}
		return null;
	}
	
	/**
	 * Gets the file where this camp is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getHouseDirectory(), "" + this.getName() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	/**
	 * Loads a camp from its configuration file.
	 * @param cf
	 * @return House
	 */
	public static House load(YamlConfiguration cf){
		String name;
		Instant creation;
		name = cf.getString("Name");
		
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		
		House h = new House(name, Banner.parse(cf.getString("Banner")));
		h.setCreationDate(creation);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			h.addMember(Bukkit.getServer().getOfflinePlayer(UUID.fromString((cf.getString("Members."+i)))));
			i+=1;
		}
		
		return h;
	}
	/**
	 * Saves the camp to its file.
	 * @return true if the camp has been saved, false otherwise.
	 */
	@Override
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		fc.set("Name", getName());
		fc.set("Creation", getCreationDate().toString());
		fc.set("Banner", getBanner().toString());
		int i = 0;
		for(UUID id : getMembers()){
			fc.set("Members." + i, id.toString());
			i += 1;
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
