package com.kylantraynor.civilizations.shops;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.settings.ShopSettings;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.util.Util;

public class Shop {
	
	public ShopSettings settings = new ShopSettings();
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
	
	public boolean isInMarketStall(){
		Plot plot = Plot.getAt(location);
		if(plot != null){
			if(plot instanceof MarketStall){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Sends all the taxes to the different institutions from a taxed amount of money.
	 * @param taxedAmount The amount of money subject to taxes.
	 * @return The amount of money left untaxed.
	 */
	public double sendTransactionTaxesOn(double taxedAmount){
		double remaining = taxedAmount;
		return remaining;
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
			return Util.getMaterialName(getItem());
		}
		return "";
	}
	
	public Settlement getSettlement() {
		Plot p = Plot.getAt(this.getLocation());
		if(p != null){
			return p.getSettlement();
		}
		return null;
	}
	
	public Fort getFort(){
		return InfluenceMap.getInfluentFortAt(this.getLocation());
	}
}
