package com.kylantraynor.civilizations.groups;

import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.TransactionResult;

public interface Purchasable {
	/**
	 * Gets the owner of this group.
	 * @return {@link EconomicEntity}
	 */
	public EconomicEntity getOwner();
	/**
	 * Checks if the given player is the owner of this group.
	 * @param player as {@link OfflinePlayer}
	 * @return true if the player owns the group, false otherwise.
	 */
	public boolean isOwner(OfflinePlayer player);
	/**
	 * Gets the price this group can be bought against.
	 * @return double
	 */
	public double getPrice();
	/**
	 * Sets the price this group can be bought against.
	 * @param newPrice as double
	 */
	public void setPrice(double newPrice);
	/**
	 * Checks if the {@link Group} is for sale.
	 * @return true if the {@link Group} is for sale, false otherwise.
	 */
	public boolean isForSale();
	/**
	 * Sets the forsale state of this {@link Group}.
	 * @param forSale
	 */
	public void setForSale(boolean forSale);
	/**
	 * Makes the given {@link EconomicEntity} purchase this group.
	 * @param ecoEntity as {@link EconomicEntity}
	 * @return {@link TransactionResult}
	 */
	public TransactionResult purchase(EconomicEntity ecoEntity);
}
