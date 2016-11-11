package com.kylantraynor.civilizations.groups.settlements.plots.market;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.ActionType;
import com.kylantraynor.civilizations.groups.GroupAction;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.shops.ShopManager;
import com.kylantraynor.civilizations.shops.ShopType;
import com.kylantraynor.civilizations.util.Util;

public class MarketStall extends Plot{
	
	public MarketStall(String name, Shape shape, Settlement settlement) {
		super(name.isEmpty() ? "Stall" : name, shape, settlement);
		CacheManager.marketstallListChanged = true;
	}
	
	public MarketStall(String name, List<Shape> shapes, Settlement settlement) {
		super(name.isEmpty() ? "Stall" : name, shapes, settlement);
		CacheManager.marketstallListChanged = true;
	}
	
	public MarketStall() {
		super();
		CacheManager.marketstallListChanged = true;
	}
	
	public OfflinePlayer getOwner(){ return getSettings().getOwner(); }
	public void setOwner(OfflinePlayer player){ getSettings().setOwner(player); }
	
	public boolean isForRent() { return getSettings().isForRent(); }
	public void setForRent(boolean forRent) { getSettings().setForRent(forRent); }
	
	public OfflinePlayer getRenter(){ return getSettings().getRenter(); }
	public void setRenter(OfflinePlayer player){ getSettings().setRenter(player); }
	
	public double getRent(){ return getSettings().getRent(); }
	public void setRent(double newRent){ getSettings().setRent(newRent); }
	
	private void setNextPayment(Instant instant) { getSettings().setNextPayment(instant); }

	public String getIcon(){
		return "scales";
	}
	
	@Override
	public boolean isPersistent(){
		return true;
	}
	
	@Override
	public void update(){
		if(getRenter() != null){
			if(Instant.now().isAfter(getSettings().getNextPayment())){
				payRent();
				setChanged(true);
			}
		}
		if(getSettlement() == null){
			if(Settlement.getAt(getProtection().getCenter()) != null){
				setSettlement(Settlement.getAt(getProtection().getCenter()));
			}
		}
		DynmapHook.updateMap(this);
		super.update();
	}
	/**
	 * Destroys this plot.
	 * @return true if the plot has been removed, false otherwise.
	 */
	@Override
	public boolean remove(){
		CacheManager.marketstallListChanged = true;
		return super.remove();
	}
	
	@Override
	public String getType() {
		return "Market Stall";
	}
	
	public Map<ItemStack, Double> getWares(){
		try{
			Civilizations.DEBUG("Trying to find wares in stall.");
		Map<ItemStack, Double> wares = new HashMap<ItemStack, Double>();
		Location current = this.getProtection().getCenter().clone();
		for(Shape s : getProtection().getShapes()){
			for(int x = s.getMinX(); x <= s.getMaxX(); x++){
				for(int y = s.getMinY(); y <= s.getMaxY(); y++){
					for(int z = s.getMinZ(); z <= s.getMaxZ(); z++){
						current.setX(x);
						current.setY(y);
						current.setZ(z);
						if(current.getBlock().getType() == Material.CHEST || current.getBlock().getType() == Material.TRAPPED_CHEST){
							Shop shop = ShopManager.getShop(current);
							if(shop != null){
								double multiplier = 1.0;
								if(shop.getType() == ShopType.BUYING){
									multiplier = -1.0;
								}
								wares.put(shop.getItem(), multiplier * shop.getPrice());
							}
						}
					}
				}
			}
		}
		return wares;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<ItemStack, Double>();
		}
	}
	
	public List<String> getWaresToString(){
		List<String> result = new ArrayList<String>();
		for(Entry<ItemStack, Double> e : getWares().entrySet()){
			result.add((e.getValue() < 0 ? "Buying: " : "Selling: ") + getNameOf(e.getKey()) + " (for " + Economy.format(Math.abs(e.getValue())) + ")");
		}
		return result;
	}
	
	private String getNameOf(ItemStack item) {
		if(item.getItemMeta().getDisplayName() != null){
			return item.getItemMeta().getDisplayName();
		} else {
			return Util.prettifyText(Util.getMaterialName(item));
		}
	}

