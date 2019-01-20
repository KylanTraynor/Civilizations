package com.kylantraynor.civilizations.groups;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.utils.Utils;

public class GroupInventory implements Inventory {
	
	private ItemStack[] contents;

	public GroupInventory(int size) {
		this.contents = new ItemStack[size];
	}

	@Override
	public int getSize() {
		return contents.length;
	}

	@Override
	public int getMaxStackSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxStackSize(int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Inventory";
	}

	@Override
	public ItemStack getItem(int index) {
		return contents[index];
	}

	@Override
	public void setItem(int index, ItemStack item) {
		contents[index] = item;
	}

	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items)
			throws IllegalArgumentException {
		HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
		for(int j = 0; j < items.length; j++){
			for(int i = 0; i < getSize(); i++){
				if(contents[i] == null) continue;
				if(contents[i].isSimilar(items[j])){
					int max = contents[i].getMaxStackSize();
					int dif = max - contents[i].getAmount();
					if(dif <= 0) continue;
					int transfer = Math.min(items[j].getAmount(), dif);
					contents[i].setAmount(contents[i].getAmount() + transfer);
					items[j].setAmount(items[j].getAmount() - transfer);
					if(items[j].getAmount() <= 0){
						break;
					}
				}
			}
			if(items[j].getAmount() > 0){
				result.put(j, items[j]);
			}
		}
		return result;
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items)
			throws IllegalArgumentException {
		HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
		for(int j = 0; j < items.length; j++){
			for(int i = 0; i < getSize(); i++){
				if(contents[i] == null) continue;
				if(contents[i].isSimilar(items[j])){
					int dif = contents[i].getAmount();
					if(dif <= 0) continue;
					int transfer = Math.min(items[j].getAmount(), dif);
					contents[i].setAmount(contents[i].getAmount() - transfer);
					items[j].setAmount(items[j].getAmount() - transfer);
					if(items[j].getAmount() <= 0){
						break;
					}
				}
			}
			if(items[j].getAmount() > 0){
				result.put(j, items[j]);
			}
		}
		return result;
	}

	@Override
	public ItemStack[] getContents() {
		return contents;
	}

	@Override
	public void setContents(ItemStack[] items) throws IllegalArgumentException {
		contents = items;
	}

	@Override
	public ItemStack[] getStorageContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStorageContents(ItemStack[] items)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Material material) throws IllegalArgumentException {
		for(ItemStack s : contents){
			if(s == null) continue;
			if(s.getType() == material) return true;
		}
		return false;
	}

	@Override
	public boolean contains(ItemStack item) {
		for(ItemStack s : contents){
			if(s == item) return true;
		}
		return false;
	}

	@Override
	public boolean contains(Material material, int amount)
			throws IllegalArgumentException {
		for(ItemStack s : contents){
			if(s == null) continue;
			if(s.getType() == material && s.getAmount() == amount) return true;
		}
		return false;
	}

	@Override
	public boolean contains(ItemStack item, int amount) {
		for(ItemStack s : contents){
			if(s == null) continue;
			if(s.isSimilar(item) && s.getAmount() >= amount) return true;
		}
		return false;
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		int count = 0;
		for(ItemStack s : contents){
			if(s == null) continue;
			if(s.isSimilar(item) && count + s.getAmount() >= amount) return true;
			else if(s.isSimilar(item)) count += s.getAmount();
		}
		return false;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material)
			throws IllegalArgumentException {
		HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
		for(int i = 0; i < getSize(); i++){
			if(contents[i] != null){
				if(contents[i].getType() == material){
					result.put(i, contents[i]);
				}
			}
		}
		return result;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		HashMap<Integer, ItemStack> result = new HashMap<Integer, ItemStack>();
		for(int i = 0; i < getSize(); i++){
			if(contents[i] != null){
				if(contents[i].isSimilar(item)){
					result.put(i, contents[i]);
				}
			}
		}
		return result;
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		for(int i = 0; i < getSize(); i++){
			if(contents[i] != null){
				if(contents[i].getType() == material){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int first(ItemStack item) {
		for(int i = 0; i < getSize(); i++){
			if(contents[i] != null){
				if(contents[i].isSimilar(item)){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int firstEmpty() {
		for(int i = 0; i < getSize(); i++){
			if(contents[i] != null){
				if(contents[i].getType() == Material.AIR){
					return i;
				}
			} else {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
		for(int i = 0; i < getSize(); i++){
			if(contents[i].getType() == material){
				contents[i] = null;
				break;
			}
		}
	}

	@Override
	public void remove(ItemStack item) {
		for(int i = 0; i < getSize(); i++){
			if(contents[i].isSimilar(item)){
				contents[i] = null;
				break;
			}
		}
	}

	@Override
	public void clear(int index) {
		contents[index] = null;
	}

	@Override
	public void clear() {
		for(int i = 0; i < getSize(); i++){
			contents[i] = null;
		}
	}

	@Override
	public List<HumanEntity> getViewers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryHolder getHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<ItemStack> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<ItemStack> iterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
