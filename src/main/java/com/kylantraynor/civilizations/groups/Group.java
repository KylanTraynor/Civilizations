package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import com.kylantraynor.civilizations.exceptions.RecursiveParentException;
import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.economy.TaxBase;
import com.kylantraynor.civilizations.economy.TaxInfo;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.settings.GroupSettings;

/**
 * A group contains members (players).
 * @author Baptiste
 *
 */
public class Group extends EconomicEntity{
	
	private static Map<String, Group> all = new HashMap<String, Group>();
	//private static ArrayList<Group> list = new ArrayList<Group>();
	public static Collection<Group> getList() {return all.values();}
	
	public static Stack<Integer> availableIds = new Stack<Integer>();
	
	public static void clearAll(){
		all.clear();
	}
	
	private int id;
	private boolean hasChanged = true;
	protected Protection protection;
	private ChatColor chatColor;
	private GroupSettings settings;
	private UUID parent;
	
	@Override
	public boolean isPlayer(){return false;}
	@Override
	public OfflinePlayer getOfflinePlayer(){return null;}
	
	public Group(){
		initSettings();
		init();
		getSettings().setCreationDate(Instant.now());
		all.put(getUniqueId().toString(), this);
		setChanged(true);
	}
	
	/**
	 * Creates a Group using the data in the given {@linkplain GroupSettings} file.
	 * @param settings
	 */
	public Group(GroupSettings settings){
		this.settings = settings;
		init();
		all.put(getUniqueId().toString(), this);
		setChanged(true);
	}
	
	public void init(){
		chatColor = ChatColor.WHITE;
		initProtection();
	}
	
	public void initProtection(){
		protection = new Protection(getUniqueId());
	}
	
	public UUID getUniqueId(){
		return getSettings().getUniqueId();
	}
	
	public void initSettings(){
		setSettings(new GroupSettings());
	}
	
	public String getChatHeader(){
		return ChatColor.GOLD + "[" + chatColor + getName() + ChatColor.GOLD + "] " + chatColor; 
	}
	
	/**
	 * Gets the group's name.
	 * @return String
	 */
	public String getName(){ return getSettings().getName(); }
	/**
	 * Sets the group's name.
	 * @param newName
	 */
	public void setName(String newName){ getSettings().setName(newName); } 
	
	/**
	 * Gets the color of this group's chat.
	 * @return ChatColor
	 */
	public ChatColor getChatColor(){return chatColor;}
	/**
	 * Sets the color of this group's chat.
	 * @param newColor
	 */
	public void setChatColor(ChatColor newColor){chatColor = newColor;}
	/**
	 * Gets the ID of this group.
	 * @return Integer
	 */
	public int getId() {return id;}
	/**
	 * Sets the ID of this group.
	 * @param id
	 */
	public void setId(int id) {this.id = id;}
	/**
	 * Gets the group with the given ID.
	 * @deprecated Use get(UUID) instead.
	 * @param id
	 * @return Group
	 */
	public static Group get(int id){
		for(Group g : all.values()){
			if(g.getId() == id) return g;
		}
		return null;
	}
	/**
	 * Gets the group with the given Unique ID.
	 * @param uid as {@link UUID}
	 * @return Group
	 */
	public static Group get(UUID uid){
		return all.get(uid.toString());
	}
	/**
	 * Gets the protection of this group.
	 * @return¨Protection
	 */
	public Protection getProtection() {return protection;}
	/**
	 * Sets the protection of this group.
	 * @param protection
	 */
	public void setProtection(Protection protection) {
		this.protection = protection;
		setChanged(true);
	}
	/**
	 * Gets the list of all the members of this group.
	 * @return List<UUID> of the members
	 */
	public List<UUID> getMembers() {return this.getSettings().getMembers();}
	/**
	 * Sets the list of all the members of this group.
	 * @param members
	 */
	public void setMembers(List<UUID> members) { this.getSettings().setMembers(members); }
	/**
	 * Adds the given {@linkplain OfflinePlayer} to the list of members of this {@linkplain Group}.
	 * @param member
	 * @return true if the player wasn't already in the list, false otherwise.
	 */
	public boolean addMember(OfflinePlayer member){
		CivilizationsAccount account = CivilizationsAccount.get(member.getUniqueId());
		if(account.getCurrentCharacterId() != null){
			if(getMembers().contains(account.getCurrentCharacterId())) return false;
		} else {
			if(getMembers().contains(account.getPlayerId())) return false;
		}
		List<UUID> members = getMembers();
		if(account.getCurrentCharacterId() != null){
			members.add(account.getCurrentCharacterId());
		} else {
			members.add(account.getPlayerId());
		}
		setMembers(members);
		return true;
	}
	
