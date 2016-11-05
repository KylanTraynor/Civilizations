package com.kylantraynor.civilizations.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.Warehouse;
import com.kylantraynor.civilizations.groups.settlements.plots.fort.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.hook.draggyrpg.DraggyRPGHook;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.shapes.Shape;

public class GroupManager {
	
	Map<String, Settlement> loadedSettlements = new HashMap<String, Settlement>();
	
	public static Group createGroup(){
		return new Group();
	}
	
	public static Settlement createSettlement(){
		return null;
	}
	
	public static void loadAll(){
		loadGroups();
		loadHouses();
		loadCamps();
		loadPlots();
	}
	
	/**
	 * Loads the data from the file into the given group.
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
		File groupDir = Civilizations.getCampDirectory();
		if(groupDir.exists()){
			for(File f : groupDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					Civilizations.log("WARNING", "Couldn't find file " + f.getName());
				} catch (IOException e) {
					Civilizations.log("WARNING", "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					Civilizations.log("WARNING", "Invalid file configuration.");
				}
				//Group.load(yaml);
			}
		}
	}
	
	private static void loadPlots() {
		loadDirectory(Keep.class, Civilizations.getKeepDirectory());
		//loadKeeps();
		loadDirectory(MarketStall.class, Civilizations.getMarketStallDirectory());
		//loadStalls();
		loadDirectory(com.kylantraynor.civilizations.groups.settlements.plots.House.class, Civilizations.getHousePlotDirectory());
		//loadPlotHouses();
		loadDirectory(Warehouse.class, Civilizations.getWarehousesDirectory());
		//loadWarehouses();
	}

	private static void loadDirectory(Class c, File directory){
		if(directory.exists()){
			Civilizations.log("INFO", "Loading " + c.getSimpleName() + "...");
			for(File f : directory.listFiles()){
				try{
					if(!f.getName().split("\\.")[1].equalsIgnoreCase("yml")) continue;
					Civilizations.log("INFO", "Loading " + c.getSimpleName() + " from file: " + f.getPath());
					load(f, (Group) c.newInstance());
					f.delete();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	private static void loadKeeps() {
		File keepDir = Civilizations.getKeepDirectory();
		if(keepDir.exists()){
			Civilizations.log("INFO", "Loading Keeps...");
			for(File f : keepDir.listFiles()){
				try{
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					Civilizations.log("INFO", "Loading Keep from file: " + f.getPath());
					load(f, new Keep());
					f.delete();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadStalls() {
		File stallDir = Civilizations.getMarketStallDirectory();
		if(stallDir.exists()){
			Civilizations.log("INFO", "Loading Stalls...");
			for(File f : stallDir.listFiles()){
				try{
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					Civilizations.log("INFO", "Loading Stall from file: " + f.getPath());
					load(f, new MarketStall());
					f.delete();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void loadPlotHouses() {
		File plotHousesDir = Civilizations.getHousePlotDirectory();
		if(plotHousesDir.exists()){
			Civilizations.log("INFO", "Loading Houses...");
			for(File f : plotHousesDir.listFiles()){
				try{
					if(!f.getName().split("\\.")[1].equals("yml")) continue;
					Civilizations.log("INFO", "Loading House from file: " + f.getPath());
					load(f, new com.kylantraynor.civilizations.groups.settlements.plots.House());
					f.delete();
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static Settlement loadSettlement(String path){
		Civilizations.log("INFO", "Getting settlement from " + path);
		if(path.contains("TOWNY: ")){
			if(TownyHook.isActive()){
				return TownyHook.loadTownyTown(path.replace("TOWNY: ", ""));
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
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(Civilizations.isClearing() ){
					Civilizations.log("INFO", "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				load(f, new Camp());
				f.delete();
			}
		}
	}

	private static void loadHouses() {
		File houseDir = Civilizations.getHouseDirectory();
		if(houseDir.exists()){
			for(File f : houseDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(Civilizations.isClearing() ){
					Civilizations.log("INFO", "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				load(f, new House());
				f.delete();
			}
		}
	}
	
	/**
	 * Update each individual group in the group list.
	 */
	public static void updateAllGroups(){
		for(Group g : CacheManager.getGroupList()){
			g.update();
		}
		if(DraggyRPGHook.isActive()){
			DraggyRPGHook.updateLevelCenters();
		}
		Civilizations.log("INFO", "Files saved!");
	}
	
	/**
	 * Creates a new camp at the given location.
	 * @param l Location of the new camp.
	 * @return The camp created.
	 */
	public static Camp createCamp(Location l){
		return new Camp(l);
	}
	
	/**
	 * Creates a new MarketStall at the given location.
	 * @param s The selection.
	 * @return The new Market Stall.
	 */
	public static MarketStall createMarketStall(Selection s){
		Settlement set = Settlement.getClosest(s.getLocation());
		boolean canMerge = false;
		for(Plot p : set.getPlots()){
			if(!canMerge){
				for(Shape shape : p.getSettings().getShapes()){
					if(shape.distance(s) <= Civilizations.SETTLEMENT_MERGE_RADIUS){
						canMerge = true;
						break;
					}
				}
			}
		}
		if(!canMerge) return null;
		return new MarketStall("Stall", s, set);
	}

	/**
	 * Updates all the builders, in all concerned groups.
	 */
	public static void updateAllBuilders() {
		for(Group g : CacheManager.getGroupList()){
			if(g instanceof HasBuilder){
				((HasBuilder) g).getBuilder().update();
			}
		}
	}
}