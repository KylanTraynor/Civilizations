package com.kylantraynor.civilizations.hook;

import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.hook.citizens.CitizensHook;
import com.kylantraynor.civilizations.hook.draggyrpg.DraggyRPGHook;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.hook.livelyworld.LivelyWorldHook;
import com.kylantraynor.civilizations.hook.lwc.LWCHook;
import com.kylantraynor.civilizations.hook.quickshop.QuickShopListener;
import com.kylantraynor.civilizations.hook.quickshop.QuickshopHook;
import com.kylantraynor.civilizations.hook.titlemanager.TitleManagerHook;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.hook.worldborder.WorldBorderHook;

public class HookManager {
	
	private static CitizensHook citizens = null;
	private static DraggyRPGHook draggyRPG = null;
	private static DynmapHook dynmap = null;
	private static LivelyWorldHook livelyWorld = null;
	private static LWCHook lwc = null;
	private static QuickshopHook quickshop = null;
	private static TitleManagerHook titleManager = null;
	private static TownyHook towny = null;
	private static WorldBorderHook worldBorder = null;
	
	public static void loadHooks(){
		PluginManager pm = Bukkit.getPluginManager();
		Logger log = Civilizations.currentInstance.getLogger();
		
		if(pm.getPlugin("dynmap") != null){
			log.info("Hook to Dynmap: OK");
			dynmap = new DynmapHook();
			dynmap.load(pm);
		} else { log.warning("Hook to Dynmap: NO, " + Civilizations.PLUGIN_NAME + " will not be displayed."); }
		
		if(pm.getPlugin("TitleManager") != null){
			log.info("Hook to TitleManager: OK");
			titleManager = new TitleManagerHook();
			titleManager.load(pm);
		} else { log.warning("Hook to TitleManager: NO"); }
		
		if(pm.getPlugin("LivelyWorld") != null){
			log.info("Hook to LivelyWorld: OK");
			livelyWorld = new LivelyWorldHook();
		} else { log.info("Hook to LivelyWorld: NO");}
		
		if(pm.getPlugin("Towny") != null){
			log.info("Side by side with Towny: OK");
			towny = new TownyHook();
		} else { log.info("Side by side with Towny: NO"); }
			
		if(Economy.load(pm)){ log.info("Economy: OK");
		} else { log.warning("Economy: NO, " + Civilizations.PLUGIN_NAME + " will not be working properly."); }
		
		if(pm.getPlugin("LWC") != null) {
			log.info("LWC: OK"); 
			lwc = new LWCHook();
		} else {log.info("LWC: NO"); }
		
		if(pm.getPlugin("Citizens") != null){
			log.info("Citizens: OK");
			citizens = new CitizensHook();
		} else {
			log.info("Citizens: NO");
		}
		
		if(dynmap != null) dynmap.activateDynmap();
		if(towny != null){
			towny.loadUniqueIds();
			towny.loadTownyTowns();
			pm.registerEvents(towny.getTownyListener(), Civilizations.currentInstance);
		}
		
		if(pm.getPlugin("DraggyData") != null){
			log.info("DraggyData: OK");
		} else {log.severe(ChatColor.RED + "DraggyData is not present! " + Civilizations.PLUGIN_NAME + " will not work!");}
		
		if(pm.getPlugin("DraggyRPG") != null) {
			log.info("DraggyRPG: OK");
			draggyRPG = new DraggyRPGHook();
			draggyRPG.loadLevelCenters();
		} else {log.info("DraggyRPG: NO");}
		
		if(pm.getPlugin("QuickShop") != null){
			log.info("QuickShop: OK");
			quickshop = new QuickshopHook();
			pm.registerEvents(new QuickShopListener(), Civilizations.currentInstance);
		} else { log.info("Quickshop: NO");}
	}

	public static void unloadHooks(){
	    if(towny != null){
	        towny.saveUniqueIds();
        }
    }
	
	public static CitizensHook getCitizens() {
		return citizens;
	}
	public static DraggyRPGHook getDraggyRPG() {
		return draggyRPG;
	}
	public static DynmapHook getDynmap() {
		return dynmap;
	}
	public static LivelyWorldHook getLivelyWorld() {
		return livelyWorld;
	}
	public static LWCHook getLWC() {
		return lwc;
	}
	public static QuickshopHook getQuickshop() {
		return quickshop;
	}
	public static TitleManagerHook getTitleManager() {
		return titleManager;
	}
	public static TownyHook getTowny() {
		return towny;
	}
	public static WorldBorderHook getWorldBorder() {
		return worldBorder;
	}
	
}
