package com.kylantraynor.civilizations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.commands.CommandAnswer;
import com.kylantraynor.civilizations.commands.CommandCamp;
import com.kylantraynor.civilizations.commands.CommandCivilizations;
import com.kylantraynor.civilizations.commands.CommandGroup;
import com.kylantraynor.civilizations.commands.CommandHouse;
import com.kylantraynor.civilizations.commands.CommandPlot;
import com.kylantraynor.civilizations.commands.CommandSelection;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.hook.towny.CommandTownyTown;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.listeners.CivilizationsListener;
import com.kylantraynor.civilizations.listeners.MenuListener;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.protection.Protection;

public class Civilizations extends JavaPlugin{
	
	/**
	 * Plugin Constants
	 */
	public static final String MC_SERVER_VERSION = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	public static final String PLUGIN_NAME = "Civilizations";
	public static final int settlementMergeRadius = 25;
	
	/**
	 * Currently running instance of Civilizations
	 */
	public static Civilizations currentInstance;
	
	/**
	 * Header displayed in chat
	 */
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GOLD + ChatColor.BOLD + "Civilizations" + ChatColor.GOLD + "] ";

	private boolean reload;
	private boolean clearing = false;
	private ArrayList<Player> playersInProtectionMode = new ArrayList<Player>();
	static private HashMap<Player, Location[]> selectionPoints = new HashMap<Player, Location[]>();
	static private HashMap<Player, Protection> selectedProtections = new HashMap<Player, Protection>();
	static private FileConfiguration config;
	
	public static HashMap<Player, Protection> getSelectedProtections(){ return selectedProtections; }
	
	private static List<BukkitRunnable> processes = new ArrayList<BukkitRunnable>();
	/*
	 * Listeners
	 */
	private static CivilizationsListener mainListener = new CivilizationsListener();
	private static MenuListener menuListener = new MenuListener();
	
	/**
	 * Returns the main listener of Civilizations.
	 * @return CivilizationsListener
	 */
	public static CivilizationsListener getMainListener(){ return mainListener; }
	
	/**
	 * Returns the FileConfiguration of the current running instance.
	 * @return FileConfiguration
	 */
	public static FileConfiguration getInstanceConfig(){
		return currentInstance.getConfig();
	}
	
	/**
	 * Sends a message to the console with the specified level.
	 * @param level of the message.
	 * @param message to send.
	 * @see Level
	 */
	public static void log(String level, String message){
		Level lvl = Level.parse(level);
		if(lvl != null){
			Bukkit.getServer().getLogger().log(lvl, message);
		} else {
			Bukkit.getServer().getLogger().log(Level.ALL, "["+level+"] " + message);
		}
	}
	
	/**
	 * Function called when the plugin is enabled.
	 */
	@Override
	public void onEnable(){
		currentInstance = this;
		saveDefaultConfig();
		config = this.getConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(getMainListener(), this);
		pm.registerEvents(getMenuListener(), this);
		
		registerAchievement("Setting up Camp!","Create a camp.");
		
		loadGroups();
		loadHouses();
		loadPlots();
		loadCamps();
		
		loadHooks(pm);
		
		startGroupUpdater(20L * 60 * 5);
		startProtectionUpdater(40L);
		
		setupCommands();
	}

	private void loadPlots() {
		Map<String, Settlement> settlements = new HashMap<String, Settlement>();
		loadKeeps(settlements);
	}

