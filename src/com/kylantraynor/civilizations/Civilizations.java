package com.kylantraynor.civilizations;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.kylantraynor.civilizations.permissions.PermissionType;
import com.kylantraynor.civilizations.plots.Plot;
import com.kylantraynor.civilizations.questions.GroupQuestion;
import com.kylantraynor.civilizations.questions.QuestionsHandler;
import com.kylantraynor.civilizations.towns.TownyTown;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Civilizations extends JavaPlugin implements Listener{
	
	public static final String MC_SERVER_VERSION = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	public static final String PLUGIN_NAME = "Civilizations";
	public static Civilizations currentInstance;
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GOLD + "CIVILIZATIONS" + ChatColor.GOLD + "] ";

	private Plugin dynmap;
	private boolean hasDynmap;
	private static MarkerAPI markerAPI;
	private boolean reload;
	private boolean clearing = false;
	private DynmapAPI dynmapAPI;
	private ArrayList<Player> playersInProtectionMode = new ArrayList<Player>();
	private Plugin titleManager;
	private boolean hasTitleManager;
	private boolean hasTowny;
	private Plugin towny;
	static private HashMap<Player, Protection> selectedProtections = new HashMap<Player, Protection>();
	private static MarkerSet campMarkerSet;
	static private FileConfiguration config;
	
	static HashMap<Player, Protection> getSelectedProtections(){
		return selectedProtections;
	}
	
	static List<BukkitRunnable> processes = new ArrayList<BukkitRunnable>();
	static HashMap<String, Marker> markerList = new HashMap<String, Marker>();
	
	public void log(Level level, String message){
		getLogger().log(level, message);
	}
	
	@Override
	public void onEnable(){
		currentInstance = this;
		saveDefaultConfig();
		config = getConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		
		registerAchievement("Setting up Camp!","Create a camp.");
		
		loadGroups();
		loadCamps();
		if((dynmap = pm.getPlugin("dynmap")) != null){
			hasDynmap = true;
			dynmapAPI = (DynmapAPI)dynmap;
			log(Level.INFO, "Hook to Dynmap: OK");
			if(dynmap.isEnabled()){
				activateDynmap();
			}
		} else {
			log(Level.WARNING, "Hook to Dynmap: NO, " + PLUGIN_NAME + " will not be displayed.");
		}
		if((titleManager = pm.getPlugin("TitleManager")) != null){
			hasTitleManager = true;
			log(Level.INFO, "Hook to TitleManager: OK");
		} else {
			log(Level.WARNING, "Hook to Titlemanager: NO");
		}
		
		if((towny = pm.getPlugin("Towny")) != null){
			hasTowny = true;
			log(Level.INFO, "Side by side with Towny: OK");
			loadTownyTowns();
		} else {
			log(Level.WARNING, "Side by side with Towny: NO");
		}
		
		BukkitRunnable br = new BukkitRunnable(){

			@Override
			public void run() {
				for(Group g : Cache.getGroupList()){
					g.update();
				}
			}
			
		};
		br.runTaskTimer(this, 0, 1000);
		BukkitRunnable pr = new BukkitRunnable(){

			@Override
			public void run() {
				BukkitRunnable[] prcs = Civilizations.processes.toArray(new BukkitRunnable[Civilizations.processes.size()]);
				for(BukkitRunnable r : prcs){
					if(Civilizations.processes.contains(r)){
						Civilizations.processes.remove(r);
					}
					r.runTask(Civilizations.currentInstance);
				}
			}
			
		};
		pr.runTaskTimerAsynchronously(this, 10, 40);
	}
	
	private void loadTownyTowns() {
		if(hasTowny){
			List<Town> tl = TownyUniverse.getDataSource().getTowns();
			for(Town t : tl){
				log(Level.INFO, "Loading " + t.getName() + ".");
				try {
					new TownyTown(t);
				} catch (TownyException e) {
					log(Level.WARNING, t.getName() + " couldn't be loaded.");
				}
			}
		}
	}

	protected void updateProtectionVisibility(Player p, Protection prot) {
		if(playersInProtectionMode.contains(p)){
			if(Civilizations.getSelectedProtections().get(p).equals(prot)){
				prot.highlight(p);
			} else {
				prot.hide(p);
			}
		} else {
			prot.hide(p);
		}
	}

	private void loadGroups() {
		File groupDir = getCampDirectory();
		if(groupDir.exists()){
			for(File f : groupDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "Couldn't find file " + f.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "Invalid file configuration.");
				}
				Group.load(yaml);
			}
		}
	}

	private void loadCamps() {
		File campDir = getCampDirectory();
		if(campDir.exists()){
			for(File f : campDir.listFiles()){
				if(!f.getName().split("\\.")[1].equals("yml")) continue;
				if(clearing ){
					log(Level.INFO, "Cleared file " + f.getName());
					f.delete();
					continue;
				}
				YamlConfiguration yaml = new YamlConfiguration();
				try {
					yaml.load(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "Couldn't find file " + f.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "File " + f.getName() + " is in use in another application.");
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					log(Level.WARNING, "Invalid file configuration.");
				}
				f.delete();
				Camp.load(yaml);
			}
		}
	}

	private void activateDynmap() {
		try{
			markerAPI = dynmapAPI.getMarkerAPI();
			if (markerAPI == null) {
				log(Level.SEVERE, "Error loading dynmap marker API!");
				return;
		    }
			if (this.reload){
				reloadConfig();
				if (campMarkerSet != null){
					campMarkerSet.deleteMarkerSet();
					campMarkerSet = null;
				}
		    } else {
		    	this.reload = true;
		    }
			loadCampMarkerSet();
		} catch (Exception e) {
			log(Level.SEVERE, "Something went wrong activating Dynmap for Civilizations. Is it up to date?");
			hasDynmap = false;
		}
	}

	private void loadCampMarkerSet() {
		if(!hasDynmap) return;
		campMarkerSet = markerAPI.getMarkerSet("civilizations.markerset.camps");
		if (campMarkerSet == null) {
			campMarkerSet = markerAPI.createMarkerSet("civilizations.markerset.camps", getConfig().getString("Dynmap.Layer.Name", "Camps"), null, false);
		} else {
			campMarkerSet.setMarkerSetLabel(getConfig().getString("Dynmap.Layer.Camp.Name", "Camps"));
		}
		if (campMarkerSet == null){
			log(Level.SEVERE, "Error creating marker set");
			return;
		}
		int minzoom = getConfig().getInt("Dynmap.Layer.Camp.MinZoom", 5);
		if (minzoom > 0) {
		   campMarkerSet.setMinZoom(minzoom);
		}
		campMarkerSet.setLayerPriority(getConfig().getInt("Dynmap.Layer.Camp.LayerPrio", 10));
	    campMarkerSet.setHideByDefault(getConfig().getBoolean("Dynmap.Layer.Camp.HideByDefault", false));
	}

	@Override
	public void onDisable(){
		markerList.clear();
		markerList = null;
		campMarkerSet = null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		switch (cmd.getName().toUpperCase()){
		case "CIVILIZATIONSANSWER":
			if(!(sender instanceof Player)) return false;
			if(args.length > 1 && Group.get(Integer.parseInt(args[0])) != null){
				Group g = Group.get(Integer.parseInt(args[0]));
				((GroupQuestion) QuestionsHandler.getQuestion(g)).initiateAnswerRoutine((Player) sender, args);
			} else {
				QuestionsHandler.getQuestion(sender).initiateAnswerRoutine(args);
			}
			break;
		case "SELECTION":
			if(!(sender instanceof Player)) return false;
			if(args.length >= 1){
				switch(args[0].toUpperCase()){
				case "MEMBERS":
					if(selectedProtections.containsKey(sender)){
						selectedProtections.get(sender).getGroup().getInteractiveMembersList().send(sender);
					} else {
						sender.sendMessage(ChatColor.RED + "You have no protection selected.");
					}
					break;
				}
			}
			break;
		case "CIVILIZATIONS":
			if(args.length > 0 && sender.isOp()){
				switch(args[0].toUpperCase()){
				case "TOGGLE":
					if(args.length > 1){
						switch(args[1].toUpperCase()){
						case "CLEARING":
							clearing = !clearing;
							break;
						case "PROTECTIONMODE":
							if(!(sender instanceof Player)) return false;
							if(playersInProtectionMode.contains((Player) sender)){
								playersInProtectionMode.remove((Player) sender);
								sender.sendMessage(messageHeader + "Protection mode turned off.");
							} else {
								playersInProtectionMode.add((Player) sender);
								sender.sendMessage(messageHeader + "Protection mode turned on.");
							}
						}
					}
				}
			}
			break;
		case "CAMP":
			Camp.onCommand(sender, cmd, label, args);
			break;
		case "GROUP":
			Group.onCommand(sender, cmd, label, args);
			break;
		case "TOWNYTOWN":
			TownyTown.onCommand(sender, cmd, label, args);
			break;
		}
		
		return false;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement != null){
			if(settlement.getType() == Settlement.Type.TOWNY) return;
			if(!settlement.hasPermission(PermissionType.BREAK, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here.");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement != null){
			if(settlement.getType() == Settlement.Type.TOWNY) return;
			if(!settlement.hasPermission(PermissionType.PLACE, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks here.");
			}
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event){
		Settlement settlement = Settlement.getAt(event.getBlock().getLocation());
		if(settlement == null) return;
		if(settlement.getType() == Settlement.Type.TOWNY) return;
		switch(event.getCause()){
		case SPREAD:
			if(!settlement.hasPermission(PermissionType.FIRESPREAD, event.getBlock(), null)){
				event.setCancelled(true);
			}
			break;
		case FLINT_AND_STEEL:
			if(!settlement.hasPermission(PermissionType.FIRE, event.getBlock(), event.getPlayer())){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can't start a fire here.");
			}
			break;
		default:
			if(!settlement.hasPermission(PermissionType.FIRE, event.getBlock(), null)){
				event.setCancelled(true);
			}
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntitySpawn(EntitySpawnEvent event){
		Settlement settlement = Settlement.getAt(event.getLocation());
		if(settlement == null) return;
		if(settlement.getType() == Settlement.Type.TOWNY) return;
		if(event.getEntity() instanceof LivingEntity){
			LivingEntity entity = (LivingEntity) event.getEntity();
			switch(entity.getType()){
			case ZOMBIE: case SKELETON: case CREEPER:
				if(!settlement.hasPermission(PermissionType.MOBSPAWNING, event.getLocation().getBlock(), null)){
					event.setCancelled(true);
				}
				break;
			default:
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if(item != null && item.getType() == Material.STICK){
			if(!playersInProtectionMode.contains(event.getPlayer())){
				playersInProtectionMode.add(event.getPlayer());
				/*for(Settlement s : Settlement.getSettlementList()){
					updateProtectionVisibility(event.getPlayer(), s.getProtection());
					for(Plot p : s.getPlots()){
						updateProtectionVisibility(event.getPlayer(), p.getProtection());
					}
				}*/
			}
		} else {
			if(playersInProtectionMode.contains(event.getPlayer())){
				playersInProtectionMode.remove(event.getPlayer());
				/*for(Settlement s : Settlement.getSettlementList()){
					updateProtectionVisibility(event.getPlayer(), s.getProtection());
					for(Plot p : s.getPlots()){
						updateProtectionVisibility(event.getPlayer(), p.getProtection());
					}
				}*/
			}
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		if(event.getPlayer() != null){
			if(playersInProtectionMode.contains(event.getPlayer())){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer() != null){
			if(playersInProtectionMode.contains(event.getPlayer())){
				selectTargetProtection(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		displayProtectionStatus(event.getFrom(), event.getTo(), event.getPlayer());
	}

	private void selectTargetProtection(Player player) {
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
			if(s.getType() == Settlement.Type.TOWNY){
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

	private void displayProtectionStatus(Location fromL, Location toL, Player player) {
		boolean fromProtected = Settlement.isProtected(fromL);
		boolean toProtected = Settlement.isProtected(toL);
		if((fromProtected && toProtected) || (!fromProtected && toProtected)){
			
			Settlement from = Settlement.getAt(fromL);
			Settlement to = Settlement.getAt(toL);
			if(!to.equals(from)){
				
				switch(to.getType()){
				case CAMP:
					sendTitle("", ChatColor.GRAY + "Camp", 10, 40, 10, player);
					if(!to.isMember(player)){
						sendActionBar("Protected Area", player, false);
					}
				default:
				}
			}
			
		} else if( fromProtected && !toProtected){
			
			Settlement from = Settlement.getAt(fromL);
			if(from != null){
				
				switch(from.getType()){
				case CAMP:
					if(from.isMember(player)){
						sendActionBar("Leaving Camp", player, false);
					}
				default:
				}
			}
			
		} else {
			
		}
	}

	public static void updateMap(Settlement s){
		if(!Civilizations.currentInstance.hasDynmap) return;
		if(s instanceof Camp){
			Camp c = (Camp) s;
			updateMapCamp(c);
		}
	}
	
	public static void updateMapCamp(Camp c){
		String id = "" + c.getLocation().getBlockX() + "_" +
				c.getLocation().getBlockY() + "_" +
				c.getLocation().getBlockZ() + "_camp";
		String campMarker = c.getIcon();
		MarkerIcon campIcon = null;
	    if (campMarker != null)
	    {
	    	campIcon = markerAPI.getMarkerIcon(campMarker);
	        if (campIcon == null)
	        {
	          currentInstance.log(Level.INFO, "Invalid CampIcon: " + campMarker);
	          campIcon = markerAPI.getMarkerIcon("blueicon");
	        }
	    }
	    if(campIcon != null){
	    	Marker camp = markerList.remove(id);
	    	if (camp == null){
	    		camp = campMarkerSet.createMarker(id, "Camp", c.getLocation().getWorld().getName(), c.getLocation().getBlockX(), c.getLocation().getBlockY(), c.getLocation().getBlockZ(), campIcon, false);
	    	} else {
	    		camp.setLocation(c.getLocation().getWorld().getName(),
	    				c.getLocation().getBlockX(),
	    				c.getLocation().getBlockY(),
	    				c.getLocation().getBlockZ());
	            camp.setLabel("Camp");
	            camp.setMarkerIcon(campIcon);
	    	}
	    	StringBuilder sb = new StringBuilder();
	    	for(UUID uid : c.getMembers()){
	    		sb.append(Bukkit.getServer().getOfflinePlayer(uid).getUniqueId() + "\n");
	    	}
	    	camp.setDescription("Expire in " + ChronoUnit.HOURS.between(Instant.now(), c.getExpireOn()) + " hours."
	    			+ "\nMembers: " + sb.toString());
	    	markerList.put(id, camp);
	    }
	}
	
	public static File getGroupDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Groups");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	public static File getCampDirectory(){
		File f = new File(currentInstance.getDataFolder(), "Camps");
		if(f.exists()){
			return f;
		} else {
			f.mkdir();
			return f;
		}
	}
	
	public void sendTitle(String title, String subTitle, int in, int out, int stay, Player p){
		if(hasTitleManager){
			TitleObject to = new TitleObject(title, subTitle);
			to.setFadeIn(in);
			to.setFadeOut(out);
			to.setStay(stay);
			to.send(p);
		} else if(MC_SERVER_VERSION.equals("v1_8_R2")){
			com.kylantraynor.civilizations.v1_8_R2.TitleDisplay.send(title, subTitle, in, out, stay, p);
		} else if (MC_SERVER_VERSION.equals("v1_8_R3")){
			com.kylantraynor.civilizations.v1_8_R3.TitleDisplay.send(title, subTitle, in, out, stay, p);
		} else {
			this.log(Level.SEVERE, "Version of server (" + MC_SERVER_VERSION + ") isn't supported. Try getting TitleManager to fix this issue.");
		}
	}
	
	public void sendActionBar(String text, Player p, boolean broadcast){
		if(hasTitleManager){
			ActionbarTitleObject abo = new ActionbarTitleObject(text);
			if(broadcast){ abo.broadcast(); } else { abo.send(p); }
		} else if(MC_SERVER_VERSION.equals("v1_8_R2")){
			com.kylantraynor.civilizations.v1_8_R2.ActionBar bar = new com.kylantraynor.civilizations.v1_8_R2.ActionBar(text);
			if(broadcast){bar.sendToAll();} else {bar.sendToPlayer(p);}
		} else if (MC_SERVER_VERSION.equals("v1_8_R3")){
			com.kylantraynor.civilizations.v1_8_R3.ActionBar bar = new com.kylantraynor.civilizations.v1_8_R3.ActionBar(text);
			if(broadcast){bar.sendToAll();} else {bar.sendToPlayer(p);}
		} else {
			this.log(Level.SEVERE, "Version of server (" + MC_SERVER_VERSION + ") isn't supported. Try getting TitleManager to fix this issue.");
		}
	}
	
	public void sendActionBar(FancyMessage fm, Player p, boolean broadcast){
		if(hasTitleManager){
			ActionbarTitleObject abo = new ActionbarTitleObject(fm.toJSONString());
			if(broadcast){ abo.broadcast(); } else { abo.send(p); }
		} else if(MC_SERVER_VERSION.equals("v1_8_R2")){
			com.kylantraynor.civilizations.v1_8_R2.ActionBar bar = new com.kylantraynor.civilizations.v1_8_R2.ActionBar(fm);
			if(broadcast){bar.sendToAll();} else {bar.sendToPlayer(p);}
		} else if (MC_SERVER_VERSION.equals("v1_8_R3")){
			com.kylantraynor.civilizations.v1_8_R3.ActionBar bar = new com.kylantraynor.civilizations.v1_8_R3.ActionBar(fm);
			if(broadcast){bar.sendToAll();} else {bar.sendToPlayer(p);}
		} else if (MC_SERVER_VERSION.equals("v1_9_R1")){
			com.kylantraynor.civilizations.v1_9_R1.ActionBar bar = new com.kylantraynor.civilizations.v1_9_R1.ActionBar(fm);
			if(broadcast){bar.sendToAll();} else {bar.sendToPlayer(p);}
		} else {
			this.log(Level.SEVERE, "Version of server (" + MC_SERVER_VERSION + ") isn't supported. Try getting TitleManager to fix this issue.");
		}
	}
	
	public void registerAchievement(String name, String Description){
		
	}
}
