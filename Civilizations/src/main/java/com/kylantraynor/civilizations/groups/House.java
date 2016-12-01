package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.banners.BannerOwner;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.settings.HouseSettings;

/**
 * Family House, with all the members of the family.
 * @author Baptiste
 *
 */
public class House extends Group implements BannerOwner{
	
	public String getWords() { return getSettings().getWords(); }
	public void setWords(String words) { getSettings().setWords(words); }
	
	public List<House> getVassals(){ return getSettings().getVassals(); }
	
	public OfflinePlayer getLord(){ return getSettings().getLord(); }
	public void setLord(OfflinePlayer p){ getSettings().setLord(p); }
	
	public House(String name, Banner b) {
		super();
		setName(name);
		setBanner(b);
		CacheManager.houseListChanged = true;
	}
	
	public House() {
		super();
		CacheManager.houseListChanged = true;
	}
	
	@Override
	public void initSettings(){
		setSettings(new HouseSettings());
	}
	
	@Override
	public HouseSettings getSettings(){
		return (HouseSettings)super.getSettings();
	}
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + ChatColor.BLUE + ChatColor.BOLD + getName() + ChatColor.GOLD + "] "; 
	}
	/**
	 * Gets this House's banner.
	 */
	@Override
	public Banner getBanner() {
		return getSettings().getBanner();
	}
	/**
	 * Sets this House's banner.
	 */
	@Override
	public void setBanner(Banner newBanner) {
		getSettings().setBanner(newBanner);
	}
	/**
	 * Gets the list of all Houses.
	 * @return
	 */
	public static List<House> getAll(){
		return CacheManager.getHouseList();
	}
	/**
	 * Gets the house the given banner represents.
	 * @param banner
	 * @return
	 */
	public static House get(Banner banner) {
		for(House h : getAll()){
			if(h.getBanner().equals(banner)){
				return h;
			}
		}
		return null;
	}
	/**
	 * Gets the house the given player belongs to.
	 * @param p
	 * @return
	 */
	public static House get(OfflinePlayer p){
		for(House h : getAll()){
			if(h.isMember(p)){
				return h;
			}
		}
		return null;
	}
	/**
	 * Gets the House with the given name.
	 * @param name
	 * @return
	 */
	public static House get(String name){
		for(House h : getAll()){
			if(h.getName().equalsIgnoreCase(name)){
				return h;
			}
		}
		return null;
	}
	/**
	 * Destroys this house.
	 * @return true if the house has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		CacheManager.houseListChanged = true;
		return super.remove();
	}
	/**
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("HOUSE " + getName().toUpperCase(), null));
		fm.then("\nWords: ").color(ChatColor.GRAY).then(getWords()).color(ChatColor.GOLD);
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nCurrent Lord: ").color(ChatColor.GRAY).
			then("" + getLordName()).color(ChatColor.GOLD);
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		fm.then("\nVassals: ").color(ChatColor.GRAY).command("/house " + this.getName() + " Vassals").
			then("" + getVassals().size()).color(ChatColor.GOLD).command("/house " + this.getName() + " Vassals");
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	/**
	 * Gets the lord name of this House.
	 * @return Unknown if there is no lord.
	 */
	public String getLordName() {
		if(getSettings().getLord() != null){
			return getSettings().getLord().getName();
		}
		return "Unknown";
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
	 * Adds the given house to the list of vassals.
	 * @param string
	 * @return
	 */
	public boolean addVassal(String string) {
		if(House.get(string) != null){
			addVassal(House.get(string));
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Adds the given house to the list of vassals.
	 * @param house
	 * @return
	 */
	public boolean addVassal(House house){
		List<House> vassals = getSettings().getVassals();
		if(vassals.contains(house)){
			return false;
		} else {
			if(vassals.add(house)){
				getSettings().setVassals(vassals);
				return true;
			} else {
				return false;
			}
		}
	}
	/**
	 * Gets a FancyMessage showing the list of vassals.
	 * @return
	 */
	public FancyMessage getInteractiveVassalsList() {
		FancyMessage fm = new FancyMessage("========== HOUSE " + getName().toUpperCase() + " VASSALS ==========").color(ChatColor.GOLD);
		for(House h : getVassals()){
			fm.then("\n" + h.getName()).command("/House " + h.getName() + " INFO");
		}
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
}