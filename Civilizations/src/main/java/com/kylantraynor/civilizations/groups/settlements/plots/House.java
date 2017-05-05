package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.groups.Rentable;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Shape;

public class House extends Plot implements Rentable{

	public House(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
	}
	public House(String name, List<Shape> shapes, Settlement settlement) {
		super(name.isEmpty() ? "House" : name, shapes, settlement);
	}
	
	public House() {
		super();
	}
	
	/*
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		
		fm.then("\nActions: ").color(ChatColor.GRAY);
		if(this.isMember(player)){
			if(getSettlement().hasPermission(PermissionType.MANAGE_PLOTS, null, player)){
				fm.then("\nRename").color(ChatColor.GOLD).tooltip("Rename this House.").suggest("/group " + getId() + " setname NEW NAME");
			} else {
				fm.then("\nRename").color(ChatColor.GRAY).tooltip("You don't have the MANAGE PLOTS permission here.");
			}
		}
		
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	*/
	
	public int getBedCount(){
		int result = 0;
		for(Shape s : getProtection().getShapes()){
			for(Location l : s.getBlockLocations()){
				if(l.getBlock().getType() == Material.BED_BLOCK && l.getBlock().getLightFromSky() < 14){
					BlockState state = l.getBlock().getState();
					Bed bed = (Bed) state.getData();
					if(bed.isHeadOfBed()){
						result++;
					}
				}
			}
		}
		return result;
	}
	
