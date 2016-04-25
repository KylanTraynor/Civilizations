package com.kylantraynor.civilizations.groups.settlements.plots;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Chest;
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
	public String getType() {
		return "Warehouse";
	}

	public List<Chest> getAllChests(){
		List<Chest> list = new ArrayList<Chest>();
		for(Shape s : getProtection().getShapes()){
			for(int x = s.getMinX(); x < s.getMaxX(); x++){
				for(int y = s.getMinY(); y < s.getMaxY(); y++){
					for(int z = s.getMinZ(); z < s.getMaxZ(); z++){
						
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public void addItem(ItemStack itemStack) {
		
	}

	@Override
	public void removeItem(ItemStack itemStack) {
		
	}

}
