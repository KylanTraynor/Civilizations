package com.kylantraynor.civilizations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.jetty.server.Server;

import com.kylantraynor.civilizations.commands.CommandAnswer;
import com.kylantraynor.civilizations.commands.CommandBlueprint;
import com.kylantraynor.civilizations.commands.CommandCamp;
import com.kylantraynor.civilizations.commands.CommandCivilizations;
import com.kylantraynor.civilizations.commands.CommandGroup;
import com.kylantraynor.civilizations.commands.CommandHouse;
import com.kylantraynor.civilizations.commands.CommandPlot;
import com.kylantraynor.civilizations.commands.CommandRegion;
import com.kylantraynor.civilizations.commands.CommandSelection;
import com.kylantraynor.civilizations.commands.CommandStall;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.SmallOutpost;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.hook.draggyrpg.DraggyRPGHook;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.hook.lwc.LWCHook;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.hook.towny.CommandTownyTown;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.hook.towny.TownyListener;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.kylantraynor.civilizations.listeners.ChatListener;
import com.kylantraynor.civilizations.listeners.CivilizationsListener;
import com.kylantraynor.civilizations.listeners.MenuListener;
import com.kylantraynor.civilizations.listeners.ProtectionListener;
import com.kylantraynor.civilizations.listeners.TerritoryListener;
import com.kylantraynor.civilizations.listeners.VehiclesListener;
import com.kylantraynor.civilizations.listeners.WebListener;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.settings.CivilizationsSettings;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.util.MaterialAndData;

import fr.rhaz.webservers.WebServers.API;

public class Civilizations extends JavaPlugin{
	
	/**
	 * Plugin Constants
	 */
	public static final String MC_SERVER_VERSION = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	public static final String PLUGIN_NAME = "Civilizations";
	public static final int SETTLEMENT_MERGE_RADIUS = 25;
	
	/**
	 * Currently running instance of Civilizations
	 */
	public static Civilizations currentInstance;
	
	/**
	 * Header displayed in chat
	 */
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GOLD + ChatColor.BOLD + "Civilizations" + ChatColor.GOLD + "] ";

	private boolean clearBuildProjectsOnRestart = true;
	
	private boolean clearing = false;
	private boolean DEBUG = false;
	private ArrayList<Player> playersInProtectionMode = new ArrayList<Player>();
	static private HashMap<Player, Protection> selectedProtections = new HashMap<Player, Protection>();
	static private CivilizationsSettings settings;
	
	public static HashMap<Player, Protection> getSelectedProtections(){ return selectedProtections; }
	
	private static List<BukkitRunnable> processes = new ArrayList<BukkitRunnable>();
	/*
	 * Listeners
	 */
	private static CivilizationsListener mainListener = new CivilizationsListener();
	private static MenuListener menuListener = new MenuListener();
	private static TerritoryListener territoryListener = new TerritoryListener();
	private static ProtectionListener protectionListener = new ProtectionListener();
	private static WebListener webListener = new WebListener();
	private static ChatListener chatListener = new ChatListener();
	private static VehiclesListener vehiclesListener = new VehiclesListener();
	private static TownyListener townyListener = new TownyListener();
	/*
	 * InfluenceMaps
	 */
	private static Map<World, InfluenceMap> influenceMaps = new HashMap<World, InfluenceMap>();
	
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
	
	private static Server webServer;
	public static boolean useChat = false;
	
