package com.kylantraynor.civilizations.builder;

import java.util.UUID;
import java.util.logging.Level;

import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.selection.Selection;

public interface HasBuilder {
	public Builder getBuilder();
	public ItemStack getSupplies(Material material, short data);
	public boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir);
	public boolean canBuild();
	public ItemStack getSuppliesAndRemove(ItemStack is);
	public void sendNotification(Level type, String message);
	public UUID getIdentifier();
}
