package com.kylantraynor.civilizations.builder;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.selection.Selection;

public interface HasBuilder {
	Builder getBuilder();
	ItemStack getSupplies(BlockData blockData);
	boolean addBuildProject(Selection selection, Blueprint cbp, boolean setAir);
	boolean canBuild();
	boolean removeSupplies(BlockData blockData);
	void sendNotification(Level type, String message);
	UUID getIdentifier();
}
