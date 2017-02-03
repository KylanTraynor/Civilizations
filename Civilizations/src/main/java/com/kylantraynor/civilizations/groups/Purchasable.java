package com.kylantraynor.civilizations.groups;

import org.bukkit.OfflinePlayer;

public interface Purchasable {
	/**
	 * Gets the owner of this group.
	 * @return OfflinePlayer
	 */
	public OfflinePlayer getOwner();
	/**
	 * Checks if the given player is the owner of this group.
	 * @param player
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
	 * @param newPrice
	 */
	public void setPrice(double newPrice);
	/**
	 * Checks if the group is for sale.
	 * @return true if the group is for sale, false otherwise.
	 */
	public boolean isForSale();
	/**
	 * Sets the forsale state of this group.
	 * @param forSale
	 */
	public void setForSale(boolean forSale);
	/**
	 * Makes the given player purchase this group.
	 * @param player
	 * @return true if the transaction happened, false otherwise.
	 */
	public boolean purchase(OfflinePlayer player);
}
