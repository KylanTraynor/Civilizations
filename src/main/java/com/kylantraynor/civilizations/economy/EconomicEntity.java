package com.kylantraynor.civilizations.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.players.CivilizationsCharacter;

/**
 * Represents an entity which can own a balance and take part in transactions.
 * @author Baptiste Jacquet
 */
public class EconomicEntity {
	
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
			try{
				return Economy.getVault().getBalance(getOfflinePlayer());
			} catch (RuntimeException e){
				Civilizations.currentInstance.getLogger().warning("Error while trying to get " + getName() + "'s Vault Balance:");
				e.printStackTrace();
			}
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
		if(Civilizations.useVault && isPlayer()) {
			Civilizations.currentInstance.getLogger().info("Giving " + Economy.format(amount) + " to " + getName() + ".");
			Economy.getVault().depositPlayer(getOfflinePlayer(), amount);
			balance = getBalance() + amount;
			return;
		}
		setBalance(getBalance() + amount);
	}
	
	/**
	 * Takes funds from this entity without checking if it has them (adding debt if it's
	 * not the case) and without adding a {@link BudgetEntry} to the database.
	 * @param amount
	 */
	public void takeFunds(double amount){
		if(Civilizations.useVault && isPlayer()) {
			Economy.getVault().withdrawPlayer(getOfflinePlayer(), amount);
			balance = getBalance() - amount;
			return;
		}
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
		return true;
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
		}
		return "Entity";
	}

    /**
     * Checks if this {@linkplain EconomicEntity} belongs to the
     * {@linkplain EconomicEntity} with the given {@linkplain UUID}.
     * @param id as {@link UUID}
     * @return true if the given {@linkplain UUID} is the same as the
     * {@linkplain UUID} of this {@linkplain EconomicEntity}, or if this
     * entity belongs to the {@linkplain Group} with the given id, and
     * false otherwise.
     */
	public boolean isMemberOf(UUID id) {
        if (id == this.getUniqueId()) return true;
        EconomicEntity ee = getOrNull(id);
        return ee != null &&
                !ee.isPlayer() &&
                ee instanceof Group &&
                ((Group) ee).isMember(this);
    }
	
	// ===========================
	// Static Methods
	// ===========================
	
	/**
	 * Gets the {@link EconomicEntity} associated to the given {@link UUID}.
	 * If none is found, creates one with the given {@link UUID}.
	 * @param id as {@link UUID}
	 * @return {@link EconomicEntity}
	 */
	public static EconomicEntity get(UUID id){
		EconomicEntity e = entities.get(id.toString());
		if(e == null){
			e = new EconomicEntity(id);
		}
		return e;
	}
	/**
	 * Gets the {@link EconomicEntity} associated to the given {@link UUID}.
	 * If none is found, returns Null.
	 * @param id as {@link UUID}
	 * @return {@link EconomicEntity}
	 */
	public static EconomicEntity getOrNull(UUID id) {
		return entities.get(id.toString());
	}
}