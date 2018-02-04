package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.kylantraynor.civilizations.territories.HonorificTitle;
import com.kylantraynor.civilizations.territories.Influence;
import com.kylantraynor.civilizations.territories.InfluentEntity;
import com.kylantraynor.civilizations.util.Util;

/**
 * Family House, with all the members of the family.
 * @author Baptiste
 *
 */
public class House extends Group implements BannerOwner, InfluentEntity{
	
	private Influence influence = new Influence();

	public String getWords() { return getSettings().getWords(); }
	public void setWords(String words) { getSettings().setWords(words); }
	
	public List<House> getVassals(){ return getSettings().getVassals(); }
	
	public OfflinePlayer getLord(){ return getSettings().getLord(); }
	public void setLord(OfflinePlayer p){ getSettings().setLord(p); }
	
	public static FancyMessage getHousesListChatMessage(){
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("Noble Houses", ChatColor.GRAY));
		List<House> houses = getAll();
		houses.sort(getInfluenceComparator());
		
		for(int i = houses.size() - 1; i >= 0; i--){
			fm.then("\n");
			fm.then(ChatColor.GOLD + houses.get(i).getName() + " (Lord " + houses.get(i).getLordName() + ") Influence: " + houses.get(i).getInfluence().getTotalInfluence())
			.command("/group " + houses.get(i).getUniqueId().toString() + " info");
		}
		fm.then("\n");
		fm.then(ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
		
	}
	
	public static Comparator<House> getInfluenceComparator(){
		return (a, b) -> {
			if(a.getInfluence().getTotalInfluence() < b.getInfluence().getTotalInfluence()) return -1;
			if(a.getInfluence().getTotalInfluence() > b.getInfluence().getTotalInfluence()) return 1;
			return a.getName().compareToIgnoreCase(b.getName());
		};
	}
	
	public House(String name, Banner b) {
		super();
		setName(name);
		setBanner(b);
	}
	
	public House(HouseSettings settings){
		super(settings);
	}
	
	public House() {
		super();
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
	 * Gets the list of all Houses registered on the server.
	 * @return {@link List} of {@link House Houses} from {@link Group#getList()}.
	 */
	public static List<House> getAll(){
		List<House> result = new ArrayList<House>();
		for(Group g: Group.getList()){
			if(g instanceof House){
				result.add((House) g);
			}
		}
		return result;
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
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("House " + getName(), null));
		fm.then("\nWords: ").color(ChatColor.GRAY).then(getWords()).color(ChatColor.GOLD);
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nCurrent Lord: ").color(ChatColor.GRAY).
			then("" + getLordName()).color(ChatColor.GOLD).
			command("/p " + getLordName());
		fm.then("\nTitles: ").color(ChatColor.GRAY);
		for(HonorificTitle t : getTitles()){
			fm.then("\n\t" + t.getName()).color(ChatColor.GOLD);
		}
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getUniqueId().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getUniqueId().toString() + " members");
		fm.then("\nVassals: ").color(ChatColor.GRAY).command("/house " + this.getName() + " Vassals").
			then("" + getVassals().size()).color(ChatColor.GOLD).command("/house " + this.getName() + " Vassals");
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	/**
	 * Gets this house's list of Titles.
	 * @return
	 */
	private List<HonorificTitle> getTitles() {
		return getSettings().getTitles();
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
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("House " + getName() + " Vassals",null));
		for(House h : getVassals()){
			fm.then("\n" + h.getName()).command("/House " + h.getName() + " INFO");
		}
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	@Override
	public Influence getInfluence() {
		return this.influence;
	}
}