package com.kylantraynor.civilizations.hook.livelyworld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.livelyworld.deterioration.DeteriorationCause;
import com.kylantraynor.livelyworld.events.BlockDeteriorateEvent;

public class LivelyWorldListener implements Listener{
	
	@EventHandler
	public void onBlockDeteriorate(BlockDeteriorateEvent event){
		if(event.getCause() == DeteriorationCause.Erosion){
			if(ProtectionManager.hasPermissionAt(PermissionType.ERODE, event.getBlock().getLocation(), null, true)){
				
			} else {
				event.setCancelled(true);
			}
		}
	}

}
