package com.kylantraynor.civilizations.groups.settlements.forts;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.shapes.Shape;

public class SmallOutpost extends Fort{

	public SmallOutpost(Camp c, House house) {
		super(c.getLocation(), house);
		this.setMembers(c.getMembers());
		for(Plot p : c.getPlots().toArray(new Plot[c.getPlots().size()])){
			p.setSettlement(this);
		}
		c.remove();
		setChanged(true);
	}

	public SmallOutpost(Location location, House house) {
		super(location, house);
		setChanged(true);
	}

	@Override
	public String getType(){
		return "Small Outpost";
	}
	
	static public boolean hasUpgradeRequirements(Settlement s){
		for(Plot p : s.getPlots()){
			if(p instanceof Keep){
				for(Shape shape : p.getProtection().getShapes()){
					for(Location b : shape.getBlockLocations()){
						if(b.getBlock().getType() == Material.BANNER || b.getBlock().getType() == Material.STANDING_BANNER){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets an interactive info panel of this Keep.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage("========== " + getName().toUpperCase() + " ==========").color(ChatColor.GOLD);
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getCreationDate()))).color(ChatColor.GOLD);
		}
		String houseInfoCommand = "/house " + this.getHouse().getName() + " info";
		fm.then("\nOccupied by: ").color(ChatColor.GRAY).command(houseInfoCommand).
			then("" + this.getHouse().getName()).color(ChatColor.GOLD).
			command(houseInfoCommand);
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getId() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getId() + " members");
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	}
	
	/**
	 * Gets the file where this Small Outpost is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getSmallOutpostDirectory(), "" + getId() + ".yml");
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
	 * Loads a SmallOutpost from its configuration file.
	 * @param cf
	 * @return SmallOutpost
	 */
	public static SmallOutpost load(YamlConfiguration cf){
		World w = Civilizations.currentInstance.getServer().getWorld(cf.getString("Location.World"));
		double x = cf.getDouble("Location.X");
		double y = cf.getDouble("Location.Y");
		double z = cf.getDouble("Location.Z");
		Instant creation;
		String house = cf.getString("House");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		
		SmallOutpost o = new SmallOutpost(new Location(w, x, y, z), House.get(house));
		o.setCreationDate(creation);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			o.addMember(Bukkit.getServer().getOfflinePlayer(UUID.fromString((cf.getString("Members."+i)))));
			i+=1;
		}
		
		return o;
	}
	/**
	 * Saves the Small Outpost to its file.
	 * @return true if the small outpost has been saved, false otherwise.
	 */
	@Override
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Location.World", getLocation().getWorld().getName());
		fc.set("Location.X", getLocation().getBlockX());
		fc.set("Location.Y", getLocation().getBlockY());
		fc.set("Location.Z", getLocation().getBlockZ());
		
		fc.set("Creation", getCreationDate().toString());
		
		int i = 0;
		for(UUID id : getMembers()){
			fc.set("Members." + i, id.toString());
			i += 1;
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