	public void payRent() {
		if(getRenter() == null){
			return;
		}
		setNextPayment(Instant.now().plus(1, ChronoUnit.DAYS));
		if(getOwner() != null){
			if(Economy.withdrawPlayer(getRenter(), getRent())){
				if(getRenter().isOnline()){
					getRenter().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(getRent()) + " in rent.");
					Economy.playPaySound(getRenter().getPlayer());
				}
				double payout = getRent();
				//Pay Settlement's Stall Tax
				if(getSettlement() != null){
					if(Economy.depositSettlement(getSettlement(), getRent() * getSettlement().getSettings().getStallRentTax())){
						payout -= getRent() * getSettlement().getSettings().getStallRentTax();
					}
				}
				//Pay Fort's Stall Tax
				/*Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
				if(f != null){
					if(Economy.depositSettlement(f, getRent() * f.getSettings().getStallRentTax())){
						payout -= getRent() * f.getSettings().getStallRentTax();
					}
				}*/
				//Pay Owner
				Economy.depositPlayer(getOwner(), payout);
				if(getOwner().isOnline()){
					getOwner().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've received " + Economy.format(getRent()) + " for the rent.");
					Economy.playCashinSound(getOwner().getPlayer());
				}
			}
		} else if(getSettlement() != null) {
			if(Economy.withdrawPlayer(getRenter(), getRent())){
				if(getRenter().isOnline()){
					getRenter().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(getRent()) + " in rent.");
					Economy.playPaySound(getRenter().getPlayer());
				}
				double payout = getRent();
				//Pay Fort's Stall Tax
				/*Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
				if(f != null){
					if(Economy.depositSettlement(f, getRent() * f.getSettings().getStallRentTax())){
						payout -= getRent() * f.getSettings().getStallRentTax();
					}
				}*/
				//Pay Settlement
				Economy.depositSettlement(getSettlement(), payout);
			}
		} else {
			//Pay Fort
			/*Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
			if(f != null){
				if(Economy.withdrawPlayer(getRenter(), getRent())){
					if(getRenter().isOnline()){
						getRenter().getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(getRent()) + " in rent.");
						Economy.playPaySound(getRenter().getPlayer());
					}
					Economy.depositSettlement(f, getRent());
				}
			}*/
		}
	}
	
	public boolean isOwner(OfflinePlayer player){
		if(getOwner() != null){
			if(player == getOwner()){
				return true;
			}
		} else if(this.hasPermission(PermissionType.MANAGE_STALLS, null, player.getPlayer())){
			return true;
		} else if(player.isOp()){
			return true;
		}
		return false;
	}
	
	public boolean isRenter(OfflinePlayer player){
		if(getRenter() != null){
			return getRenter().equals(player);
		}
		return false;
	}
	
	/**
	 * Gets an interactive info panel of this Keep.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		/*DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getCreationDate()))).color(ChatColor.GOLD);
		}*/
		
		String renterCommand = getRenter() == null ? "" : "/p " + getRenter().getName();
		String ownerCommand = getOwner() == null ? (getSettlement() == null ? "" : "/group " + getSettlement().getId() + " INFO") : "/p " + getOwner().getName(); 
		String owner = getOwner() == null ? (getSettlement() == null ? "No one" : getSettlement().getName()) : getOwner().getName();
		if(isForRent()){
			fm.then("\nRented by: ").color(ChatColor.GRAY).command(renterCommand)
			.then(getRenter() == null ? "Available" : getRenter().getName()).color(ChatColor.GOLD).command(renterCommand);
		}
		fm.then("\nOwned by: ").color(ChatColor.GRAY).command(ownerCommand)
		.then(owner).color(ChatColor.GOLD).command(ownerCommand);
		if(isForRent()){
			fm.then("\nDaily rent: ").color(ChatColor.GRAY).then("" + getRent()).color(ChatColor.GOLD);
		}
		if(getRenter() == player){
			fm.then("\nNext Payment in ").color(ChatColor.GRAY).then("" + ChronoUnit.HOURS.between(Instant.now(), getSettings().getNextPayment()) + " hours").color(ChatColor.GOLD);
		}
		fm.then("\nActions: \n").color(ChatColor.GRAY);
		fm = addCommandsTo(fm, getGroupActionsFor(player));
		/*
		if(isOwner(player)){
			fm.then("\nRename").color(ChatColor.GOLD).tooltip("Rename this Stall.").suggest("/group " + getId() + " setname NEW NAME");
			fm.then(" - ").color(ChatColor.GRAY);
			if(getRenter() != null){
				fm.then("Kick").color(ChatColor.GOLD).tooltip("Kicks the renter of this Stall").command("/group " + getId() + " kick");
			} else {
				fm.then("Kick").color(ChatColor.GRAY).tooltip("Kicks the renter of this Stall");
			}
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Price").color(ChatColor.GOLD).tooltip("Change the rent of this Stall").suggest("/group " + getId() + " setrent " + getRent());
			fm.then(" - ").color(ChatColor.GRAY);
			if(isForRent()){
				fm.then("Rentable").color(ChatColor.GREEN).tooltip("Toggle the rentable state of the stall.").command("/group " + getId() + " togglerentable");
			} else {
				fm.then("Rentable").color(ChatColor.RED).tooltip("Toggle the rentable state of the stall.").command("/group " + getId() + " togglerentable");
			}
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Remove").color(ChatColor.GOLD).tooltip("Removes this stall and kicks anyone renting it.").command("/group " + getId() + " remove");
		} else if(getRenter() == player){
			fm.then("\nRename").color(ChatColor.GOLD).tooltip("Rename this Stall.").suggest("/group " + getId() + " setname NEW NAME");
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Leave").color(ChatColor.GOLD).tooltip("Stop renting this Stall").command("/group " + getId() + " leave");
		} else if(getRenter() == null && isForRent()) {
			fm.then("\nRent").color(ChatColor.GOLD).tooltip("Start renting this Stall").command("/group " + getId() + " join");
		}
		*/
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	@Override
	public List<GroupAction> getGroupActionsFor(Player player){
		List<GroupAction> list = new ArrayList<GroupAction>();
		
		list.add(new GroupAction("Rename", "Rename this stall", ActionType.SUGGEST, "/group " + this.getId() + " rename <NEW NAME>", isOwner(player) || isRenter(player)));
		if(isOwner(player)){
			list.add(new GroupAction("Rentable", "Toggle the rentable state of this Stall", ActionType.TOGGLE, "/group " + getId() + " toggleRentable", isForRent()));
			list.add(new GroupAction("Kick", "Kick the player renting this stall", ActionType.COMMAND, "/group " + getId() + " kick", getRenter() != null));
		} else {
			if(isRenter(player)){
				list.add(new GroupAction("Leave", "Stop renting this Stall", ActionType.COMMAND, "/group " + getId() + " leave", true));
			} else {
				list.add(new GroupAction("Rent", "Start renting this Stall", ActionType.COMMAND, "/group " + getId() + " join", isForRent()));
			}
		}
		list.add(new GroupAction("Price", "Set the rent of this stall", ActionType.SUGGEST, "/group " + getId() + " setRent " + getRent(), isOwner(player)));
		list.add(new GroupAction("Remove", "Remove this stall and kick anyone renting it", ActionType.COMMAND, "/group " + getId() + " remove", isOwner(player)));
		
		return list;
	}
	
	/**
	 * Gets the file where this keep is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getMarketStallDirectory(), "" + getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	/*
	public static MarketStall load(YamlConfiguration cf, Map<String, Settlement> settlements){
		if(cf == null) return null;
		Instant creation;
		String name = cf.getString("Name");
		String settlementPath = cf.getString("SettlementPath");
		String shapes = cf.getString("Shape");
		Boolean forRent = cf.getBoolean("IsForRent");
		Double rent = cf.getDouble("Rent");
		String nextpayment = cf.getString("NextPayment");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a stall. Replacing it by NOW.");
		}
		Settlement settlement = null;
		if(settlementPath != null){
			if(settlementPath.contains("TOWNY")){
				settlement = Civilizations.loadSettlement(settlementPath);
			} else {
				UUID id = null;
				try{
					id = UUID.fromString(settlementPath);
					Group g = Group.get(id);
					if(g instanceof Settlement){
						settlement = (Settlement) g;
					}
				} catch (IllegalArgumentException e) {
					Civilizations.DEBUG("Not a valid UUID for " + name + "'s settlement.");
				}
			}
		}
		MarketStall g = new MarketStall(name, Util.parseShapes(shapes), settlement);
		g.getSettings().setCreationDate(creation);
		if(cf.contains("Owner")){
			UUID id = UUID.fromString(cf.getString("Owner"));
			if(id != null){
				g.setOwner(Bukkit.getOfflinePlayer(id));
			}
		}
		if(cf.contains("Renter")){
			UUID id = UUID.fromString(cf.getString("Renter"));
			if(id != null){
				g.setRenter(Bukkit.getOfflinePlayer(id));
			}
		}
		if(forRent != null){
			g.setForRent(forRent);
		}
		if(rent != null){
			g.setRent(rent);
		}
		if(!nextpayment.isEmpty()){
			g.setNextPayment(Instant.parse(nextpayment));
		}
		
		return g;
	}
	*/
}
