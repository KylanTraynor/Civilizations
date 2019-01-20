package com.kylantraynor.civilizations.groups.settlements.forts;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.kylantraynor.civilizations.utils.Identifier;
import com.kylantraynor.civilizations.utils.Utils;
import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.fort.Keep;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.territories.InfluenceMap;

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

	public SmallOutpost() {
		super();
	}

	@Override
	public String getType(){
		return "Small Outpost";
	}
	
	static public boolean hasUpgradeRequirements(Settlement s){
		for(Plot p : s.getPlots()){
			if(p instanceof Keep){
				for(Shape shape : p.getShapes()){
					for(Location b : shape.getBlockLocations()){
						if(Utils.isBanner(b.getBlock().getType()) || Utils.isWallBanner(b.getBlock().getType())){
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
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getSettings().getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getSettings().getCreationDate()))).color(ChatColor.GOLD);
		}
		String houseInfoCommand = "/house " + this.getHouse().getName() + " info";
		fm.then("\nOccupied by: ").color(ChatColor.GRAY).command(houseInfoCommand).
			then("" + this.getHouse().getName()).color(ChatColor.GOLD).
			command(houseInfoCommand);
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getIdentifier().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getIdentifier().toString() + " members");
		fm.then("\n" + ChatTools.getDelimiter()).color(ChatColor.GRAY);
		return fm;
	}
	
	/**
	 * Gets the file where this Small Outpost is saved.
	 * @return File
	 */
	@Override
	public File getFile(){
		File f = new File(Civilizations.getSmallOutpostDirectory(), "" + getIdentifier().toString() + ".yml");
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
	@Deprecated
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
		House h = House.get(house);
		SmallOutpost o = new SmallOutpost(new Location(w, x, y, z), House.get(house));
		o.getSettings().setCreationDate(creation);
		o.setHouse(h);
		
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
		//InfluenceMap.saveInfluenceMap(this);
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Location.World", getLocation().getWorld().getName());
		fc.set("Location.X", getLocation().getBlockX());
		fc.set("Location.Y", getLocation().getBlockY());
		fc.set("Location.Z", getLocation().getBlockZ());
		
		fc.set("Creation", getSettings().getCreationDate().toString());
		fc.set("House", getHouse().getName());
		fc.set("Influence", getInfluence());
		
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
