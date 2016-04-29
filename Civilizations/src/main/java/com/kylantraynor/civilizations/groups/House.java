package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
	private String words = "We don't know what to say, so we just don't say it.";
	private UUID lord;
	private List<String> unloadedVassals = new ArrayList<String>();
	private List<House> vassals = new ArrayList<House>();
	
	public House(String name, Banner b) {
		super();
		setName(name);
		this.banner = b;
		setChanged(true);
		Cache.houseListChanged = true;
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
	 * Destroys this house.
	 * @return true if the house has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		Cache.houseListChanged = true;
		return super.remove();
	}
	
	/**
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage("========== HOUSE " + getName().toUpperCase() + " ==========").color(ChatColor.GOLD);
		fm.then("\nWords: ").color(ChatColor.GRAY).then(getWords()).color(ChatColor.GOLD);
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nCurrent Lord:").color(ChatColor.GRAY).
			then("" + getLordName()).color(ChatColor.GOLD);
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		fm.then("\nVassals: ").color(ChatColor.GRAY).command("/house " + this.getName() + " Vassals").
			then("" + getVassals().size()).color(ChatColor.GOLD).command("/house " + this.getName() + " Vassals");
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
	
	private String getLordName() {
		if(lord != null){
			OfflinePlayer p = Bukkit.getOfflinePlayer(lord);
			if(p != null){
				return p.getName();
			}
		}
		return "Unkown";
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
		String words;
		String lord;
		Instant creation;
		name = cf.getString("Name");
		words = cf.getString("Words");
		lord = cf.getString("Lord");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		
		House h = new House(name, Banner.parse(cf.getString("Banner")));
		h.setCreationDate(creation);
		if(words != null){
			h.setWords(words);
		}
		int i = 0;
		while(cf.contains("Members." + i)){
			h.addMember(Bukkit.getServer().getOfflinePlayer(UUID.fromString((cf.getString("Members."+i)))));
			i+=1;
		}
		
		i = 0;
		while(cf.contains("Vassals." + i)){
			h.addVassal(cf.getString("Vassals." + i));
		}
		if(lord != null){
			h.setLord(Bukkit.getOfflinePlayer(UUID.fromString(lord)));
		}
		
		return h;
	}
	
	public void addVassal(String string) {
		if(House.get(string) != null){
			addVassal(House.get(string));
		} else {
			unloadedVassals.add(string);
		}
	}
	
	public void addVassal(House house){
		vassals.add(house);
		unloadedVassals.add(house.getName());
		setChanged(true);
	}

	private void setLord(OfflinePlayer offlinePlayer) {
		if(offlinePlayer == null){
			lord = null;
		} else {
			lord = offlinePlayer.getUniqueId();
		}
		setChanged(true);
	}
	
	public OfflinePlayer getLord(){
		if(lord == null) return null;
		return Bukkit.getOfflinePlayer(lord);
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
		fc.set("Words", getWords());
		if(getLord() != null){
			fc.set("Lord", getLord().getUniqueId().toString());
		} else {fc.set("Lord", null);}
		
		for(int i = 0; i < getVassals().size(); i++){
			fc.set("Vassals." + i, getVassals().get(i).getName());
		}
		
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

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}
	
	public List<House> getVassals(){
		if(vassals.size() != unloadedVassals.size()){
			vassals = new ArrayList<House>();
			for(String s : unloadedVassals){
				if(House.get(s) != null){
					vassals.add(House.get(s));
				}
			}
			setChanged(true);
		}
		return vassals;
	}

	public FancyMessage getInteractiveVassalsList() {
		FancyMessage fm = new FancyMessage("========== HOUSE " + getName().toUpperCase() + " VASSALS ==========").color(ChatColor.GOLD);
		for(House h : getVassals()){
			fm.then("\n" + h.getName()).command("/House " + h.getName() + " INFO");
		}
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
}
