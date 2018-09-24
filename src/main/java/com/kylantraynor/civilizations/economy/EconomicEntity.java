package com.kylantraynor.civilizations.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.kylantraynor.civilizations.managers.AccountManager;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.utils.DoubleIdentifier;
import com.kylantraynor.civilizations.utils.Identifiable;
import com.kylantraynor.civilizations.utils.Identifier;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
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
	
	private static Map<UUID, EconomicEntity> entities = new HashMap<>();
	
	private Budget budget;
	private double balance = 0;
	private UUID id;
	
	/**
	 * Creates a new EconomicEntity with a predefined {@link UUID}.
	 * @param id as {@link UUID}
	 */
	public EconomicEntity(UUID id){
		this.id = id;
	}
	
	/**
	 * Creates a new EconomicEntity with a random {@link SimpleIdentifier}.
	 */
	public EconomicEntity(){
		this.id = UUID.randomUUID();
	}
	
	/**
	 * Gets the {@link UUID} of this entity.
	 * @return {@link UUID}
	 */
	public UUID getIdentifier(){
		return id;
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
		if(Civilizations.economyType == EconomyType.VAULT && isPlayer()){
			try{
				return Economy.getVault().getBalance(getOfflinePlayer());
			} catch (RuntimeException e){
				Civilizations.currentInstance.getLogger().warning("Error while trying to get " + getName() + "'s Vault Balance:");
				e.printStackTrace();
			}
		}
		if(Civilizations.economyType == EconomyType.PHYSICAL && this instanceof PhysicalMoneyHolder){
		    return Economy.getCurrencyValueOf(((PhysicalMoneyHolder) this).getPhysicalMoney());
        } else {
		    return balance;
        }
	}
	
	/**
	 * Sets the current balance of this entity.
	 * @param newBalance as double
	 */
	public void setBalance(double newBalance){
		if(Civilizations.economyType == EconomyType.VAULT && isPlayer()){
			double diff = newBalance - getBalance();
			if(diff > 0){
				Economy.getVault().depositPlayer(getOfflinePlayer(), diff);
			} else {
				Economy.getVault().withdrawPlayer(getOfflinePlayer(), -diff);
			}
			balance = newBalance;
		} else if(Civilizations.economyType == EconomyType.PHYSICAL && this instanceof PhysicalMoneyHolder){
		    balance = newBalance;
        } else {
		    balance = newBalance;
        }
	}
	
	/**
	 * Gives funds to this entity without adding a {@link BudgetEntry} to the database.
	 * @param amount as double
	 */
	public void giveFunds(double amount){
		if(Civilizations.economyType == EconomyType.VAULT && isPlayer()) {
			Civilizations.currentInstance.getLogger().info("Giving " + Economy.format(amount) + " to " + getName() + ".");
			Economy.getVault().depositPlayer(getOfflinePlayer(), amount);
		} else if(Civilizations.economyType == EconomyType.PHYSICAL && this instanceof PhysicalMoneyHolder){
            ((PhysicalMoneyHolder)this).givePhysicalMoney((long) (amount * 100));
        }
		balance += amount;
	}
	
	/**
	 * Takes funds from this entity without checking if it has them (adding debt if it's
	 * not the case) and without adding a {@link BudgetEntry} to the database.
	 * @param amount
	 */
	public void takeFunds(double amount){
		if(Civilizations.economyType == EconomyType.VAULT && isPlayer()) {
			Economy.getVault().withdrawPlayer(getOfflinePlayer(), amount);
		} else if(Civilizations.economyType == EconomyType.PHYSICAL && this instanceof PhysicalMoneyHolder){
            ((PhysicalMoneyHolder)this).takePhysicalMoney((long) (amount * 100));
        }
		balance -= amount;
	}
	
	// ===========================
	// Methods not needing saving
	// ===========================
	/**
	 * Checks if this entity actually represents an {@link OfflinePlayer}.
	 * @return true if the entity represents a player, false otherwise
	 */
	public boolean isPlayer(){
		return getOfflinePlayer() != null;
	}
	
	/**
	 * Gets the {@linkplain OfflinePlayer} this entity represents.
	 * @return {@link OfflinePlayer} or {@code null} if no player with this {@link UUID} was found.
	 */
	public OfflinePlayer getOfflinePlayer(){
	    for(OfflinePlayer op : Bukkit.getOfflinePlayers()){
	        if(op.getUniqueId().equals(this.getIdentifier())) return op;
        }
        return null;
	}
	
	/**
	 * Gets the name of this {@link EconomicEntity}, whether it's a player's name or a {@link Group}'s name.
	 * @return String
	 */
	public String getName(){
		if(isPlayer()){
		    OfflinePlayer op = getOfflinePlayer();
			if(op != null) return op.getName();
		}
		return "Unknown (DEBUG: " + this.getIdentifier().toString() + ")";
	}

    /**
     * Checks if this {@linkplain EconomicEntity} belongs to the
     * {@linkplain EconomicEntity} with the given {@linkplain Identifier}.
     * @param id as {@link Identifier}
     * @return true if the given {@linkplain Identifier} is the same as the
     * {@linkplain Identifier} of this {@linkplain EconomicEntity}, or if this
     * entity belongs to the {@linkplain Group} with the given id, and
     * false otherwise.
     */
	public boolean isMemberOf(UUID id) {
        if (id.equals(this.getIdentifier())) return true;
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
		EconomicEntity e = entities.get(id);
		if(e == null){
		    if(AccountManager.isCharacterRegistered(id)){
		        try{ return AccountManager.getCharacter(id); } catch (ExecutionException ex) {ex.printStackTrace();}
            }
			e = new EconomicEntity(id);
        }
		return e;
	}
	/**
	 * Gets the {@link EconomicEntity} associated to the given {@link Identifier}.
	 * If none is found, returns {@code null}.
	 * @param id as {@link UUID}
	 * @return {@link EconomicEntity}
	 */
	public static EconomicEntity getOrNull(UUID id) {
		return entities.get(id);
	}

    /**
     * Adds the given {@link EconomicEntity} to the map of {@link EconomicEntity EconomicEntities}.
     * @param entity as {@link EconomicEntity}
     */
    public static void register(EconomicEntity entity){
        entities.put(entity.getIdentifier(), entity);
    }
}