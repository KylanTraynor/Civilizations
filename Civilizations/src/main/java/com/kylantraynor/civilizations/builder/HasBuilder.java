package com.kylantraynor.civilizations.builder;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.selection.Selection;

public interface HasBuilder {
	public Builder getBuilder();
	public ItemStack getSupplies(Material material, short data);
	public boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir);
	public boolean canBuild();
	public ItemStack getSuppliesAndRemove(ItemStack is);
	public void sendNotification(String message);
	public UUID getUniqueId();
}
