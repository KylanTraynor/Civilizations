package com.kylantraynor.civilizations.builder;

import java.util.UUID;
import java.util.logging.Level;

import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.selection.Selection;

public interface HasBuilder {
	Builder getBuilder();
	ItemStack getSupplies(Material material, short data);
	boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir);
	boolean canBuild();
	ItemStack getSuppliesAndRemove(ItemStack is);
	void sendNotification(Level type, String message);
	UUID getIdentifier();
}
