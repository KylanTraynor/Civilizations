package com.kylantraynor.civilizations.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event){
		if(event.isAsynchronous()){
			
		} else {
			
		}
	}
	
}