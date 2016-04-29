package com.kylantraynor.civilizations.listeners;

import mkremins.fanciful.FancyMessage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kylantraynor.civilizations.groups.House;

public class ChatListener implements Listener{
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
		
		if(event.isCancelled()) return;
		
		String format = "%s%house: %s";
		
		if(event.getPlayer() != null){
			House h = House.get(event.getPlayer());
			if(h != null){
				format = format.replace("%house", " " + h.getName());
			} else {
				format = format.replace("%house", "");
			}
		}
		
		FancyMessage fm = new FancyMessage("%s");
		if(House.get(event.getPlayer()) != null){
			fm.then(" " + House.get(event.getPlayer()).getName());
			fm.tooltip("Test");
		}
		fm.then(": ");
		fm.then("%s");
		
		for(Player p : event.getRecipients()){
			fm.send(p);
		}
		event.setCancelled(true);
	}
	
}
