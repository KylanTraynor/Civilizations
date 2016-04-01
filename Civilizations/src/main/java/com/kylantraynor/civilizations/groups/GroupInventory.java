package com.kylantraynor.civilizations.groups;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface GroupInventory {
	/**
	 * Gets the shared inventory of that group.
	 * @return
	 */
	public Inventory getInventory();
	/**
	 * Sets the shared inventory of that group.
	 */
	public void addItem(ItemStack itemStack);
	/**
	 * Removes the given item from the shared inventory;
	 * @param itemStack
	 */
	public void removeItem(ItemStack itemStack);
}
