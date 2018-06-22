package com.kylantraynor.civilizations.groups;

import org.bukkit.inventory.ItemStack;

public interface HasInventory {
	/**
	 * Gets the shared inventory of that group.
	 * @return
	 */
	GroupInventory getInventory();
	/**
	 * Sets the shared inventory of that group.
	 */
	void addItem(ItemStack... items);
	/**
	 * Removes the given item from the shared inventory;
	 * @param items
	 */
	void removeItem(ItemStack... items);
	/**
	 * Checks if the inventory contains at least the given amount of the given item.
	 * @param item
	 * @param amount
	 * @return
	 */
	boolean containsAtLeast(ItemStack item, int amount);
}
