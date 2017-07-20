package com.kylantraynor.civilizations.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.Civilizations;

public class CommandStall implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = null;
		if(!(sender instanceof Player)){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This command can only be used by a player.");
			return true;
		}
		player = (Player)sender;
		
		Civilizations.DEBUG("Trying to find Stall.");
		Plot stall = null;
		for(Plot s : CacheManager.getPlotList()){
			if(s.getPlotType() != PlotType.MARKETSTALL) continue;
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
