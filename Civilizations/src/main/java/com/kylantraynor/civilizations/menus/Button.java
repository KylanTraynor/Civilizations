package com.kylantraynor.civilizations.menus;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;

public class Button extends ItemStack{
	
	private Material invalidButton = Material.IRON_BLOCK;
	
	BukkitRunnable runnable;
	private boolean isEnabled;
	
	public Button(Material mat, String title, List<String> description, BukkitRunnable runnable, boolean isEnabled){
		super(mat);
		if(!isEnabled) this.setType(invalidButton);
		ItemMeta im = getItemMeta();
		im.setDisplayName(title);
		im.setLore(description);
		this.runnable = runnable;
		this.isEnabled = isEnabled;
		setItemMeta(im);
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}
}
