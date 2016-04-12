package com.kylantraynor.civilizations.groups.settlements.forts;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;

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
	 * Gets the file where this fort is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File dir = new File(Civilizations.getFortDirectory(), this.getClass().toString());
		if(!dir.exists()){
			dir.mkdir();
		}
		File f = new File(dir, "" + getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
}
