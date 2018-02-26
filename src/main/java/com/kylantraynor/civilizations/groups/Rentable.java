package com.kylantraynor.civilizations.groups;

import java.time.Instant;

import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.TransactionResult;

public interface Rentable extends Purchasable{
	/**
	 * Gets the player renting this {@link Group}.
	 * @return {@link EconomicEntity}
	 */
	public EconomicEntity getRenter();
	/**
	 * Sets the current renter of this {@link Group}.
	 * @param entity as {@link EconomicEntity}
	 */
	public void setRenter(EconomicEntity entity);
	/**
	 * Checks if the given {@link OfflinePlayer} is renting this {@link Group}.
	 * @param player as {@link OfflinePlayer}
	 * @return true if the player is renting this {@link Group}, false otherwise.
	 */
	public boolean isRenter(OfflinePlayer player);
	/**
	 * Gets the daily rent of this {@link Group}.
	 * @return double
	 */
	public double getRent();
	/**
	 * Sets the daily rent of this {@link Group}.
	 * @param rent as double
	 */
	public void setRent(double rent);
	/**
	 * Checks if this {@link Group} is for rent.
	 * @return true if the {@link Group} is for rent, false otherwise.
	 */
	public boolean isForRent();
	/**
	 * Sets whether or not this {@link Group} can be rented.
	 * @param forRent as boolean
	 */
	public void setForRent(boolean forRent);
	/**
	 * Makes the given {@link EconomicEntity} start renting this {@link Group}.
	 * @param ecoEntity as {@link EconomicEntity}
	 * @return {@link TransactionResult}
	 */
	public TransactionResult rent(EconomicEntity ecoEntity);
	/**
	 * Gets the next payment date of the rent.
	 * @return {@link Instant}
	 */
	public Instant getNextRentDate();
	/**
	 * Sets the next payment date of the rent.
	 * @param next as {@link Instant}
	 */
	public void setNextRentDate(Instant next);
	/**
	 * Effectively pays the rent if the {@link Group} is currently being rented.
	 * @return {@link TransactionResult}
	 */
	public TransactionResult payRent();
}