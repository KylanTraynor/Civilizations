package com.kylantraynor.civilizations.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;

/**
 * Represents an entity which can own a balance and take part in transactions.
 * @author Baptiste Jacquet
 */
public abstract class EconomicEntity {
	
	private static Map<String, EconomicEntity> entities = new HashMap<String, EconomicEntity>();
	
	private Budget budget;
	private double balance = 0;
	private UUID id;
	
	/**
	 * Creates a new EconomicEntity with a predefined {@link UUID}.
	 * @param id as {@link UUID}
	 */
	public EconomicEntity(UUID id){
		this.id = id;
		this.entities.put(id.toString(), this);
	}
	
	/**
	 * Creates a new EconomicEntity with a random {@link UUID}.
	 */
	public EconomicEntity(){
		this.id = UUID.randomUUID();
		this.entities.put(id.toString(), this);
	}
	
	/**
	 * Gets the {@link UUID} of this entity.
	 * @return {@link UUID}
	 */
	public UUID getUniqueId(){
		return id;
	}
	
	/**
	 * Sets the {@link UUID} of this entity. This will break the continuity with the persistent
	 * actions of this entity. Do not use unless you definitely know what you are doing.
	 * @param id as {@link UUID}
	 */
	public void setUniqueId(UUID id){
		this.id = id;
	}
	
	@Deprecated
	public Budget getBudget() {
		if(budget == null) budget = new Budget();
		return budget;
	}
	
	@Deprecated
	public void setBudget(Budget b){
		budget = b;
	}
	
	/**
	 * Gets the current balance of this entity.
	 * @return double
	 */
	public double getBalance(){
		if(Civilizations.useVault && isPlayer()){
			return Economy.getVault().getBalance(getOfflinePlayer());
		}
		return balance;
	}
	
	/**
	 * Sets the current balance of this entity.
	 * @param newBalance as double
	 */
	public void setBalance(double newBalance){
		if(Civilizations.useVault && isPlayer()){
			double diff = newBalance - getBalance();
			if(diff > 0){
				Economy.getVault().depositPlayer(getOfflinePlayer(), diff);
			} else {
				Economy.getVault().withdrawPlayer(getOfflinePlayer(), -diff);
			}
		}
		balance = newBalance;
	}
	
	/**
	 * Gives funds to this entity without adding a {@link BudgetEntry} to the database.
	 * @param amount as double
	 */
	public void giveFunds(double amount){
		setBalance(getBalance() + amount);
	}
	
	/**
	 * Takes funds from this entity without checking if it has them (adding debt if it's
	 * not the case) and without adding a {@link BudgetEntry} to the database.
	 * @param amount
	 */
	public void takeFunds(double amount){
		setBalance(getBalance() - amount);
	}
	
	// ===========================
	// Methods not needing saving
	// ===========================
	/**
	 * Checks if this entity actually represents an {@link OfflinePlayer}.
	 * @return true if the entity represents a player, false otherwise
	 */
	public boolean isPlayer(){
		return !(this instanceof Group);
	}
	
	/**
	 * Gets the {@link OfflinePlayer} this entity represents. Before calling this method,
	 * you should make sure this entity indeed represents a player by checking {@code isPlayer()}
	 * as this method will still return an object if this entity does not represent one.
	 * @return {@link OfflinePlayer}
	 */
	public OfflinePlayer getOfflinePlayer(){
		return Bukkit.getOfflinePlayer(this.getUniqueId());
	}
	
	/**
	 * Gets the name of this {@link EconomicEntity}, whether it's a player's name or a {@link Group}'s name.
	 * @return String
	 */
	public String getName(){
		if(isPlayer()){
			return getOfflinePlayer().getName();
		} else {
			return ((Group)this).getName();
		}
	}
	
	// ===========================
	// Static Methods
	// ===========================
	
	/**
	 * Gets the {@link EconomicEntity} associated to the given {@link UUID}.
	 * @param id as {@link UUID}
	 * @return {@link EconomicEntity}
	 */
	public static EconomicEntity get(UUID id){
		return entities.get(id.toString());
	}
}