package com.kylantraynor.civilizations.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class Menu extends InventoryView{

	public void open(Player player) {
	}

	public abstract void update();
}
