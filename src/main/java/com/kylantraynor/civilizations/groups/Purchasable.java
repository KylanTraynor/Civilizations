package com.kylantraynor.civilizations.groups;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.TransactionResult;

public interface Purchasable {
	/**
	 * Gets the owner of this group.
	 * @return {@link EconomicEntity}
	 */
	EconomicEntity getOwner();
	/**
	 * Checks if the given player is the owner of this group.
	 * @param player as {@link EconomicEntity}
	 * @return true if the player owns the group, false otherwise.
	 */
	boolean isOwner(EconomicEntity player);
	/**
	 * Gets the price this group can be bought against.
	 * @return double
	 */
	double getPrice();
	/**
	 * Sets the price this group can be bought against.
	 * @param newPrice as double
	 */
	void setPrice(double newPrice);
	/**
	 * Checks if the {@link Group} is for sale.
	 * @return true if the {@link Group} is for sale, false otherwise.
	 */
	boolean isForSale();
	/**
	 * Sets the forsale state of this {@link Group}.
	 * @param forSale
	 */
	void setForSale(boolean forSale);
	/**
	 * Makes the given {@link EconomicEntity} purchase this group.
	 * @param ecoEntity as {@link EconomicEntity}
	 * @return {@link TransactionResult}
	 */
	TransactionResult purchase(EconomicEntity ecoEntity);
}
