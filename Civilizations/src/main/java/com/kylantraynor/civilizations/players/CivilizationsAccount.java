package com.kylantraynor.civilizations.players;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.AccountMenu;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.settings.AccountSettings;

/**
 * A class used to reference all the characters of a player.
 * Each player only has one account.
 * 
 * @author Baptiste Jacquet
 *
 */
public class CivilizationsAccount {
	private static Map<String, CivilizationsAccount> accounts = new HashMap<String, CivilizationsAccount>();
	private static final File Directory = new File(Civilizations.currentInstance.getDataFolder(), "Accounts");
	
	private UUID currentCharacter;
	private UUID playerId;
	private AccountSettings settings = new AccountSettings();
	
	public CivilizationsAccount(Player p){
		this(p.getUniqueId());
	}
	
	public CivilizationsAccount(UUID playerId){
		this.playerId = playerId;
		try {
			settings.load(getFile());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		settings.setPlayerId(playerId);
		accounts.put(playerId.toString(), this);
	}

	/**
	 * Gets the player id of this account.
	 * @return
	 */
	public UUID getPlayerId() {
		return playerId;
	}
	
	/**
	 * Gets the {@linkplain OfflinePlayer} attached to this account.
	 * @return
	 */
	public OfflinePlayer getOfflinePlayer(){
		return Bukkit.getOfflinePlayer(playerId);
	}
	
	/**
	 * Gets all the {@linkplain UUID}s of the {@linkplain CivilizationsCharacters}
	 * attached to this account.
	 * @return
	 */
	public UUID[] getCharacterIds() {
		return settings.getCharacterIds();
	}
	
	/**
	 * Gets the current {@linkplain CivilizationsCharacter}'s {@linkplain UUID}.
	 * @return {@link UUID} or {@link Null}
	 */
	public UUID getCurrentCharacterId() {
		return currentCharacter;
	}
	/**
	 * Gets the {@linkplain CivilizationsCharacter} with the given {@linkplain UUID}.
	 * @param id as {@link UUID}
	 * @return {@link CivilizationsCharacter} or {@link Null}
	 */
	public CivilizationsCharacter getCharacter(UUID id) {
		if(id == null) return null;
		CivilizationsCharacter cc = (CivilizationsCharacter) CivilizationsCharacter.getOrNull(id);
		if(cc == null){
			cc = settings.getCharacter(id);
		}
		return cc;
	}
	
	/**
	 * Gets the current {@linkplain CivilizationsCharacter} attached to this account.
	 * @return {@link CivilizationsCharacter} or {@link Null}
	 */
	public CivilizationsCharacter getCurrentCharacter(){
		return getCharacter(currentCharacter);
	}
	
	/**
	 * Creates a new character with a random UniqueID, and attaches it to the account.
	 * @return
	 */
	public CivilizationsCharacter createNewCharacter(){
		CivilizationsCharacter result = new CivilizationsCharacter(this);
		settings.setCharacter(result);
		return result;
	}
	
	/**
	 * Removes the {@linkplain CivilizationsCharacter} with the given ID from the list of
	 * characters attached to this account.
	 * @param id as {@link UUID}
	 * @return true if the character has been removed, false otherwise.
	 */
	public boolean removeCharacter(UUID id){
		return settings.removeCharacter(id);
	}

	/**
	 * Changes the current {@linkplain CivilizationsCharacter} for this account,
	 * and changes the {@linkplain PlayerInventory}, the {@linkplain EnderChestInventory} and its {@linkplain Location}.
	 * @param c
	 * @return The old {@link CivilizationsCharacter} if there was one and {@link Null} if there was none
	 * or if the given character was not from this account.
	 */
	public CivilizationsCharacter setCurrentCharacter(CivilizationsCharacter c) {
		if(c.getAccountId() != playerId) return null;
		OfflinePlayer op = c.getOfflinePlayer();
		CivilizationsCharacter old = null;
		if(currentCharacter != null){
			old = (CivilizationsCharacter) CivilizationsCharacter.getOrNull(currentCharacter);
		}
		this.currentCharacter = c.getUniqueId();
		if(op.isOnline()){
			Player p = op.getPlayer();
			if(old != null){
				old.getInventory().setContents(p.getInventory().getContents());
				old.getInventory().setArmorContents(p.getInventory().getArmorContents());
				old.getEnderChest().setContents(p.getEnderChest().getContents());
				old.setLocation(p.getLocation());
			}
			p.getInventory().setContents(c.getInventory().getContents());
			p.getInventory().setArmorContents(c.getInventory().getArmorContents());
			p.getEnderChest().setContents(c.getEnderChest().getContents());
			p.teleport(c.getLocation(), TeleportCause.PLUGIN);
			this.saveCharacter(old);
		}
		return old;
	}

	/**
	 * Saves all the data relative to the given {@linkplain CivilizationsCharacter}.
	 * The character must belong to the account. Saving will fail otherwise.
	 * @param character as {@link CivilizationsCharacter}
	 */
	private void saveCharacter(CivilizationsCharacter character) {
		if(character.getAccountId() != playerId) return;
		settings.setCharacter(character);
		save();
	}
	
	/**
	 * Saves all the data relative to this account.
	 * @return
	 */
	private boolean save(){
		try {
			settings.save(getFile());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Gets the {@linkplain File} where the data relative to his account is saved.
	 * @return {@link File}
	 */
	private File getFile(){
		if(!Directory.exists()){
			Directory.mkdirs();
		}
		File f = new File(Directory, this.playerId.toString() + "yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
	
	/**
	 * Opens an inventory menu for the given player.
	 * @param player
	 * @return {@link AccountMenu}
	 */
	public AccountMenu openMenu(Player player){
		return (AccountMenu)MenuManager.openMenu(new AccountMenu(this), player);
	}
	
	/**
	 * Saves the data of the current {@linkplain CivilizationsCharacter}
	 * and of the {@linkplain CivilizationsAccount}, and removes
	 * it from the list of active accounts.
	 */
	public void logout(){
		if(this.currentCharacter != null){
			CivilizationsCharacter c = this.getCurrentCharacter();
			c.update();
			this.settings.setCharacter(c);
		} else {
			OfflinePlayer op = Bukkit.getOfflinePlayer(getPlayerId());
			if(op.isOnline()){
				Player p = op.getPlayer();
				settings.setBaseInventory(p.getInventory().getContents());
				settings.setBaseArmor(p.getInventory().getArmorContents());
				settings.setBaseEnderChest(p.getEnderChest().getContents());
				settings.setBaseLocation(p.getLocation());
			}
		}
		this.save();
		accounts.remove(playerId.toString());
	}
	
	/**
	 * Gets the {@linkplain CivilizationsAccount} associated to the given player ID,
	 * or creates one if none exists.
	 * @param playerId as {@link UUID}
	 * @return {@link CivilizationsAccount}
	 */
	public static CivilizationsAccount get(UUID playerId) {
		CivilizationsAccount ca = accounts.get(playerId.toString());
		if(ca == null){
			ca = new CivilizationsAccount(playerId);
		}
		return ca;
	}
	
	/**
	 * Saves the data of the current {@linkplain CivilizationsCharacter}
	 * and of the {@linkplain CivilizationsAccount} of this player, and removes
	 * it from the list of active accounts. 
	 * @param p as {@link Player}
	 */
	public static CivilizationsAccount logout(Player p){
		CivilizationsAccount ca = accounts.get(p.getUniqueId().toString());
		if(ca != null){
			ca.logout();
		}
		return ca;
	}
	
	/**
	 * Reloads the data of the account attached to the given {@linkplain Player}
	 * then adds the resulting {@linkplain CivilizationsAccount} to the list of active accounts.
	 * @param p as {@link Player}
	 * @return {@link CivilizationsAccount}
	 */
	public static CivilizationsAccount login(Player p){
		CivilizationsAccount ac = get(p.getUniqueId());
		if(ac.getCurrentCharacterId() == null){
			Location loc = ac.settings.getBaseLocation();
			ItemStack[] inventory = ac.settings.getBaseInventory();
			ItemStack[] armor = ac.settings.getBaseArmor();
			ItemStack[] ec = ac.settings.getBaseEnderChest();
			if(loc != null && inventory != null && armor != null && ec != null){
				p.teleport(loc, TeleportCause.PLUGIN);
				p.getInventory().setContents(inventory);
				p.getInventory().setArmorContents(armor);
				p.getEnderChest().setContents(ec);
			}
		} else {
			CivilizationsCharacter current = ac.getCurrentCharacter();
			current.restore();
		}
		return ac;
	}
	
	/**
	 * Saves the data of {@linkplain CivilizationsAccount} and current
	 * {@linkplain CivilizationsCharacter} for all the active accounts.
	 */
	public static void logoutAllPlayers(){
		for(CivilizationsAccount ca : accounts.values().toArray(new CivilizationsAccount[0])){
			if(ca != null){
				ca.logout();
			}
		}
	}
}