	public boolean isValid(){
		int bedCount = 0;
		boolean hasCraftingTable = false;
		boolean hasChest = false;
		for(Shape s : getProtection().getShapes()){
			for(Location l : s.getBlockLocations()){
				if(l.getBlock().getType() == Material.BED_BLOCK && l.getBlock().getLightFromSky() < 14){
					BlockState state = l.getBlock().getState();
					Bed bed = (Bed) state.getData();
					if(bed.isHeadOfBed()){
						bedCount++;
						if(hasCraftingTable && hasChest) return true;
					}
				} else if(l.getBlock().getType() == Material.WORKBENCH && l.getBlock().getLightFromSky() < 14){
					hasCraftingTable = true;
					if(bedCount > 0 && hasChest) return true;
				} else if(l.getBlock().getType() == Material.CHEST && l.getBlock().getLightFromSky() < 14){
					hasChest = true;
					if(bedCount > 0 && hasCraftingTable) return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isPersistent(){
		return true;
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
	 * Gets the file where this house is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getHousePlotDirectory(), "" + getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	
	/**
	 * Gets an interactive info panel adapted to the given player.
	 * @param player Context
	 * @return FancyMessage
	 */
	@Override
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), ChatColor.GREEN))
			.then("\nPart of ").color(ChatColor.GRAY)
			.then(getNameOf(getSettlement())).color(ChatColor.GOLD);
		if(getSettlement() != null){
			fm.command("/group " + getSettlement().getId() + " info");
		}
		fm.then(".").color(ChatColor.GRAY)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getId() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getId() + " members")
			.then("/").color(ChatColor.GRAY)
			.then("" + getBedCount()).color(ChatColor.GOLD).tooltip("Beds under a roof.")
			.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	@Override
	public OfflinePlayer getOwner() {
		return getSettings().getOwner();
	}
	@Override
	public boolean isOwner(OfflinePlayer player) {
		if(getOwner() != null){
			return player.getUniqueId().equals(getOwner().getUniqueId());
		} else if(this.hasPermission(PermissionType.MANAGE_PLOTS, null, player.getPlayer())){
			return true;
		} else if(player.isOp()){
			return true;
		}
		return false;
	}
	@Override
	public double getPrice() {
		return getSettings().getPrice();
	}
	@Override
	public void setPrice(double newPrice) {
		getSettings().setPrice(newPrice);
	}
	@Override
	public boolean isForSale() {
		return getSettings().isForSale();
	}
	@Override
	public void setForSale(boolean forSale) {
		getSettings().setForSale(forSale);
	}
	@Override
	public boolean purchase(OfflinePlayer player) {
		if(!isForSale()){
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "This house is not for sale.");
			return false;
		}
		
		if(Economy.withdrawPlayer(player, getPrice())){
			Economy.playPaySound(player.getPlayer());
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've purchased this house for " + Economy.format(getPrice()));
			double amount = getPrice();
			
			if(getSettlement() != null){
				amount = getSettlement().taxTransaction(TaxType.TRANSACTION, amount);
			}
			
			if(getOwner() == null){
				Economy.depositSettlement(getSettlement(), amount);
				getSettlement().sendMessage(getSettlement().getChatColor() + player.getPlayer().getDisplayName() + " just purchased a house for "+ Economy.format(amount) + "!", null);
			} else {
				Economy.depositPlayer(getOwner(), amount);
				if(getOwner().isOnline()){
					Economy.playCashinSound(getOwner().getPlayer());
					getOwner().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + player.getPlayer().getDisplayName() + " purchased this house! You've received " + Economy.format(amount) + "!");
				}
			}
			this.getSettings().setOwner(player);
			this.setForSale(false);
			return true;
		} else {
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "You don't have enough money to buy this plot.");
			return false;
		}
	}
	@Override
	public OfflinePlayer getRenter() {
		return getSettings().getRenter();
	}
	@Override
	public boolean isRenter(OfflinePlayer player) {
		return getRenter() == player;
	}
	@Override
	public double getRent() {
		return getSettings().getRent();
	}
	@Override
	public void setRent(double rent) {
		getSettings().setRent(rent);
	}
	@Override
	public boolean isForRent() {
		return getSettings().isForRent();
	}
	@Override
	public void setForRent(boolean forRent) {
		getSettings().setForRent(forRent);
	}
	@Override
	public boolean rent(OfflinePlayer player) {
		if(!isForRent()){
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "This house isn't for rent.");
			return false;
		}
		if(getRenter() != null){
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "This house is already rented by someone.");
			return false;
		}
		
		this.getSettings().setRenter(player);
		if(this.payRent()){
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You are now renting this house.");
			return true;
		} else {
			player.getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "You can't afford to rent this house.");
			this.getSettings().setRenter(null);
			return false;
		}
	}
	@Override
	public Instant getNextRentDate() {
		return getSettings().getNextPayment();
	}
	@Override
	public void setNextRentDate(Instant next) {
		getSettings().setNextPayment(next);
	}
	@Override
	public boolean payRent() {
		if(getRenter() == null)
			return false;
		
		setNextRentDate(Instant.now().plus(1, ChronoUnit.DAYS));
		
		if(Economy.withdrawPlayer(getRenter(), getRent())){
			if(getRenter().isOnline()){
				Economy.playPaySound(getRenter().getPlayer());
				getRenter().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(getRent()) + " in rent.");
			}
			double amount = getRent();
			
			if(getSettlement() != null){
				amount = getSettlement().taxTransaction(TaxType.RENT, amount);
			}
			
			if(getOwner() != null){
				Economy.depositPlayer(getOwner(), amount);
				if(getOwner().isOnline()){
					Economy.playCashinSound(getOwner().getPlayer());
					getOwner().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've received " + Economy.format(amount) + " in rent.");
				}
			} else if(getSettlement() != null) {
				Economy.depositSettlement(getSettlement(), amount);
			}
			
			return true;
		} else {
			if(getRenter().isOnline()){
				getRenter().getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + "You couldn't pay " + Economy.format(getRent()) + " in rent today!");
			}
			if(getOwner() != null){
				if(getOwner().isOnline()){
					getOwner().getPlayer().sendMessage(this.getChatHeader() + ChatColor.RED + getRenter().getName() + " couldn't pay the rent of " + Economy.format(getRent()) + "!");
				}
			} else if(getSettlement() != null) {
				getSettlement().sendMessage(ChatColor.RED + getRenter().getName() + " couldn't pay the rent of " + Economy.format(getRent()) + "!", PermissionType.MANAGE_PLOTS);
			}
			return false;
		}
	}
}