	private void loadKeeps(Map<String, Settlement> settlements) {
		File houseDir = getHouseDirectory();
		if(houseDir.exists()){
			for(File f : houseDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(isClearing() ){
					log("INFO", "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					log("WARNING", "Couldn't find file " + f.getName());
				} catch (IOException e) {
					log("WARNING", "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					log("WARNING", "Invalid file configuration.");
				}
				f.delete();
				Keep h = Keep.load(yaml, settlements);
			}
		}
	}
	
	public static Settlement loadSettlement(String path){
		File f = new File(path);
		if(f.exists()){
			if(!f.getName().split("\\.")[1].equals("yml")) return null;
			YamlConfiguration yaml = new YamlConfiguration();
			try {
				yaml.load(f);
			} catch (FileNotFoundException e) {
				log("WARNING", "Couldn't find file " + f.getName());
			} catch (IOException e) {
				log("WARNING", "File " + f.getName() + " is in use in another application.");
			} catch (InvalidConfigurationException e) {
				log("WARNING", "Invalid file configuration.");
			}
			f.delete();
			switch(path.split("/")[path.split("/").length - 2]){
			case "Camps":
				return Camp.load(yaml);	
			case "Small Outpost":
				return SmallOutpost.load(yaml);
			}
		}
		return null;
	}

	private void loadHouses() {
		File houseDir = getHouseDirectory();
		if(houseDir.exists()){
			for(File f : houseDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(isClearing() ){
					log("INFO", "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					log("WARNING", "Couldn't find file " + f.getName());
				} catch (IOException e) {
					log("WARNING", "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					log("WARNING", "Invalid file configuration.");
				}
				f.delete();
				House h = House.load(yaml);
			}
		}
	}

	private Listener getMenuListener() {
		return this.menuListener;
	}

	/**
	 * Loads all the hooks allowing the interaction with other plugins.
	 * @param pm PluginManager
	 */
	private void loadHooks(PluginManager pm) {
		if(DynmapHook.load(pm)){ log("INFO", "Hook to Dynmap: OK");
		} else { log("WARNING", "Hook to Dynmap: NO, " + PLUGIN_NAME + " will not be displayed.");
		}
		if(TitleManagerHook.load(pm)){ log("INFO", "Hook to TitleManager: OK");
		} else { log("WARNING", "Hook to Titlemanager: NO");
		}
		if(TownyHook.load(pm)){ log("INFO", "Side by side with Towny: OK");
		} else { log("INFO", "Side by side with Towny: NO");
		}
		
		if(DynmapHook.isEnabled()) DynmapHook.activateDynmap();
		if(TownyHook.isEnabled()) TownyHook.loadTownyTowns();
	}
	
	/**
	 * Setups all the commands for Civilizations.
	 */
	private void setupCommands() {
		this.getCommand("Civilizations").setExecutor(new CommandCivilizations());
		this.getCommand("CivilizationsAnswer").setExecutor(new CommandAnswer());
		
		this.getCommand("Group").setExecutor(new CommandGroup());
		this.getCommand("House").setExecutor(new CommandHouse());
		this.getCommand("Camp").setExecutor(new CommandCamp());
		
		this.getCommand("Selection").setExecutor(new CommandSelection());
		
		this.getCommand("Plot").setExecutor(new CommandPlot());
		
		if(TownyHook.isEnabled()){
			this.getCommand("TownyTown").setExecutor(new CommandTownyTown());
		}
	}
	
	/**
	 * Starts the process updating the group.
	 * @param interval in ticks between updates.
	 */
	private void startGroupUpdater(long interval) {
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				for(Group g : Cache.getGroupList()){
					g.update();
				}
				log("INFO", "Files saved!");
			}
			
		};
		br.runTaskTimer(this, interval, interval);
	}
	
	/**
	 * Starts process updating protection visibility in an asynchronous thread.
	 * @param interval in ticks between updates.
	 */
	private void startProtectionUpdater(long interval) {
		BukkitRunnable pr = new BukkitRunnable(){

			@Override
			public void run() {
				BukkitRunnable[] prcs = Civilizations.getProcesses().toArray(new BukkitRunnable[Civilizations.getProcesses().size()]);
				for(BukkitRunnable r : prcs){
					if(Civilizations.getProcesses().contains(r)){
						Civilizations.getProcesses().remove(r);
					}
					r.runTask(Civilizations.currentInstance);
				}
			}
			
		};
		pr.runTaskTimerAsynchronously(this, interval, interval);
	}
	
	/**
	 * Updates the given protection's visibility for the given player.
	 * @param player to display the change to.
	 * @param protection to update.
	 */
	protected static void updateProtectionVisibility(Player player, Protection protection) {
		if(getPlayersInProtectionMode().contains(player)){
			if(Civilizations.getSelectedProtections().get(player).equals(protection)){
				protection.highlight(player);
			} else {
				protection.hide(player);
			}
		} else {
			protection.hide(player);
		}
	}

	/**
	 * Loads the Groups from their files.
	 */
	private void loadGroups() {
		File groupDir = getCampDirectory();
		if(groupDir.exists()){
			for(File f : groupDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					log("WARNING", "Couldn't find file " + f.getName());
				} catch (IOException e) {
					log("WARNING", "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					log("WARNING", "Invalid file configuration.");
				}
				Group.load(yaml);
			}
		}
	}

	/**
	 * Loads the Camps from their files.
	 */
	private void loadCamps() {
		File campDir = getCampDirectory();
		if(campDir.exists()){
			for(File f : campDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(isClearing() ){
					log("INFO", "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					log("WARNING", "Couldn't find file " + f.getName());
				} catch (IOException e) {
					log("WARNING", "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					log("WARNING", "Invalid file configuration.");
				}
				f.delete();
				Camp c = Camp.load(yaml);
				c.setDefaultPermissions();
			}
		}
	}

	@Override
	public void onDisable(){
		for(Group g : Cache.getGroupList()){
			g.update();
		}
		if(DynmapHook.isEnabled()){
			DynmapHook.disable();
		}
	}
	
	/**
	 * Select the protection the given player is targeting.
	 * @param player
	 */
	public static void selectTargetProtection(Player player) {
		Location l = null;
		for(Block b : player.getLineOfSight((Set<Material>) null, 100)){
			if(b.getType() != Material.AIR){
				if(Settlement.isProtected(b.getLocation())){
					l = b.getLocation();
				}
				break;
			}
		}
		if(l != null){
			Settlement s = Settlement.getAt(l);
			Protection newProt = s.getProtection();
			Plot plot = null;
			if(s instanceof TownyTown){
				for(Plot p : s.getPlots()){
					if(p.protects(l)){
						plot = p;
						newProt = p.getProtection();
					}
				}
			}
			Protection old = Civilizations.getSelectedProtections().get(player);
			if(newProt.equals(old)){
				player.sendMessage(messageHeader + ChatColor.RED + "Protection already selected.");
				return;
			}
			Civilizations.getSelectedProtections().put(player, newProt);
			String plotName = "Settlement.";
			if(plot != null) plotName = " " + plot.getName() + ".";
			player.sendMessage(messageHeader + ChatColor.GREEN + "Protection selected: " + s.getName() + " " + plotName);
			if(old != null) updateProtectionVisibility(player, old);
			updateProtectionVisibility(player, newProt);
		} else {
			if(Civilizations.getSelectedProtections().containsKey(player)){
				Protection old = Civilizations.getSelectedProtections().get(player);
				Civilizations.getSelectedProtections().remove(player);
				updateProtectionVisibility(player, old);
			}
			player.sendMessage(messageHeader + ChatColor.RED + "No protection selected.");
		}
	}

	/**
	 * Displays a message to a player when they move from one area to another.
	 * @param fromL
	 * @param toL
	 * @param player
	 */
	public static void displayProtectionStatus(Location fromL, Location toL, Player player) {
		boolean fromProtected = Settlement.isProtected(fromL);
		boolean toProtected = Settlement.isProtected(toL);
		if((fromProtected && toProtected) || (!fromProtected && toProtected)){
			
			Settlement from = Settlement.getAt(fromL);
			Settlement to = Settlement.getAt(toL);
			
			if(!to.equals(from)){
				if(to instanceof Camp){
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getType(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				} else if(to instanceof SmallOutpost){
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getType(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				} else if(to instanceof TownyTown){
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getName(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				}
			}
			
		} else if( fromProtected && !toProtected){
			
			Settlement from = Settlement.getAt(fromL);
			if(from != null){
				if(from instanceof Camp){
					if(from.isMember(player)){
						TitleManagerHook.sendActionBar("Leaving Camp", player, false);
					}
				} else if(from instanceof SmallOutpost){
					if(from.isMember(player)){
						TitleManagerHook.sendActionBar("Leaving Outpost", player, false);
					}
				} else if(from instanceof TownyTown){
					TitleManagerHook.sendActionBar("Leaving " + from.getName(), player, false);
				}
			}
			
		} else {
			
		}
	}
	
	/**
	 * Get the directory the group files are stored in.
	 * @return File
	 */
	public static File getGroupDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Groups");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	
	/**
	 * Get the directory the camp files are stored in.
	 * @return File
	 */
	public static File getCampDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Camps");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the house files are stored in.
	 * @return File
	 */
	public static File getHouseDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Houses");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the fort files are stored in.
	 * @return File
	 */
	public static File getFortDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Forts");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the Small Outpost files are stored in.
	 * @return File
	 */
	public static File getSmallOutpostDirectory() {
		File f = new File(getFortDirectory(), "Small Outposts");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the plot files are stored in.
	 * @return File
	 */
	public static File getPlotDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Plots");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the keep files are stored in.
	 * @return File
	 */
	public static File getKeepDirectory() {
		File f = new File(getPlotDirectory(), "Keeps");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Registers an achievement.
	 * @param name of the achievement
	 * @param description of the achievement
	 */
	public void registerAchievement(String name, String description){
		
	}
	
	/**
	 * Gets the list of processes.
	 * @return List<BukkitRunnable>
	 */
	public static List<BukkitRunnable> getProcesses() {
		return processes;
	}

	/**
	 * Sets the list of processes.
	 * @param processes to set.
	 */
	public static void setProcesses(List<BukkitRunnable> processes) {
		Civilizations.processes = processes;
	}

	/**
	 * Gets a list of all the players currently in Protection Mode.
	 * @return List<Player>
	 */
	public static List<Player> getPlayersInProtectionMode() {
		return currentInstance.playersInProtectionMode;
	}

	/**
	 * Sets the list of the players currently in Protection Mode.
	 * @param playersInProtectionMode
	 */
	public static void setPlayersInProtectionMode(ArrayList<Player> playersInProtectionMode) {
		currentInstance.playersInProtectionMode = playersInProtectionMode;
	}

	/**
	 * No description
	 * @return
	 */
	public static boolean isClearing() {
		return currentInstance.clearing;
	}

	/**
	 * No description
	 * @param clearing
	 */
	public static void setClearing(boolean clearing) {
		currentInstance.clearing = clearing;
	}

	public static HashMap<Player, Location[]> getSelectionPoints() {
		return selectionPoints;
	}

	public static void setSelectionPoints(HashMap<Player, Location[]> selectionPoints) {
		Civilizations.selectionPoints = selectionPoints;
	}
}