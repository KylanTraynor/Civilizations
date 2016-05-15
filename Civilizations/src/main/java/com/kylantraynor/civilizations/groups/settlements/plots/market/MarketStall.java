package com.kylantraynor.civilizations.groups.settlements.plots.market;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.hook.quickshop.QuickshopHook;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shops.Shop;
import com.kylantraynor.civilizations.shops.ShopManager;
import com.kylantraynor.civilizations.shops.ShopType;
import com.kylantraynor.civilizations.territories.InfluenceMap;

public class MarketStall extends Plot{

	private UUID owner;
	private UUID renter;
	private double rent = 1.0;
	private Instant nextPayment = Instant.now();
	private boolean forRent = true;
	
	public MarketStall(String name, Shape shape, Settlement settlement) {
		super(name.isEmpty() ? "Stall" : name, shape, settlement);
		Cache.marketstallListChanged = true;
	}
	
	public MarketStall(String name, List<Shape> shapes, Settlement settlement) {
		super(name.isEmpty() ? "Stall" : name, shapes, settlement);
		Cache.marketstallListChanged = true;
	}
	
	public String getIcon(){
		return "scales";
	}
	
	
	@Override
	public void update(){
		if(getRenter() != null){
			if(Instant.now().isAfter(nextPayment)){
				payRent();
				setChanged(true);
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
		Cache.marketstallListChanged = true;
		return super.remove();
	}
	
	@Override
	public String getType() {
		return "Market Stall";
	}
	
	public double getRent(){
		return rent;
	}
	
	public void setRent(double newRent){
		rent = newRent;
		setChanged(true);
	}
	
	public Map<ItemStack, Double> getWares(){
		try{
			Civilizations.DEBUG("Trying to find wares in stall.");
		Map<ItemStack, Double> wares = new HashMap<ItemStack, Double>();
		Location current = this.getProtection().getCenter().clone();
		for(Shape s : getProtection().getShapes()){
			for(int x = s.getMinX() - 1; x < s.getMaxX() + 1; x++){
				for(int y = s.getMinY(); y < s.getMaxY(); y++){
					for(int z = s.getMinZ() - 1; z < s.getMaxZ() + 1; z++){
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
			result.add((e.getValue() < 0 ? "Buying: " : "Selling: ") + e.getKey().getItemMeta().getDisplayName() + " " + Economy.format(Math.abs(e.getValue())));
		}
		return result;
	}
	
	public OfflinePlayer getOwner(){
		if(owner == null) return null;
		return Bukkit.getOfflinePlayer(owner);
	}
	
	public void setOwner(OfflinePlayer player){
		if(player == null){
			owner = null;
		} else {
			owner = player.getUniqueId();
		}
		setChanged(true);
	}
	
	public OfflinePlayer getRenter(){
		if(renter == null) return null;
		return Bukkit.getOfflinePlayer(renter);
	}
	
	public void setRenter(OfflinePlayer player){
		if(player == null){
			renter = null;
		} else {
			renter = player.getUniqueId();
		}
		setChanged(true);
	}

	public void payRent() {
		if(this.renter == null){
			return;
		}
		nextPayment = Instant.now().plus(1, ChronoUnit.DAYS);
		OfflinePlayer renter = Bukkit.getOfflinePlayer(this.renter);
		if(this.owner != null){
			OfflinePlayer owner = Bukkit.getOfflinePlayer(this.owner);
			if(Economy.withdrawPlayer(renter, rent)){
				if(renter.isOnline()){
					renter.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(rent) + " in rent.");
					Economy.playPaySound(renter.getPlayer());
				}
				double payout = rent;
				//Pay Settlement's Stall Tax
				if(getSettlement() != null){
					if(Economy.depositSettlement(getSettlement(), rent * getSettlement().getStallRentTax())){
						payout -= rent * getSettlement().getStallRentTax();
					}
				}
				//Pay Fort's Stall Tax
				Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
				if(f != null){
					if(Economy.depositSettlement(f, rent * f.getStallRentTax())){
						payout -= rent * f.getStallRentTax();
					}
				}
				//Pay Owner
				Economy.depositPlayer(owner, payout);
				if(owner.isOnline()){
					owner.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've received " + Economy.format(rent) + " for the rent.");
					Economy.playCashinSound(owner.getPlayer());
				}
			}
		} else if(getSettlement() != null) {
			if(Economy.withdrawPlayer(renter, rent)){
				if(renter.isOnline()){
					renter.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(rent) + " in rent.");
					Economy.playPaySound(renter.getPlayer());
				}
				double payout = rent;
				//Pay Fort's Stall Tax
				Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
				if(f != null){
					if(Economy.depositSettlement(f, rent * f.getStallRentTax())){
						payout -= rent * f.getStallRentTax();
					}
				}
				//Pay Settlement
				Economy.depositSettlement(getSettlement(), payout);
			}
		} else {
			//Pay Fort
			Fort f = InfluenceMap.getInfluentFortAt(getProtection().getCenter());
			if(f != null){
				if(Economy.withdrawPlayer(renter, rent)){
					if(renter.isOnline()){
						renter.getPlayer().sendMessage(this.getChatHeader() + ChatColor.GREEN + "You've paid " + Economy.format(rent) + " in rent.");
						Economy.playPaySound(renter.getPlayer());
					}
					Economy.depositSettlement(f, rent);
				}
			}
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
			fm.then("\nNext Payment in ").color(ChatColor.GRAY).then("" + ChronoUnit.HOURS.between(Instant.now(), this.nextPayment) + " hours").color(ChatColor.GOLD);
		}
		fm.then("\nActions: ").color(ChatColor.GRAY);
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
		} else if(getRenter() == player){
			fm.then("\nRename").color(ChatColor.GOLD).tooltip("Rename this Stall.").suggest("/group " + getId() + " setname NEW NAME");
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Leave").color(ChatColor.GOLD).tooltip("Stop renting this Stall").command("/group " + getId() + " leave");
		} else if(getRenter() == null && isForRent()) {
			fm.then("\nRent").color(ChatColor.GOLD).tooltip("Start renting this Stall").command("/group " + getId() + " join");
		}
		
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
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
	
	/**
	 * Loads Stall from its configuration file.
	 * @param cf
	 * @return Group
	 */
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
			if(settlements.get(settlementPath) == null){
				Settlement s = Civilizations.loadSettlement(settlementPath);
				if(s!= null){
					settlements.put(settlementPath, s);
					settlement = s;
				}
			}
			
		}
		MarketStall g = new MarketStall(name, Plot.parseShapes(shapes), settlement);
		g.setCreationDate(creation);
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
	private void setNextPayment(Instant instant) {
		nextPayment = instant;
	}

	/**
	 * Saves the Stall to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Name", getName());
		if(getSettlement() != null){
			if(getSettlement() instanceof TownyTown){
				fc.set("SettlementPath", "TOWNY: " + getSettlement().getName());
			} else {
				fc.set("SettlementPath", getSettlement().getFile().getAbsolutePath());
			}
		} else {
			fc.set("SettlementPath", null);
		}
		fc.set("Shape", getShapesString());
		fc.set("IsForRent", isForRent());
		fc.set("Rent", getRent());
		fc.set("NextPayment", nextPayment.toString());
		fc.set("Creation", getCreationDate().toString());
		if(owner != null){
			fc.set("Owner", owner.toString());
		}
		if(renter != null){
			fc.set("Renter", renter.toString());
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean isForRent() {
		return forRent;
	}

	public void setForRent(boolean forRent) {
		this.forRent = forRent;
		setChanged(true);
	}
	
}
