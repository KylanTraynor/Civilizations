package com.kylantraynor.civilizations.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.selection.Selection;

public interface HasBuilder {
	public Builder getBuilder();
	public ItemStack getSupplies(Material material, short data);
	public boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir);
	public boolean canBuild();
	public ItemStack getSuppliesAndRemove(ItemStack is);
}
