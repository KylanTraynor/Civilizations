package com.kylantraynor.civilizations.groups.settlements.plots.fort;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.groups.settlements.plots.FortComponent;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.util.Util;

public class Keep extends Plot implements FortComponent{

	public Keep(String name, Shape shape, Settlement settlement) {
		super(name.isEmpty() ? "Keep" : name, shape, settlement);
	}
	
	public Keep(String name, List<Shape> shapes, Settlement settlement) {
		super(name.isEmpty() ? "Keep" : name, shapes, settlement);
	}
	
	public Keep(PlotSettings settings) {
		super(settings);
	}

	public PlotType getPlotType(){
		return PlotType.KEEP;
	}
	
	@Override
	public Fort getFort(){
		if(getSettlement() instanceof Fort){
			return (Fort) getSettlement();
		}
		return null;
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
		String houseInfoCommand = "/house " + ((Fort)getSettlement()).getHouse().getName() + " info";
		fm.then("\nOccupied by: ").color(ChatColor.GRAY).command(houseInfoCommand).
			then("" + ((Fort)getSettlement()).getHouse().getName()).color(ChatColor.GOLD).
			command(houseInfoCommand);
		fm.then("\nMembers: ").color(ChatColor.GRAY).command("/group " + this.getUniqueId().toString() + " members").
			then("" + getMembers().size()).color(ChatColor.GOLD).command("/group " + this.getUniqueId().toString() + " members");
		
		fm.then("\nActions: ").color(ChatColor.GRAY);
		if(this.isMember(EconomicEntity.get(player.getUniqueId()))){
			if(ProtectionManager.hasPermission(PermissionType.MANAGE_PLOTS, getSettlement(), player, true)){
				fm.then("\nRename").color(ChatColor.GOLD).tooltip("Rename this Keep.").suggest("/group " + getUniqueId().toString() + " setname NEW NAME");
			} else {
				fm.then("\nRename").color(ChatColor.GRAY).tooltip("You don't have the MANAGE PLOTS permission here.");
			}
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
		File f = new File(Civilizations.getKeepDirectory(), "" + getUniqueId().toString() + ".yml");
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
	 * Loads Keep from its configuration file.
	 * @param cf
	 * @return Group
	 */
	public static Keep load(YamlConfiguration cf, Map<String, Settlement> settlements){
		if(cf == null) return null;
		Instant creation;
		String name = cf.getString("Name");
		String settlementPath = cf.getString("SettlementPath");
		String shapes = cf.getString("Shape");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a keep. Replacing it by NOW.");
		}
		Settlement settlement = null;
		if(settlementPath != null){
			if(settlements.get(settlementPath) == null){
				Settlement s = GroupManager.loadSettlement(settlementPath);
				if(s!= null){
					settlements.put(settlementPath, s);
					settlement = s;
				}
			}
			
		}
		Keep g = new Keep(name, Util.parseShapes(shapes), settlement);
		g.getSettings().setCreationDate(creation);
		
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
		fc.set("Shape", Util.getShapesString(getShapes()));
		fc.set("Creation", getSettings().getCreationDate().toString());
		
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