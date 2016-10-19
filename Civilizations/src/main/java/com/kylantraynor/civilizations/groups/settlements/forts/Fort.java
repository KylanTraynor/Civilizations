package com.kylantraynor.civilizations.groups.settlements.forts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.BannerOwner;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class Fort extends Settlement implements BannerOwner{

	private int influence = 10;
	private House house;
	
	public Fort(Location l, House house) {
		super(l);
		this.house = house;
		setName("Fort");
		Cache.fortListChanged = true;
	}
	
	public Fort() {
		super();
		Cache.fortListChanged = true;
	}

	@Override
	public String getType(){
		return "Fort";
	}
	
	public static List<Fort> getAll(){
		return Cache.getFortList();
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
	
	@Override
	public void update(){
		super.update();
	}
	
	/**
	 * Destroys this settlement.
	 * @return true if the settlement has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		Cache.fortListChanged = true;
		return super.remove();
	}
	
	/**
	 * Gets the file where this fort is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getFortDirectory(), "" + getId() + ".yml");
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
