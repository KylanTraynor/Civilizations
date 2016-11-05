package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.GroupInventory;
import com.kylantraynor.civilizations.groups.HasInventory;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.shapes.Shape;

public class Warehouse extends Plot implements HasInventory{
	
	private List<Chest> chests;
	
	public Warehouse(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
	}
	
	public Warehouse() {
		super();
	}
	
	public String getIcon(){
		return "bricks";
	}
	
	@Override
	public String getType() {
		return "Warehouse";
	}
	
	@Override
	public boolean isPersistent(){
		return true;
	}

	public List<Chest> getAllChests(){
		List<Chest> list = new ArrayList<Chest>();
		Location l = null;
		for(Shape s : getProtection().getShapes()){
			l = s.getLocation().clone();
			for(int x = s.getMinX(); x < s.getMaxX(); x++){
				for(int y = s.getMinY(); y < s.getMaxY(); y++){
					for(int z = s.getMinZ(); z < s.getMaxZ(); z++){
						l.setX(x);
						l.setY(y);
						l.setZ(z);
						if(l.getBlock().getType() == Material.CHEST ||
								l.getBlock().getType() == Material.TRAPPED_CHEST){
							BlockState state = l.getBlock().getState();
							if(state instanceof Chest){
								list.add((Chest) state);
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public GroupInventory getInventory() {
		if(chests == null) chests = getAllChests();
		GroupInventory inv = new GroupInventory(getSize());
		int j = 0;
		for(Chest c : chests){
			for(int i = 0; i < c.getBlockInventory().getSize(); i++){
				inv.getContents()[j] = c.getBlockInventory().getContents()[i];
				j++;
			}
		}
		return inv;
	}
	
	public int getUsedSize(){
		if(chests == null) chests = getAllChests();
		int used = 0;
		for(Chest c : chests){
			for(ItemStack is : c.getBlockInventory().getContents()){
				if(is == null) continue;
				used += is.getAmount() * (64 / is.getMaxStackSize());
			}
		}
		return used;
	}
	
	public int getSize(){
		if(chests == null) chests = getAllChests();
		int chestSize = 0;
		if(chests.isEmpty()) return 0;
		chestSize = chests.get(0).getBlockInventory().getSize();
		return chestSize * chests.size() * 64;
	}

	@Override
	public void addItem(ItemStack... items) {
		if(chests == null) chests = getAllChests();
		for(Chest c : chests){
			if(items.length == 0) return;
			HashMap<Integer, ItemStack> result = c.getBlockInventory().addItem(items);
			items = result.values().toArray(new ItemStack[result.size()]);
		}
	}

	@Override
	public void removeItem(ItemStack... items) {
		if(chests == null) chests = getAllChests();
		for(Chest c : chests){
			if(items.length == 0) return;
			HashMap<Integer, ItemStack> result = c.getBlockInventory().removeItem(items);
			items = result.values().toArray(new ItemStack[result.size()]);
		}
	}
	
	@Override
	public void update(){
		if(getSettlement() == null){
			if(Settlement.getAt(getProtection().getCenter()) != null){
				setSettlement(Settlement.getAt(getProtection().getCenter()));
			}
		}
		super.update();
	}
	
	/**
	 * Gets the file where this keep is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getWarehousesDirectory(), "" + getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
}