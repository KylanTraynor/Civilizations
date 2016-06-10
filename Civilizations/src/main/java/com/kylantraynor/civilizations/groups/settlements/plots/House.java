package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Shape;

public class House extends Plot {

	public House(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
	}
	public House(String name, List<Shape> shapes, Settlement settlement) {
		super(name.isEmpty() ? "House" : name, shapes, settlement);
	}
	
	/**
	 * Gets an interactive info panel of this House.
	 * @param player Context
	 * @return FancyMessage
	 */
	public FancyMessage getInteractiveInfoPanel(Player player) {
		FancyMessage fm = new FancyMessage(ChatTools.formatTitle(getName().toUpperCase(), null));
		DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy");
		if(getCreationDate() != null){
			fm.then("\nCreation Date: ").color(ChatColor.GRAY).
				then(format.format(Date.from(getCreationDate()))).color(ChatColor.GOLD);
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
	 * Loads House from its configuration file.
	 * @param cf
	 * @return Group
	 */
	public static House load(YamlConfiguration cf, Map<String, Settlement> settlements){
		if(cf == null) return null;
		Instant creation;
		String name = cf.getString("Name");
		String settlementPath = cf.getString("SettlementPath");
		String shapes = cf.getString("Shape");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a house. Replacing it by NOW.");
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
		House g = new House(name, Plot.parseShapes(shapes), settlement);
		g.setCreationDate(creation);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			g.getMembers().add(UUID.fromString((cf.getString("Members."+i))));
			i+=1;
		}
		
		return g;
	}
	/**
	 * Saves the keep to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Name", getName());
		if(getSettlement() != null){
			fc.set("SettlementPath", getSettlement().getFile().getAbsolutePath());
		} else {
			fc.set("SettlementPath", null);
		}
		fc.set("Shape", getShapesString());
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