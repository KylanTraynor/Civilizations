package com.kylantraynor.civilizations.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.selection.SelectionManager;

public class CommandBlueprint implements CommandExecutor{
	
	private String messageHeader = "" + ChatColor.GOLD + "[" + ChatColor.BLUE + "Blueprint" + ChatColor.GOLD + "] ";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if(!(sender instanceof Player)){
			sender.sendMessage(messageHeader + ChatColor.RED + "This command must be used by a player.");
		}
		Player player = (Player) sender;
		
		if(!SelectionManager.hasSelection(player)){
			player.sendMessage(messageHeader + ChatColor.RED + "You need to have a selection.");
		}
		
		player.sendMessage(messageHeader + ChatColor.RED + "Blueprints are not yet activated.");
		return false;
	}

}