	/**
	 * Adds the given entity to the list of members of this group.
	 * @param member
	 * @return true if the player wasn't already in the list, false otherwise.
	 */
	public boolean addMember(EconomicEntity member){
		if(getMembers().contains(member.getUniqueId())) return false;
		List<UUID> members = getMembers();
		members.add(member.getUniqueId());
		setMembers(members);
		return true;
	}
	/**
	 * Removes the given {@linkplain OfflinePlayer} from the list of members of this {@linkplain Group}.
	 * @param member
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(OfflinePlayer member){
		CivilizationsAccount account = CivilizationsAccount.get(member.getUniqueId());
		UUID id = account.getPlayerId();
		if(account.getCurrentCharacterId() != null){
			id = account.getCurrentCharacterId();
		}
		if(getMembers().contains(id)){
			List<UUID> members = getMembers();
			members.remove(id);
			setMembers(members);
			return true;
		}
		return false;
	}
	/**
	 * Removes the given entity from the list of members of this group.
	 * @param member
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(EconomicEntity member){
		if(getMembers().contains(member.getUniqueId())){
			List<UUID> members = getMembers();
			members.remove(member.getUniqueId());
			setMembers(members);
			return true;
		}
		return false;
	}
	/**
	 * Checks if the given {@linkplain OfflinePlayer} is a member of this {@linkplain Group}.
	 * @param player as {@link OfflinePlayer}
	 * @return true if the player is a member, false otherwise.
	 */
	public boolean isMember(OfflinePlayer player){
		CivilizationsAccount account = CivilizationsAccount.get(player.getUniqueId());
		if(account.getCurrentCharacterId() != null){
			return getMembers().contains(account.getCurrentCharacterId());
		} else {
			return getMembers().contains(account.getPlayerId());
		}
	}
	/**
	 * Checks if the given entity is a member of this group.
	 * @param entity
	 * @return true if the entity is a member, false otherwise.
	 */
	public boolean isMember(EconomicEntity entity){
		return getMembers().contains(entity.getUniqueId());
	}
	/**
	 * Checks if at least one players in this list of members of this group is online.
	 * @return true if at least one player is online, false otehrwise.
	 */
	public boolean hasOneMemberOnline(){
		for(UUID i : getMembers()){
			EconomicEntity en = EconomicEntity.get(i);//Bukkit.getServer().getOfflinePlayer(i);
			if(en.isPlayer()){
				if(en.getOfflinePlayer().isOnline()){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Gets a list of all the online players of this group.
	 * @return List<Player> of online members.
	 */
	public List<Player> getOnlinePlayers(){
		List<Player>  l = new ArrayList<Player>();
		for(UUID i : getMembers()){
			OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(i);
			if(op.isOnline()){
				l.add(op.getPlayer());
			}
		}
		return l;
	}
	/**
	 * Destroys this Group.
	 * @return true if the group has been removed, false otherwise.
	 */
	public boolean remove() {
		Civilizations.DEBUG("Trying to remove " + getType() + ".");
		File f = getFile();
		if(f != null){
			if(f.exists()) f.delete();
		}
		boolean result = all.remove(getUniqueId().toString()) != null;
		return result;
	}
	/**
	 * Gets the File where this Group is saved.
	 * @return File
	 */
	public File getFile(){
		File f = new File(Civilizations.getGroupDirectory(), "" + this.getId() + ".yml");
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
	 * Gets the name of the given group.
	 * @param g
	 * @return "None" if the given group is Null.
	 */
	public static String getNameOf(Group g){
		if(g == null){
			return "None";
		} else {
			return g.getName();
		}
	}
	/**
	 * Loads the data from the file into the given group.
	 * @deprecated Use the constructor receiving a {@linkplain GroupSettings} instead.
	 * @param file
	 * @param group
	 * @return
	 */
	public static <T extends Group> T load(File file, T group){
		if(file == null) return null;
		try {
			group.getSettings().load(file);
			group.postLoad();
			return group;
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Do things right after the settings of the group are loaded.
	 * @deprecated Too confusing, put stuff into the constructor with {@linkplain GroupSettings} instead.
	 */
	public void postLoad(){ }
	/**
	 * Saves the group to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(getProtection() != null){
			getSettings().saveProtection(getProtection());
		}
		if(Civilizations.currentInstance.isEnabled()){
			getSettings().asyncSave(f);
		} else {
			getSettings().save(f);
		}
		return !getSettings().hasChanged();
	}
	/**
	 * Updates the group.
	 */
	public void update(){
		if(isChanged() || getSettings().hasChanged()){
			try{save();} catch (Exception e) {e.printStackTrace();};
		}
	}
	/**
	 * Checks if the group has changed.
	 * @return
	 */
	public boolean isChanged() {
		return hasChanged;
	}
	/**
	 * Sets if the groups has changed or not.
	 * @param hasChanged
	 */
	public void setChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	/**
	 * Gets an interactive info panel of this group.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getUniqueId().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getUniqueId().toString() + " members");
		fm.then("\nActions: (You can click on the action you want to do)\n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	/**
	 * Adds all the commands of this group to a clickable message.
	 * @param fm Message to add the commands to
	 * @param actions Actions to add
	 * @return Same as fm, with the added commands
	 */
	public FancyMessage addCommandsTo(FancyMessage fm, List<GroupAction> actions){
		if(actions.size() > 0){
			Iterator<GroupAction> it = actions.iterator();
			while(it.hasNext()){
				fm = it.next().addTo(fm);
				if(it.hasNext()){
					fm.then(" - ").color(ChatColor.GRAY);
				}
			}
		} else {
			fm.then("You can't do anything to this right now").color(ChatColor.GRAY);
		}
		
		return fm;
	}
	/**
	 * Gets all the commands available to this group.
	 * @param player
	 * @return
	 */
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		return list;
	}
	/**
	 * Gets an interactive list of the members of this group.
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveMembersList(){
		return getInteractiveMembersList(1);
	}
	/**
	 * Gets an interactive list of the members of this group.
	 * @param page
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveMembersList(int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("MEMBERS", null));
		for(int i = 8 * (page - 1); i < getMembers().size() && i < 8 * (page); i+=1){
			OfflinePlayer p = Civilizations.currentInstance.getServer().getOfflinePlayer(getMembers().get(i));
			fm.then("\n" + p.getName());
			if(p.isOnline()){
				fm.color(ChatColor.GREEN);
			} else {
				fm.color(ChatColor.GRAY);
			}
			fm.command("/p " + p.getName());
			Rank pr = getProtection().getRank(p);
			if(pr != null){
				fm.then(" (" + pr.getName() + ")");
				fm.color(ChatColor.GOLD);
				fm.command("/group " + this.getUniqueId().toString() + " rank " + pr.getName() + " members");
			}
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getUniqueId().toString() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getUniqueId().toString() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	/**
	 * Gets a fancy message showing the list of rank members for the given rank.
	 * @param r
	 * @param page
	 * @return
	 */
	public FancyMessage getInteractiveRankMembers(Rank r, int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(r.getName().toUpperCase(), null));
		for(int i = 8 * (page - 1); i < r.getPlayers().size() && i < 8 * (page); i+=1){
			OfflinePlayer p = r.getPlayers().get(i);
			fm.then("\n" + p.getName());
			if(p.isOnline()){
				fm.color(ChatColor.GREEN);
			} else {
				fm.color(ChatColor.GRAY);
			}
			fm.command("/p " + p.getName());
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getUniqueId().toString() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getUniqueId().toString() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GOLD);
		return fm;
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message
	 * @param permission
	 */
	public void sendMessage(FancyMessage message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!ProtectionManager.hasPermission(getProtection(), permission, p, false)) continue;
			}
			message.send(p);
		}
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message
	 * @param permission
	 */
	public void sendMessage(String message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!ProtectionManager.hasPermission(getProtection(), permission, p, false)) continue;
			}
			p.sendMessage(getChatHeader() + getChatColor() + message);
		}
	}
	/**
	 * Checks if the given player has a certain permission.
	 * @param perm
	 * @param block
	 * @param player
	 * @return
	 */
	@Deprecated
	public boolean hasPermission(PermissionType perm, Block block, Player player) {
		return ProtectionManager.hasPermission(getProtection(), perm, player, false);
		/*
		boolean result = false;
		if(player != null){
			return getProtection().hasPermission(player, perm);
		} else {
			result = getProtection().getPermission(perm, new PermissionTarget(TargetType.SERVER));
		}
		return result;*/
	}
	/**
	 * Checks if the given player has a certain rank.
	 * @param targetId of the rank.
	 * @param player
	 * @return
	 */
	public boolean hasRank(String targetId, Player player) {
		return getProtection().getRank(targetId).includes(player);
	}
	/**
	 * Gets the main info panel for the given rank.
	 * @param playerRank
	 * @return
	 */
	public FancyMessage getInteractiveRankPanel(Rank playerRank) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(playerRank.getName().toUpperCase(), null));
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getUniqueId().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getUniqueId().toString() + " rank " + playerRank.getName() + " members");
		//fm.then("\nActions: (You can click on the action you want to do)\n").color(ChatColor.GRAY);
		//fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	/**
	 * Gets the next taxed amount for the specific type of tax.
	 * @param tax The type of tax.
	 * @return double Amount
	 */
	public double getNextTaxationAmount(String tax) {
		Group parent = getParent();
		TaxInfo taxInfo;
		if(parent != null){
			taxInfo = parent.getTax(tax);
		} else {
			taxInfo = Civilizations.getSettings().getTaxInfo(tax);
		}
		if(taxInfo != null){
			return (((int) (calculateTax(taxInfo) * 100)) / 100.0);
		}
		return 0;
	}
	
	/**
	 * Gets the exact amount for th specific tax base.
	 * @param taxInfo
	 * @return
	 */
	public double calculateTax(TaxInfo taxInfo){
		switch(taxInfo.getBase()){
		case FromBalance:
			if(taxInfo.isPercent()){
				return this.getBalance() * (taxInfo.getValue() / 100.0);
			} else {
				return taxInfo.getValue();
			}
		case PerMember:
			if(taxInfo.isPercent()){
				double val = this.getBalance() * (taxInfo.getValue() / 100.0);
				return val * getMembers().size();
			} else {
				return taxInfo.getValue() * getMembers().size();
			}
		default:
			break;
		}
		return 0;
	}
	/**
	 * Gets the tax informations of the given type.
	 * @param tax
	 * @return
	 */
	public TaxInfo getTax(String tax) {
		return this.getSettings().getTaxInfo(tax);
	}
	/**
	 * Adds a new tax.
	 * @param tax
	 * @param base
	 * @param value
	 * @param isPercent
	 */
	public void setTax(String tax, TaxBase base, double value, boolean isPercent){
		this.getSettings().setTaxInfo(tax, base, value, isPercent);
	}
	/**
	 * Gets the names of all the taxes of this group.
	 * @return
	 */
	public Set<String> getTaxes(){
		return this.getSettings().getTaxes();
	}
	/**
	 * Processes all the taxes outgoing transfers for this group.
	 */
	public void processTaxes(){
		Group parent = getParent();
		Set<String> taxes;
		String taxTarget = "Server";
		if(parent != null){
			taxTarget = parent.getName();
			taxes = parent.getTaxes();
		} else {
			taxes = Civilizations.getSettings().getTaxes();
		}
		for(String tax : taxes){
			double transfer = getNextTaxationAmount(tax);
			if(Economy.tryTransferFunds(this, parent, taxTarget + " " + tax, transfer)){
				this.sendMessage(ChatColor.GREEN + "Payed tax " + tax + " to " + taxTarget + ": " + Economy.format(transfer), PermissionType.TAX_NOTIFICATIONS);
				Civilizations.currentInstance.getLogger().info(ChatColor.GREEN + "" + this.getName() + " payed " + tax + " tax to " + taxTarget + ": " + Economy.format(transfer));
			} else {
				this.sendMessage(ChatColor.RED + "Couldn't pay tax " + tax + " to " + taxTarget + ": " + Economy.format(transfer), PermissionType.TAX_NOTIFICATIONS);
				Civilizations.currentInstance.getLogger().info(ChatColor.RED + "" + this.getName() + " coudln't pay " + tax + " tax to " + taxTarget + ": " + Economy.format(transfer));
			}
		}
	}
	/**
	 * Gets the parent group of this group, if any.
	 * @return Null if this group has no parent.
	 */
	public Group getParent() {
		if(getParentId() != null){
			return GroupManager.get(getParentId());
		}
		return null;
	}
	/**
	 * Sets the parent group of this group.
     * @throws RecursiveParentException if the given {@link Group} is already a child of this one.
	 * @param group {@link Group} to set as the parent.
	 */
	public void setParent(Group group) throws RecursiveParentException {
        if (group != null){
            if (group.hasRecursiveParent(this)) {
                throw new RecursiveParentException(group, this);
            }
        setParentId(group.getUniqueId());
        } else setParentId(null);
	}

    /**
     * Checks recursively if the given {@linkplain Group} is a parent
     * of this {@linkplain Group}
     * @param potentialParent as {@link Group}
     * @return true if the given {@link Group} is a parent, false otherwise.
     */
	public boolean hasRecursiveParent(Group potentialParent) {
        return getParentId() == potentialParent.getUniqueId() ||
                getParentId() != null && getParent().hasRecursiveParent(potentialParent);
    }

	/**
	 * Gets the parent's id, if it exists.
	 * @return UUID
	 */
	public UUID getParentId(){
		return getSettings().getParentId();
	}
	/**
	 * Sets the parent's id.
	 * @param id
	 */
	public void setParentId(UUID id){
		getSettings().setParentId(id);
	}
	/**
	 * Opens an inventory menu for the given player.
	 * @param player
	 * @return GroupMenu
	 */
	public GroupMenu openMenu(Player player){
		return (GroupMenu)MenuManager.openMenu(new GroupMenu(this), player);
	}
	
	public String getType() {
		return "Group";
	}
	
	public boolean upgrade() {
		return false;
	}
	
	public GroupSettings getSettings() {
		return settings;
	}
	
	public void setSettings(GroupSettings settings) {
		this.settings = settings;
	}
	
}