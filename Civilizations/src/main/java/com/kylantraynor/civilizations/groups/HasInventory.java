package com.kylantraynor.civilizations.groups;

import org.bukkit.inventory.ItemStack;

public interface HasInventory {
	/**
	 * Gets the shared inventory of that group.
	 * @return
	 */
	public GroupInventory getInventory();
	/**
	 * Sets the shared inventory of that group.
	 */
	public void addItem(ItemStack... items);
	/**
	 * Removes the given item from the shared inventory;
	 * @param itemStack
	 */
	public void removeItem(ItemStack... items);
	
	boolean containsAtLeast(ItemStack item, int amount);
}
