package com.kylantraynor.civilizations.groups.settlements.forts;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.IHasBanner;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Fort extends Settlement implements IHasBanner{

	private int influence = 1;
	private House house;
	
	public Fort(Location l, House house) {
		super(l);
		this.house = house;
	}
	
	@Override
	public String getType(){
		return "Fort";
	}

	@Override
	public Banner getBanner() {
		return getHouse().getBanner();
	}

	@Override
	public void setBanner(Banner newBanner) {
		setHouse(House.get(newBanner));
	}

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
		updateAllBanners();
	}

	private void updateAllBanners() {
		
	}

	public int getInfluence() {
		return influence;
	}

	public void setInfluence(int influence) {
		this.influence = influence;
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
