package com.kylantraynor.civilizations.settings;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.shops.ShopType;

public class ShopSettings extends YamlConfiguration{

	public Location getLocation() {
		if(this.contains("location")){
			try{
				return new Location(Bukkit.getWorld(this.getString("location.world")),
						this.getInt("location.x"),
						this.getInt("location.y"),
						this.getInt("location.z"));
			} catch (Exception e){
				return null;
			}
		}
		return null;
	}
	
	public void setLocation(Location newLocation){
		if(newLocation == null){
			this.set("location", null);
		} else {
			this.set("location.world", newLocation.getWorld().getName());
			this.set("location.x", newLocation.getBlockX());
			this.set("location.y", newLocation.getBlockY());
			this.set("location.z", newLocation.getBlockZ());
		}
	}

	public OfflinePlayer getOwner() {
		if(this.contains("owner")){
			try{
				return Bukkit.getOfflinePlayer(UUID.fromString(this.getString("owner")));
			} catch (Exception e){
				return null;
			}
		}
		return null;
	}
	
	public void setOwner(OfflinePlayer newOwner) {
		if(newOwner != null){
			this.set("owner", newOwner.getUniqueId().toString());
		} else {
			this.set("owner", null);
		}
	}

	public ItemStack getItem() {
		return this.getItemStack("item");
	}

	public void setItem(ItemStack newItem) {
		this.set("item", newItem);
	}
	
	public double getPrice(){
		return this.getDouble("price");
	}
	
	public void setPrice(double newPrice){
		this.set("price", newPrice);
	}
	
	public ShopType getType(){
		return ShopType.valueOf(this.getString("type"));
	}
	
	public void setType(ShopType newType){
		this.set("type", newType.toString());
	}
}
