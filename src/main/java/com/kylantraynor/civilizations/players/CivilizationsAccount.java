package com.kylantraynor.civilizations.players;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.managers.AccountManager;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.settings.CivilizationsSettings;
import com.kylantraynor.civilizations.utils.DoubleIdentifier;
import com.kylantraynor.civilizations.utils.Identifier;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.AccountMenu;
import com.kylantraynor.civilizations.settings.AccountSettings;

/**
 * A class used to reference all the characters of a player.
 * Each player only has one account.
 * 
 * @author Baptiste Jacquet
 *
 */
public class CivilizationsAccount {
	
	private UUID currentCharacter;
	private final AccountSettings settings;

	public CivilizationsAccount(AccountSettings settings){
	    this.settings = settings;
    }

    public AccountSettings getSettings() {
        return settings;
    }

    /**
	 * Gets the player id of this account.
	 * @return
	 */
	public UUID getPlayerId() {
		return settings.getPlayerId();
	}
	
	/**
	 * Gets the {@linkplain OfflinePlayer} attached to this account.
	 * @return
	 */
	public OfflinePlayer getOfflinePlayer(){
		return Bukkit.getOfflinePlayer(settings.getPlayerId());
	}
	
	/**
	 * Gets all the {@linkplain DoubleIdentifier}s of the {@linkplain CivilizationsCharacter}
	 * attached to this account.
	 * @return
	 */
	public UUID[] getCharacterIds() {
		return settings.getCharacterIds();
	}
	
	/**
	 * Gets the current {@linkplain CivilizationsCharacter}'s {@linkplain UUID}.
	 * @return {@link UUID} or Null
	 */
	public UUID getCurrentCharacterId() {
		return currentCharacter;
	}
	
	/**
	 * Gets the current {@linkplain CivilizationsCharacter} attached to this account.
	 * @return {@link CivilizationsCharacter} or Null
	 */
	public CivilizationsCharacter getCurrentCharacter(){
		try{
		    return AccountManager.getCharacter(currentCharacter);
		} catch (ExecutionException ex) {
		    ex.printStackTrace();
		    return null;
		}
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
	 * and changes the {@linkplain PlayerInventory}, the Ender Chest {@linkplain Inventory} and its {@linkplain Location}.
	 * @param c
	 * @return The old {@link CivilizationsCharacter} if there was one and Null if there was none
	 * or if the given character was not from this account.
	 */
	public CivilizationsCharacter setCurrentCharacter(CivilizationsCharacter c) {
		if(c.getAccountId().equals(settings.getPlayerId())) return null;
		OfflinePlayer op = c.getOfflinePlayer();
		CivilizationsCharacter old = null;
		if(settings.getCurrentId() != null){
			try{ old = AccountManager.getCharacter(settings.getCurrentId()); } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
		}
		settings.setCurrentId(c.getIdentifier());
		if(op.isOnline()){
			Player p = op.getPlayer();
			if(old != null){
				old.setInventory(p.getInventory().getContents());
				old.setArmor(p.getInventory().getArmorContents());
				old.setEnderChest(p.getEnderChest().getContents());
				old.setLocation(p.getLocation());
				old.getSettings().save();
			}
			p.getInventory().setContents(c.getInventory());
			p.getInventory().setArmorContents(c.getArmor());
			p.getEnderChest().setContents(c.getEnderChest());
			p.teleport(c.getLocation(), TeleportCause.PLUGIN);
			;
		}
		return old;
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
			c.getSettings().save();
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
		this.settings.save();
		AccountManager.deactivateAccount(this.getPlayerId());
	}

    public CivilizationsCharacterFactory getCharacterFactory(){
	    return new CivilizationsCharacterFactory(getPlayerId());
    }
}