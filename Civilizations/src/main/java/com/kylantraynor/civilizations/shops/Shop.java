package com.kylantraynor.civilizations.shops;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.settings.ShopSettings;

public class Shop {
	
	public ShopSettings settings;
	private Location location;
	private OfflinePlayer owner;
	private List<Sign> signs;
	
	public Location getLocation(){
		if(location == null){
			location = settings.getLocation(); 
		}
		return location;
	}
	
	public void setLocation(Location newLocation){
		location = newLocation;
		settings.setLocation(newLocation);
	}
	
	public OfflinePlayer getOwner(){
		if(owner != null){
			owner = settings.getOwner();
		}
		return owner;
	}
	
	public void setOwner(OfflinePlayer newOwner){
		owner = newOwner;
		settings.setOwner(newOwner);
	}
	
	public ItemStack getItem(){
		return settings.getItem();
	}
	
	public void setItem(ItemStack newItem){
		settings.setItem(newItem);
	}
	
	public double getPrice(){
		return settings.getPrice();
	}
	
	public void setPrice(double newPrice){
		settings.setPrice(newPrice);
	}
	
	public ShopType getType(){
		return settings.getType();
	}
	
	public void setType(ShopType newType){
		settings.setType(newType);
	}
	
	public boolean isDoubleChest(){
		Location current = location.clone();
		for(int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++){
			for(int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++){
				if(Math.abs(x) == Math.abs(z)) continue;
				current.setX(x);
				current.setZ(z);
				if(location.getBlock().getType() == current.getBlock().getType()) return true;
			}
		}
		return false;
	}
	
	public Material getMaterial(){
		if(getItem() != null){
			return getItem().getType();
		}
		return null;
	}
	
	public String getItemName(){
		if(getItem() != null){
			if(getItem().getItemMeta() != null){
				if(!getItem().getItemMeta().getDisplayName().isEmpty()){
					return ChatColor.GOLD + getItem().getItemMeta().getDisplayName();
				}
			}
			return getItem().getType().toString().replace("_", " ") + (getItem().getData().getData() != 0 ? (":" + getItem().getData().getData()) : "") ;
		}
		return "";
	}
	
}
