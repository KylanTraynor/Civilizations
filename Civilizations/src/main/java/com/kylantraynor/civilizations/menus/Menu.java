package com.kylantraynor.civilizations.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public abstract class Menu extends InventoryView{

	private Player player;
	
	@Override
	public Player getPlayer(){
		return player;
	}
	
	public void open(Player player){
		this.player = player;
		update();
		player.openInventory(this);
	}

	public abstract void update();
}
