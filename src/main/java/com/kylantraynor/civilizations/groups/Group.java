package com.kylantraynor.civilizations.groups;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.kylantraynor.civilizations.exceptions.RecursiveParentException;
import com.kylantraynor.civilizations.managers.AccountManager;
import com.kylantraynor.civilizations.utils.DoubleIdentifier;
import com.kylantraynor.civilizations.utils.Identifier;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import mkremins.fanciful.civilizations.FancyMessage;

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
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.settings.GroupSettings;

/**
 * A group contains members (players).
 * @author Baptiste
 *
 */
public class Group extends EconomicEntity implements Comparable<Group>{
	
	private static Map<String, Group> all = new HashMap<>();
	public static Collection<Group> getList() {return all.values();}
	
	public static void clearAll(){
		all.clear();
	}

	private boolean hasChanged = true;
	private ChatColor chatColor;
	private GroupSettings settings;
	
	@Override
	public boolean isPlayer(){return false;}
	@Override
	public OfflinePlayer getOfflinePlayer(){return null;}
	
	public Group(){
		initSettings();
		init();
		getSettings().setCreationDate(Instant.now());
		all.put(getIdentifier().toString(), this);
		setChanged(true);
	}
	
	/**
	 * Creates a Group using the data in the given {@linkplain GroupSettings} file.
	 * @param settings The {@link GroupSettings} to load from.
	 */
	public Group(GroupSettings settings){
		this.settings = settings;
		init();
		all.put(getIdentifier().toString(), this);
		clearGhostMembers();
		setChanged(true);
	}
	
	public void init(){
		chatColor = ChatColor.WHITE;
	}

