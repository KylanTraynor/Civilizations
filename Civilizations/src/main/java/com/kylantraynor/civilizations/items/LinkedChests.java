package com.kylantraynor.civilizations.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

/**
 * A class handling different chests with the same inventory.
 * @author Baptiste
 *
 */
public class LinkedChests {
	
	public static List<LinkedChests> all = new ArrayList<LinkedChests>();
	
	public static LinkedChests getAt(Location l){
		for(LinkedChests lc : all){
			if(lc.chest1 != null){
				if(lc.chest1.getBlock() == l.getBlock()){
					return lc;
				}
			}
			if(lc.chest2 != null){
				if(lc.chest2.getBlock() == l.getBlock()){
					return lc;
				}
			}
		}
		return null;
	}
	
	Location chest1;
	Location chest2;
	
	LinkedChests(Location chest1, Location chest2){
		this.chest1 = chest1;
		this.chest2 = chest2;
	}
	
	public Chest getChest1(){
		if(chest1!=null){
			if(chest1.getBlock().getType() == Material.CHEST || chest1.getBlock().getType() == Material.TRAPPED_CHEST){
				BlockState state = chest1.getBlock().getState();
				if(state instanceof Chest){
					return (Chest) state;
				}
			}
		}
		return null;
	}
	
	public Chest getChest2(){
		if(chest2!=null){
			if(chest2.getBlock().getType() == Material.CHEST || chest2.getBlock().getType() == Material.TRAPPED_CHEST){
				BlockState state = chest2.getBlock().getState();
				if(state instanceof Chest){
					return (Chest) state;
				}
			}
		}
		return null;
	}
	
	public Inventory getInventory(){
		
		if(getChest1() != null){
			return getChest1().getInventory();
		}
		return null;
	}

}
