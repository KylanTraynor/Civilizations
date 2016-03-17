package com.kylantraynor.civilizations.hook.titlemanager;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class TitleManagerHook {
	private static Plugin plugin;

	/**
	 * Tries to load the TitleManager plugin. Returns true if successfully loaded, returns false otherwise.
	 * @param manager
	 * @return boolean
	 */
	public static boolean load(PluginManager manager) {
		if((plugin = manager.getPlugin("TitleManager")) != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the state of the TitleManager hook. Returns true if the plugin is loaded and enabled, returns false otherwise.
	 * @return boolean
	 */
	public static boolean isEnabled(){
		if(plugin != null){
			return plugin.isEnabled();
		} else {
			return false;
		}
	}
	/**
	 * Sends a title message to a player, or a chat message if TitleManager isn't enabled.
	 * @param title
	 * @param subTitle
	 * @param in
	 * @param out
	 * @param stay
	 * @param p
	 */
	public static void sendTitle(String title, String subTitle, int in, int out, int stay, Player p){
		if(TitleManagerHook.isEnabled()){
			TitleObject to = new TitleObject(title, subTitle);
			to.setFadeIn(in);
			to.setFadeOut(out);
			to.setStay(stay);
			to.send(p);
		} else {
			p.sendMessage(title);
			p.sendMessage(subTitle);
		}
	}
	/**
	 * Sends an action bar message to a player or to the server, or a chat message if TitleManager isn't enabled.
	 * @param text
	 * @param p
	 * @param broadcast
	 */
	public static void sendActionBar(String text, Player p, boolean broadcast){
		if(TitleManagerHook.isEnabled()){
			ActionbarTitleObject abo = new ActionbarTitleObject(text);
			if(broadcast){ abo.broadcast(); } else { abo.send(p); }
		} else {
			if(broadcast){
				Bukkit.getServer().broadcastMessage(text);
			} else {
				p.sendMessage(text);
			}
		}
	}
	/**
	 * Sends an action bar message to a player or to the server, or a chat message if TitleManager isn't enabled.
	 * @param fm
	 * @param p
	 * @param broadcast
	 */
	public static void sendActionBar(FancyMessage fm, Player p, boolean broadcast){
		if(TitleManagerHook.isEnabled()){
			ActionbarTitleObject abo = new ActionbarTitleObject(fm.toJSONString());
			if(broadcast){ abo.broadcast(); } else { abo.send(p); }
		} else {
			if(broadcast){
				Bukkit.getServer().broadcastMessage(fm.toJSONString());
			} else {
				fm.send(p);;
			}
		}
	}
}
