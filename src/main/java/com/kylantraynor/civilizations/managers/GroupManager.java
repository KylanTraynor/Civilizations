package com.kylantraynor.civilizations.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.events.CampCreateEvent;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.hook.HookManager;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.settings.BuilderSettings;
import com.kylantraynor.civilizations.settings.CampSettings;
import com.kylantraynor.civilizations.settings.HouseSettings;
import com.kylantraynor.civilizations.settings.GroupSettings;
import com.kylantraynor.civilizations.settings.PlotSettings;
import com.kylantraynor.civilizations.settings.SettlementSettings;
import com.kylantraynor.civilizations.shapes.Shape;

public class GroupManager {
	
	Map<String, Settlement> loadedSettlements = new HashMap<String, Settlement>();

    /**
     * Creates a {@linkplain Group} with the given name and parent.
     * @param name The name of the new group.
     * @param parent The parent {@link Group}.
     * @return
     */
	public static Group createGroup(String name, Group parent){
		Group g = new Group();
		g.setName(name);
		g.setParent(parent);
		return g;
	}
	
	public static void loadAll(){
		loadBuilders();
		loadGroups();
		loadHouses();
		loadCamps();
		loadSettlements();
		loadPlots();
	}
	
	private static void loadSettlements() {
		File settlementDir = Civilizations.getSettlementDirectory();
		if(settlementDir.exists()){
			for(File f : settlementDir.listFiles()){
				try {
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					if(Civilizations.isClearing() ){
						Civilizations.log("INFO", "Cleared file " + f.getName());
						f.delete();
						continue;
					}
					SettlementSettings ss = new SettlementSettings();
					ss.load(f);
					new Settlement(ss);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void loadBuilders() {
		File dir = Civilizations.getBuilderDirectory();
		for(File f : dir.listFiles()){
			BuilderSettings s = new BuilderSettings();
			try {
				s.load(f);
				new Builder(s);
				f.delete();
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Makes sure that all the plots declaring a settlement are
	 * indeed in the plot list of that settlement.
	 * @deprecated This method is weird and should be avoided.
	 */
	public static void ensurePlotsAreLinked(){
		for(Group g : Group.getList()){
			if(!(g instanceof Plot)) continue;
			Plot p = (Plot) g;
			if(p.getSettlement() != null){
				//Will not add the plot if it's already included, so it's all good.
				p.getSettlement().addPlot(p);
			}
		}
	}

	/**
	 * Loads the data from the file into the given group.
	 * @deprecated Use {@link Group#Group(GroupSettings)}
	 * @param file
	 * @param group
	 * @return
	 */
	public static <T extends Group> T load(File file, T group){
		if(file == null) return null;
		try {
			group.getSettings().load(file);
			group.postLoad();
			return group;
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Loads the Groups from their files.
	 */
	private static void loadGroups() {
		File groupDir = Civilizations.getGroupDirectory();
		if(groupDir.exists()){
			for(File f : groupDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				try{
				    GroupSettings yaml = new GroupSettings();
				    yaml.load(f);
				    new Group(yaml);
                } catch (Exception e){
				    e.printStackTrace();
                }
			}
		}
	}
	
	private static void loadPlots() {
		loadPlotDirectory(PlotType.KEEP, Civilizations.getPlotDirectory(PlotType.KEEP));
		loadPlotDirectory(PlotType.MARKETSTALL, Civilizations.getPlotDirectory(PlotType.MARKETSTALL));
		loadPlotDirectory(PlotType.HOUSE, Civilizations.getPlotDirectory(PlotType.HOUSE));
		loadPlotDirectory(PlotType.WAREHOUSE, Civilizations.getPlotDirectory(PlotType.WAREHOUSE));
		loadPlotDirectory(PlotType.CROPFIELD, Civilizations.getPlotDirectory(PlotType.CROPFIELD));
		loadPlotDirectory(PlotType.BANK, Civilizations.getPlotDirectory(PlotType.BANK));
	}

	private static void loadPlotDirectory(PlotType type, File directory){
		if(directory.exists()){
			Civilizations.log("INFO", "Loading " + type.toString() + "...");
			for(File f : directory.listFiles()){
				try{
					if(!f.getName().split("\\.")[1].equalsIgnoreCase("yml")) continue;
					Civilizations.log("INFO", "Loading " + type.toString() + " from file: " + f.getPath());
					PlotSettings settings = new PlotSettings();
					settings.load(f);
					Plot p = new Plot(settings);
					//load(f, p);
					p.setPersistent(true);
					p.setPlotType(type);
					//f.delete();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Deprecated
	public static Settlement loadSettlement(String path){
		Civilizations.log("INFO", "Getting settlement from " + path);
		if(path.contains("TOWNY: ")){
			if(HookManager.getTowny() != null){
				return HookManager.getTowny().loadTownyTown(path.replace("TOWNY: ", ""));
			} else { return null;}
		} else {
			File f = new File(path);
			if(f.exists()){
				if(!f.getName().split("\\.")[1].equals("yml")) return null;
				Group g = null;
				String[] pathSplit = path.split(File.separator);
				Civilizations.log("INFO", "Settlement type: " + pathSplit[pathSplit.length - 2]);
				switch(pathSplit[pathSplit.length - 2]){
				case "Camps":
					Civilizations.log("INFO", "Loading camp from " + path);
					g = new Camp();
				case "Small Outposts":
					Civilizations.log("INFO", "Loading small outpost from " + path);
					g = new SmallOutpost();
				}
				g = load(f, g);
				f.delete();
				return (Settlement)g;
			}
		}
		return null;
	}
	
	/**
	 * Loads the Camps from their files.
	 */
	private static void loadCamps() {
		File campDir = Civilizations.getCampDirectory();
		if(campDir.exists()){
			for(File f : campDir.listFiles()){
				try {
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					if(Civilizations.isClearing() ){
						Civilizations.log("INFO", "Cleared file " + f.getName());
						f.delete();
						continue;
					}
					CampSettings cs = new CampSettings();
					cs.load(f);
					new Camp(cs);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void loadHouses() {
		File houseDir = Civilizations.getHouseDirectory();
		if(houseDir.exists()){
			for(File f : houseDir.listFiles()){
				try {
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					if(Civilizations.isClearing() ){
						Civilizations.log("INFO", "Cleared file " + f.getName());
						f.delete();
						continue;
					}
					HouseSettings settings = new HouseSettings();
					settings.load(f);
					new House(settings);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Update each individual group in the group list.
	 */
	public static void updateAllGroups(){
		Group[] groups = Group.getList().toArray(new Group[0]);
		for(Group g : groups){
			if(g == null) continue;
			g.update();
		}
		if(HookManager.getDraggyRPG() != null){
			HookManager.getDraggyRPG().updateLevelCenters();
		}
		Civilizations.log("INFO", "Files saved!");
	}
	
	/**
	 * Creates a new camp at the given location.
	 * @param l Location of the new camp.
	 * @return The camp created.
	 */
	public static Camp createCamp(Player p, Location l){
		Settlement set = Settlement.getClosest(l);
		if(set != null){
			if(set.distance(l) <= Camp.getSize() * 2){
				p.sendMessage(Camp.messageHeader + ChatColor.RED + "Too close to another settlement.");
				return null;
			}
		}
		CampCreateEvent event = new CampCreateEvent(p, l);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			Camp camp = new Camp(l);
			camp.addMember(EconomicEntity.get(p.getUniqueId()));
			return camp;
		}
		return null;
	}
	
	/**
	 * Creates a new MarketStall at the given location.
	 * @param s The selection.
	 * @return The new Market Stall.
	 */
	public static Plot createMarketStall(Selection s){
		Settlement set = Settlement.getClosest(s.getLocation());
		boolean canMerge = false;
		for(Plot p : set.getPlots()){
			if(!canMerge){
				for(Shape shape : p.getSettings().getShapes()){
					if(shape.distanceSquared((Shape)s) <= Civilizations.getSettings().getSettlementMergeDistanceSquared()){
						canMerge = true;
						break;
					}
				}
			}
		}
		if(!canMerge) return null;
		Plot p = new Plot("Stall", (Shape)s, set);
		p.setPersistent(true);
		p.setPlotType(PlotType.MARKETSTALL);
		return p;
	}

	/**
	 * Updates all the builders, in all concerned groups.
	 */
	public static void updateAllBuilders() {
		Group[] groups = Group.getList().toArray(new Group[0]);
		for(Group g : groups){
			if(g instanceof HasBuilder){
				((HasBuilder) g).getBuilder().update();
			}
		}
	}

	/**
	 * Cancels all the builds in all builders.
	 */
	public static void cancelAllBuilds() {
		Group[] groups = Group.getList().toArray(new Group[0]);
		for(Group g : groups){
			if(g instanceof HasBuilder){
				((HasBuilder) g).getBuilder().clearProjects();
			}
		}
	}
	
	public static Settlement convertToSettlement(Camp camp){
		Settlement s = new Settlement(camp.getSettings());
		s.setPlots(camp.getPlots());
		Civilizations.currentInstance.getLogger().info("Converted Camp " + camp.getId() + " into a Settlement (id: " + s.getId() + ")");;
		return s;
	}
	
	public static void updateForEconomy() {
		Group[] groups = Group.getList().toArray(new Group[0]);
		for(Group g : groups){
			if(g != null){
				// Lets the parent (or the server) tax this group.
				if(!(g instanceof Camp)){
					g.processTaxes();
				}
			}
		}
	}

	public static Group get(UUID groupId) {
		return Group.get(groupId);
	}
}