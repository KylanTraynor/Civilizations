package com.kylantraynor.civilizations.groups.settlements.plots;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.groups.GroupInventory;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.shapes.Shape;

public class Warehouse extends Plot implements GroupInventory{

	public Warehouse(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public void addItem(ItemStack itemStack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItem(ItemStack itemStack) {
		// TODO Auto-generated method stub
		
	}

}
