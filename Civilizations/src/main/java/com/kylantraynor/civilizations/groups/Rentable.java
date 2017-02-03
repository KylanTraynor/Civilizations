package com.kylantraynor.civilizations.groups;

import java.time.Instant;

import org.bukkit.OfflinePlayer;

public interface Rentable extends Purchasable{
	/**
	 * Gets the player renting this group.
	 * @return OfflinePlayer
	 */
	public OfflinePlayer getRenter();
	/**
	 * Checks if the given player is renting this group.
	 * @param player
	 * @return true if the player is renting this group, false otherwise.
	 */
	public boolean isRenter(OfflinePlayer player);
	/**
	 * Gets the daily rent of this group.
	 * @return double
	 */
	public double getRent();
	/**
	 * Sets the daily rent of this group.
	 * @param rent
	 */
	public void setRent(double rent);
	/**
	 * Checks if this group is for rent.
	 * @return true if the group is for rent, false otherwise.
	 */
	public boolean isForRent();
	/**
	 * Sets the forrent state of this group.
	 * @param forRent
	 */
	public void setForRent(boolean forRent);
	/**
	 * Makes the given player start renting this group.
	 * @param player
	 * @return true if the player now rents the group, false otherwise.
	 */
	public boolean rent(OfflinePlayer player);
	/**
	 * Gets the next payment date of the rent.
	 * @return Instant
	 */
	public Instant getNextRentDate();
	/**
	 * Sets the next payment date of the rent.
	 * @param next
	 */
	public void setNextRentDate(Instant next);
	/**
	 * Effectively pays the rent if the group is currently being rented.
	 * @return true if the transaction happened, false otherwise.
	 */
	public boolean payRent();
}