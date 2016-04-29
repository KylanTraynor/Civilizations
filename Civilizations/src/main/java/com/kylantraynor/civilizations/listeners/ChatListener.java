package com.kylantraynor.civilizations.listeners;

import mkremins.fanciful.FancyMessage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.House;

public class ChatListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
		if(!Civilizations.useChat) return;
		if(event.isCancelled()) return;
		
		if(event.getPlayer() == null) return;
		
		FancyMessage fm;
		if(event.getPlayer().getCustomName() == null){
			fm = new FancyMessage(""+event.getPlayer().getName());
		} else {
			fm = new FancyMessage(""+event.getPlayer().getCustomName());
		}
		fm.suggest("/msg " + event.getPlayer().getName() + " ");
		if(House.get(event.getPlayer()) != null){
			fm.then(" " + House.get(event.getPlayer()).getName());
			fm.tooltip("\"" + House.get(event.getPlayer()).getWords() + "\"");
			fm.command("/house " + House.get(event.getPlayer()).getName() + " INFO");
		}
		fm.then(": ");
		fm.then(event.getMessage());
		
		for(Player p : event.getRecipients()){
			fm.send(p);
		}
		event.setCancelled(true);
	}
	
}
