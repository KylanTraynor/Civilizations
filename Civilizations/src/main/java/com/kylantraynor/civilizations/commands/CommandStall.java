package com.kylantraynor.civilizations.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;

public class CommandStall extends CommandGroup{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = null;
		if(!(sender instanceof Player)){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This command can only be used by a player.");
			return true;
		}
		player = (Player)sender;
		
		
		MarketStall stall = null;
		for(MarketStall s : Cache.getMarketstallList()){
			if(s.protects(player.getLocation())){
				stall = s;
				break;
			}
		}
		if(stall == null){
			player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "There is no stall here.");
			return true;
		}
		
		
		if(args.length == 0){
			stall.getInteractiveInfoPanel(player).send(player);
			return true;
		} else {
			player.sendMessage(stall.getChatHeader() + "The more advanced commands are disabled for now. Just use " + ChatColor.GOLD + "/stall");
			return true;
		}
	}
	
}