	@Override
	public UUID getIdentifier(){
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
	 * @param newName The new name.
	 */
	public void setName(String newName){ getSettings().setName(newName); }

	
	/**
	 * Gets the color of this group's chat.
	 * @return ChatColor
	 */
	public ChatColor getChatColor(){return chatColor;}

	/**
	 * Sets the color of this group's chat.
	 * @param newColor The new {@link ChatColor}.
	 */
	public void setChatColor(ChatColor newColor){chatColor = newColor;}

	/**
	 * Gets the group with the given Unique ID.
	 * @param id as {@link Identifier}
	 * @return Group
	 */
	public static Group get(UUID id){
		if(id == null) throw new NullPointerException("ID can't be null!");
		return all.get(id.toString());
	}

	/**
	 * Gets the list of all the members of this group.
	 * @return Set of {@link UUID} of the members
	 */
	public Set<UUID> getMembers() {return this.getSettings().getMembers();}

    /**
     * Gets an array of all the members of this group.
     * @return Array of {@link UUID}.
     */
	public UUID[] getMembersArray(){
	    return getMembers().toArray(new UUID[getMembers().size()]);
    }

	/**
	 * Sets the list of all the members of this group.
	 * @param members The new list of members.
	 */
	public void setMembers(Set<UUID> members) { this.getSettings().setMembers(members); }

    /**
     * Clears all the {@linkplain Group}'s members.
     */
	public void clearMembers(){
	    this.getSettings().setMembers(new TreeSet<>());
    }

    public int clearGhostMembers(){
	    UUID[] members = getMembersArray();
	    int count = 0;
	    for(UUID id : members){
	        EconomicEntity e = EconomicEntity.get(id);
	        if(e.isGhost()){
	            if(this.removeMember(e)){
	                count++;
                }
            }
        }
        if(count > 0) Civilizations.DEBUG("Removed " + count + " ghost members from " + getName() + " (" + getIdentifier().toString()+ ")!");
        return count;
    }
	/**
	 * Adds the given {@linkplain OfflinePlayer} to the list of members of this {@linkplain Group}.
	 * @param member The {@link OfflinePlayer} to add.
	 * @return true if the player wasn't already in the list, false otherwise.
     * @throws NullPointerException if Member is null.
	 */
	public boolean addMember(OfflinePlayer member){
		if(member == null) throw new NullPointerException("Member can't be null.");
	    UUID id = AccountManager.getCurrentIdentifier(member);
        Set<UUID> members = getMembers();
        if(members.add(id)){
            setMembers(members);
            return true;
        } else {
            return false;
        }
	}
	
	/**
	 * Adds the given entity to the list of members of this group.
	 * @param member The {@link EconomicEntity} to add.
	 * @return true if the player wasn't already in the list, false otherwise.
	 */
	public boolean addMember(EconomicEntity member){
		Set<UUID> members = getMembers();
		if(members.add(member.getIdentifier())){
            setMembers(members);
		    return true;
        }
		return false;
	}

	/**
	 * Removes the given {@linkplain OfflinePlayer} from the list of members of this {@linkplain Group}.
	 * @param member The {@link OfflinePlayer} to remove.
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(OfflinePlayer member){
		UUID id = AccountManager.getCurrentIdentifier(member);
        Set<UUID> members = getMembers();
		if(members.remove(id)){
			setMembers(members);
			return true;
		}
		return false;
	}
	/**
	 * Removes the given entity from the list of members of this group.
	 * @param member The {@link EconomicEntity} to remove.
	 * @return true if the player has been removed, false otherwise.
	 */
	public boolean removeMember(EconomicEntity member){
        Set<UUID> members = getMembers();
		if(members.remove(member.getIdentifier())){
			setMembers(members);
			return true;
		}
		return false;
	}

	/**
     * Checks if the given {@linkplain OfflinePlayer} is a shallow member of this {@linkplain Group}.
     * @param player as {@link OfflinePlayer}
     * @return true if the player is a member, false otherwise.
     */
	@Deprecated
    public boolean isMember(OfflinePlayer player){
        return isMember(player, false);
    }

    /**
     * Checks if the given {@linkplain OfflinePlayer} is a member of this {@linkplain Group}.
     * @param player as {@link OfflinePlayer}
     * @param recursive true for deep, false for shallow.
     * @return true if the player is a member, false otherwise.
     */
    public boolean isMember(OfflinePlayer player, boolean recursive){
        UUID id = AccountManager.getCurrentIdentifier(player);
        return isMember(id, recursive);
    }

	/**
	 * Checks if the given entity is a member of this group.
	 * @param entity The {@link EconomicEntity} to check membership of.
	 * @return true if the entity is a member, false otherwise.
	 */
	@Deprecated
	public boolean isMember(EconomicEntity entity){
		return isMember(entity.getIdentifier());
	}

	/**
	 * Checks if the given {@linkplain UUID} is a shallow member of this group.
	 * @param id the {@link UUID} to check.
	 * @return true if is a member, false otherwise.
	 */
	@Deprecated
	public boolean isMember(UUID id){
		return isMember(id, false);
	}

	/**
	 * Checks (potentially recursively) if the given {@linkplain UUID}
	 * is a part of this {@linkplain Group}.
	 * @param id the {@link UUID} to test.
	 * @param recursive whether or not the test should check child groups.
	 * @return true if the given {@link} belongs to his {@link Group} or any child {@link Group}.
	 */
	public boolean isMember(UUID id, boolean recursive){
		return isMember(id, recursive, new ArrayList<Group>());
	}

    /**
     * Checks (potentially recursively) if the given {@linkplain UUID}
     * is a part of this {@linkplain Group} while making sure to prevent infinite loops.
     * @param id {@link UUID} to test
     * @param recursive Whether or not the test should check child groups
     * @param checkedGroups The list of {@link Group Groups} already checked
     *                      <br/>Should initially be an empty group
     * @return {@code true} if the given {@link UUID} belongs to this {@link Group} or any child {@link Group}
     * and {@code false} otherwise
     */
	private boolean isMember(UUID id, boolean recursive, List<Group> checkedGroups){
	    if(checkedGroups.contains(this)) return false; // To prevent infinite loops;

		boolean result = getMembers().contains(id);
		Civilizations.DEBUG(String.format("ID %s belongs to %s? %s", id.toString(), this.getName(), result));
		if(recursive && !result){
		    checkedGroups.add(this);
			for(Group g : getList()){
				if(result) break;
				if(g.getParentId() != null && g.getParent() == this){
				    result = g.getIdentifier().equals(id) || g.isMember(id, true, checkedGroups);
				}
			}
            Civilizations.DEBUG(String.format("ID %s belongs recursively to %s? %s", id.toString(), this.getName(), result));
		}
		return result;
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
	 * @return The list of online members.
	 */
	public List<Player> getOnlinePlayers(){
		List<Player>  l = new ArrayList<Player>();
		for(UUID i : getMembers()){
		    EconomicEntity en = EconomicEntity.get(i);
		    if(en.isPlayer()){
		        if(en.getOfflinePlayer().isOnline()){
		            l.add(en.getOfflinePlayer().getPlayer());
                }
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
		boolean result = all.remove(getIdentifier().toString()) != null;
		return result;
	}
	/**
	 * Gets the File where this Group is saved.
	 * @return File
	 */
	public File getFile(){
		File f = new File(Civilizations.getGroupDirectory(), "" + this.getIdentifier() + ".yml");
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
	 * @param g The {@link Group} to get the name of.
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
	 * @param file The {@link File} to extract data from.
	 * @param group The {@link Group} to put data into.
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
		/*if(getProtection() != null){
			getSettings().saveProtection(getProtection());
		}*/
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
	 * @return true if the group has changed, false otherwise.
	 */
	public boolean isChanged() {
		return hasChanged;
	}
	/**
	 * Sets if the groups has changed or not.
	 * @param hasChanged Value to set.
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
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getIdentifier().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getIdentifier().toString() + " members");
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
	 * @param player {@link Player} who's checking for the actions.
	 * @return The list of actions.
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
	 * @param page The displayed page.
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveMembersList(int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle("MEMBERS", null));
		UUID[] members = getMembers().toArray(new UUID[getMembers().size()]);
		for(int i = 8 * (page - 1); i < members.length && i < 8 * (page); i+=1){
		    EconomicEntity en = EconomicEntity.get(members[i]);
			fm.then("\n" + en.getName());
			if(en.isPlayer()){
                if(en.getOfflinePlayer().isOnline()){
                    fm.color(ChatColor.GREEN);
                } else {
                    fm.color(ChatColor.GRAY);
                }
                fm.command("/p " + en.getName());
            } else {
                fm.color(ChatColor.GOLD);
                fm.command("/group " + members[i].toString() + " INFO");
            }
			/*Rank pr = getProtection().getRank(p);
			if(pr != null){
				fm.then(" (" + pr.getName() + ")");
				fm.color(ChatColor.GOLD);
				fm.command("/group " + this.getUniqueId().toString() + " rank " + pr.getName() + " members");
			}*/
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getIdentifier().toString() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getIdentifier().toString() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	/**
	 * Gets a fancy message showing the list of rank members for the given rank.
	 * @param r The {@link Group} to display.
	 * @param page The displayed page.
	 * @return A {@link FancyMessage} to display.
	 */
	public FancyMessage getInteractiveRankMembers(Group r, int page){
		if(page < 1) page = 1;
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(r.getName().toUpperCase(), null));
		UUID[] members = r.getMembers().toArray(new UUID[r.getMembers().size()]);
		for(int i = 8 * (page - 1); i < members.length && i < 8 * (page); i+=1){
			EconomicEntity p = EconomicEntity.get(members[i]);
			fm.then("\n" + p.getName());
			if(p.isPlayer()){
			    if(p.getOfflinePlayer().isOnline()) {
			        fm.color(ChatColor.GREEN);
                } else {
			        fm.color(ChatColor.GRAY);
                }
			} else {
				fm.color(ChatColor.GOLD);
			}
			fm.command("/p " + p.getName());
		}
		fm.then("\n<- Previous");
		if(page > 1){
			fm.color(ChatColor.BLUE).command("/group " + this.getIdentifier().toString() + " members " + (page - 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("" + page).color(ChatColor.GOLD);
		fm.then(" - ").color(ChatColor.GRAY);
		fm.then("Next ->");
		if(page < getMembers().size() / 8){
			fm.color(ChatColor.BLUE).command("/group " + this.getIdentifier().toString() + " members " + (page + 1));
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GOLD);
		return fm;
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message The {@link FancyMessage} to send.
	 * @param permission The {@link PermissionType} required to see it.
	 */
	public void sendMessage(FancyMessage message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
                if(!ProtectionManager.hasPermission(permission, this, p, true).getResult()) continue;
			}
			message.send(p);
		}
	}
	/**
	 * Sends a message to the members of the group with the given permission.
	 * @param message The {@link String} to send.
	 * @param permission The {@link PermissionType} required to see it.
	 */
	public void sendMessage(String message, PermissionType permission) {
		for(Player p : getOnlinePlayers()){
			if(permission != null){
				if(!ProtectionManager.hasPermission(permission, this, p, true).getResult()) continue;
			}
			p.sendMessage(getChatHeader() + getChatColor() + message);
		}
	}
	/**
	 * Checks if the given player has a certain permission.
     * @deprecated Use {@link ProtectionManager#hasPermission(PermissionType, Group, OfflinePlayer, boolean)} instead.
	 * @param perm
	 * @param block
	 * @param player
	 * @return
	 */
	public boolean hasPermission(PermissionType perm, Block block, Player player) {
		return ProtectionManager.hasPermission(perm, this, player, true).getResult();
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
	 * Gets the main info panel for the given rank.
	 * @param playerRank
	 * @return
	 */
	public FancyMessage getInteractiveRankPanel(Group playerRank) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(playerRank.getName().toUpperCase(), null));
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getIdentifier().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getIdentifier().toString() + " rank " + playerRank.getName() + " members");
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
	 * Gets the tax information of the given type.
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
        setParentId(group.getIdentifier());
        } else setParentId(null);
	}

    /**
     * Checks recursively if the given {@linkplain Group} is a parent
     * of this {@linkplain Group}
     * @param potentialParent as {@link Group}
     * @return true if the given {@link Group} is a parent, false otherwise.
	 * @throws NullPointerException potentialParent is null.
     */
	public boolean hasRecursiveParent(Group potentialParent) {
		if(potentialParent == null) throw new NullPointerException("potentialParent can't be null.");
		if(getParentId() == null) return false;
        return getParentId().equals(potentialParent.getIdentifier()) ||
                getParentId() != null && getParent().hasRecursiveParent(potentialParent);
    }

	/**
	 * Gets the parent's id, if it exists.
	 * @return {@link UUID}
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

    @Override
    public int compareTo(Group o) {
        int result = getType().compareTo(o.getType());
        if(result == 0) result = getName().compareTo(o.getName());
        if(result == 0) result = getIdentifier().compareTo(o.getIdentifier());
        return result;
    }
}