	public static Server getWebServer(){
		return webServer;
	}
	/**
	 * Sends a message to the console with the specified level.
	 * @param level of the message.
	 * @param message to send.
	 * @see Level
	 */
	public static void log(String level, String message){
		if(level != null){
			Level lvl = Level.parse(level);
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
		
		File f = new File(this.getDataFolder(), "config.yml");
		settings = new CivilizationsSettings();
		try {
			settings.load(f);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		MaterialAndData.reloadFromConfig(settings);
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(getMainListener(), this);
		pm.registerEvents(getMenuListener(), this);
		pm.registerEvents(getTerritoryListener(), this);
		pm.registerEvents(getProtectionListener(), this);
		pm.registerEvents(getChatListener(), this);
		pm.registerEvents(getVehiclesListener(), this);
		
		registerAchievements();
		
		GroupManager.loadAll();
		
		loadHooks(pm);
		
		GroupManager.ensurePlotsAreLinked();
		
		loadInfluenceMaps();
		
		startGroupUpdater(20L * 60 * 5);
		startProtectionUpdater(40L);
		startEconomyUpdater(20L * 60);
		startBuilderUpdater(20L * 2);
		
		initManagers();
		
		setupCommands();
		
		/*int port = 8120;
		try {
			startWebServer(port);
			log("INFO", "Successfully started webserver on port " + port);
		} catch (Exception e) {
			log("WARNING", "Could not start webserver on port " + port + ". This port is probably already in use.");
			e.printStackTrace();
		}*/
	}

	private void loadInfluenceMaps() {
		List<String> enabledWorlds = getConfig().getStringList("EnabledWorlds");
		for(World w : Bukkit.getServer().getWorlds()){
			if(enabledWorlds.contains(w.getName()))
				influenceMaps.put(w, new InfluenceMap(w));
		}
		
		for(InfluenceMap map : influenceMaps.values()){
			log("INFO", "Generating influence map for " + map.getWorld().getName() + ".");
			map.generateFull();
			log("INFO", "Map was generated with " + map.getCells().length + " cells.");
		}
	}

	private void initManagers() {
		LockManager.init();
		SelectionManager.init();
		CacheManager.init();
	}
	
	private void freesManagers(){
		SelectionManager.free();
	}

	private void registerAchievements() {
		/*Achievement createCamp = new Achievement("create_camp", "Setting up Camp", null, new ArrayList<String>());
		createCamp.getDescription().add("Create a camp.");
		AchievementManager.registerAchievement(createCamp);*/
	}

	private void startWebServer(int port) throws Exception {
		
		createServerViews();
		
		webServer = API.createServer(port, "Civilizations", "");
		webServer.start();
		getServer().getPluginManager().registerEvents(getWebListener(), this);
	}

	private void createServerViews() {
		
	}

	private Listener getMenuListener() {
		return Civilizations.menuListener;
	}

	/**
	 * Loads all the hooks allowing the interaction with other plugins.
	 * @param pm PluginManager
	 */
	private void loadHooks(PluginManager pm) {
		if(DynmapHook.load(pm)){ log("INFO", "Hook to Dynmap: OK");
		} else { log("WARNING", "Hook to Dynmap: NO, " + PLUGIN_NAME + " will not be displayed."); }
		
		if(TitleManagerHook.load(pm)){ log("INFO", "Hook to TitleManager: OK");
		} else { log("WARNING", "Hook to Titlemanager: NO"); }
		
		if(TownyHook.isActive()){ log("INFO", "Side by side with Towny: OK");
		} else { log("INFO", "Side by side with Towny: NO"); }
			
		if(Economy.load(pm)){ log("INFO", "Economy: OK");
		} else { log("WARNING", "Economy: NO, " + PLUGIN_NAME + " will not be working properly."); }
		
		if(LWCHook.isActive()) {log("INFO", "LWC: OK"); } else {log("INFO", "LWC: NO"); }
		
		if(DynmapHook.isEnabled()) DynmapHook.activateDynmap();
		if(TownyHook.isActive()){
			TownyHook.loadTownyTowns();
			pm.registerEvents(getTownyListener(), this);
		}
		
		if(DraggyRPGHook.isActive()) {
			log("INFO", "DraggyRPG: OK");
			DraggyRPGHook.loadLevelCenters();
		} else {log("INFO", "DraggyRPG: NO");}
	}
	
	/**
	 * Setups all the commands for Civilizations.
	 */
	private void setupCommands() {
		this.getCommand("Civilizations").setExecutor(new CommandCivilizations());
		this.getCommand("CivilizationsAnswer").setExecutor(new CommandAnswer());
		this.getCommand("Blueprint").setExecutor(new CommandBlueprint());
		
		this.getCommand("Group").setExecutor(new CommandGroup());
		this.getCommand("House").setExecutor(new CommandHouse());
		this.getCommand("Camp").setExecutor(new CommandCamp());
		
		this.getCommand("Region").setExecutor(new CommandRegion());
		
		this.getCommand("Selection").setExecutor(new CommandSelection());
		
		this.getCommand("Plot").setExecutor(new CommandPlot());
		this.getCommand("Stall").setExecutor(new CommandStall());
		
		if(TownyHook.isActive()){
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
				GroupManager.updateAllGroups();
			}
			
		};
		br.runTaskTimer(this, (long) (Math.random() * 20), interval);
	}
	
	/**
	 * Starts the process of updating the economy.
	 * @param interval in ticks between updates.
	 */
	private void startEconomyUpdater(long interval) {
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				if(settings.hasChanged()){
					try {
						settings.save(new File(Civilizations.currentInstance.getDataFolder(), "config.yml"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(Instant.now().isAfter(settings.getTaxationDate())){
					settings.setTaxationDate(Instant.now().plus(1, ChronoUnit.DAYS));
					GroupManager.updateForEconomy();
				}
			}
			
		};
		br.runTaskTimer(this, (long) (Math.random() * 20), interval);
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
		pr.runTaskTimerAsynchronously(this, (long) (Math.random() * interval), interval);
	}
	
	/**
	 * Starts process updating the builders.
	 * @param interval in ticks between updates.
	 */
	private void startBuilderUpdater(long interval) {
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				GroupManager.updateAllBuilders();
			}
			
		};
		br.runTaskTimer(this, (long) (Math.random() * 20), interval);
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

	@Override
	public void onDisable(){
		if(clearBuildProjectsOnRestart)
			GroupManager.cancelAllBuilds();
		
		GroupManager.updateAllGroups();
		if(DynmapHook.isEnabled()){
			DynmapHook.disable();
		}
		freesManagers();
		MaterialAndData.saveToConfig(getConfig());
		saveConfig();
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
			//Try getting a plot
			Plot plot = null;
			Protection newProt = null;
			for(Plot p : CacheManager.getPlotList()){
				if(p.protects(l)){
					plot = p;
					newProt = p.getProtection();
				}
			}
			Settlement s = null;
			if(newProt == null){
				s = Settlement.getAt(l);
				if(s != null){
					newProt = s.getProtection();
				}
			} else {
				s = plot.getSettlement();
			}
			Protection old = Civilizations.getSelectedProtections().get(player);
			if(newProt.equals(old)){
				player.sendMessage(messageHeader + ChatColor.RED + "Protection already selected.");
				return;
			}
			Civilizations.getSelectedProtections().put(player, newProt);
			String plotName = " Settlement";
			if(plot != null) plotName = " " + plot.getName();
			player.sendMessage(messageHeader + ChatColor.GREEN + "Protection selected: " + (s != null ? s.getName() : "") + plotName + ".");
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
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getName(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				} else if(to instanceof SmallOutpost){
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getName(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				} else if(to instanceof TownyTown){
					TitleManagerHook.sendTitle("", ChatColor.GRAY + to.getName(), 10, 40, 10, player);
					if(!to.isMember(player)){
						TitleManagerHook.sendActionBar("Protected Area", player, false);
					}
				}
			} else {
				Plot p = null;
				for(Plot plot : CacheManager.getPlotList()){
					if(plot.protects(toL)) p = plot;
				}
				if(p == null) return;
				if(!p.protects(fromL)){
					TitleManagerHook.sendActionBar(p.getName(), player, false);
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
	 * Get the directory the settlement files are stored in.
	 * @return File
	 */
	public static File getSettlementDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Settlements");
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
	 * Get the directory the keep files are stored in.
	 * @return File
	 */
	public static File getHousePlotDirectory() {
		File f = new File(getPlotDirectory(), "Houses");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	/**
	 * Get the directory the stall files are stored in.
	 * @return File
	 */
	public static File getMarketStallDirectory() {
		File f = new File(getPlotDirectory(), "Stalls");
		if(!f.exists())
			f.mkdir();
		return f;
	}
	
	/**
	 * Get the directory in which the nation files are stored.
	 * @return File
	 */
	public static File getNationDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Nations");
		if(!f.exists())
			f.mkdir();
		return f;
	}
	
	public static File getPlayerDataDirectory() {
		File f = new File(currentInstance.getDataFolder(), "PlayerData");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	
	public static File getBlueprintDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Blueprints");
		if(!f.exists())
			f.mkdir();
		return f;
	}
	
	public static File getWarehousesDirectory() {
		File f = new File(getPlotDirectory(), "Warehouses");
		if(!f.exists())
			f.mkdir();
		return f;
	}
	
	public static File getWebDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Web");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	
	public static File getPrivateWebDirectory(){
		File f = new File(getWebDirectory(), "WEB-INF");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
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

	public static TerritoryListener getTerritoryListener() {
		return territoryListener;
	}

	public static ProtectionListener getProtectionListener() {
		return protectionListener;
	}
	
	public static TownyListener getTownyListener(){
		return townyListener;
	}

	public boolean isDEBUG() {
		return DEBUG;
	}

	public void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}
	
	public static void DEBUG(String message){
		if(!currentInstance.DEBUG) return;
		log("INFO", message);
	}

	public static WebListener getWebListener() {
		return webListener;
	}

	public static void setWebListener(WebListener webListener) {
		Civilizations.webListener = webListener;
	}

	public static ChatListener getChatListener() {
		return chatListener;
	}
	
	public static Listener getVehiclesListener() {
		return vehiclesListener;
	}

	public static void callEvent(Event event) {
		currentInstance.getServer().getPluginManager().callEvent(event);
	}
	
	public static InfluenceMap getInfluenceMap(World w){
		return influenceMaps.get(w);
	}

	public static File getBuilderDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Builders");
		if(!f.exists())
			f.mkdir();
		return f;
	}

	public static File getTownyTownsDirectory() {
		File f = new File(currentInstance.getDataFolder(), "Towny");
		if(!f.exists())
			f.mkdir();
		return f;
	}
}