package com.kylantraynor.civilizations.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.managers.SelectionManager;

public class CommandSelection implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length == 0){args = new String[]{"HELP"};}
		if(args.length >= 1){
			switch(args[0].toUpperCase()){
			case "HELP":
				sender.sendMessage(ChatTools.formatTitle("Selection", ChatColor.GRAY));
				sender.sendMessage(ChatColor.GOLD + "/Selection Start Hull" + ChatColor.GRAY + "  Start a point cloud selection.");
				sender.sendMessage(ChatColor.GOLD + "/Selection Stop Hull" + ChatColor.GRAY + "  Stop a point cloud selection.");
				return true;
			case "START":
				if(args.length >= 2){
					switch(args[1].toUpperCase()){
					case "HULL":
						SelectionManager.startHullSelection((Player)sender);
						sender.sendMessage(Civilizations.messageHeader + ChatColor.GOLD + "Point Cloud selection started.");
					}
				}
				return true;
			case "STOP":
				if(args.length >= 2){
					switch(args[1].toUpperCase()){
					case "HULL":
						SelectionManager.stopHullSelection((Player)sender);
						sender.sendMessage(Civilizations.messageHeader + ChatColor.GOLD + "Two-Corners selection started.");
					}
				}
				return true;
			case "INFO":
				if(Civilizations.getSelectedProtections().containsKey(sender)){
					Civilizations.getSelectedProtections().get(sender).getGroup().getInteractiveInfoPanel((Player) sender).send(sender);
				} else {
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no protection selected.");
				}
				return true;
			case "MEMBERS":
				if(Civilizations.getSelectedProtections().containsKey(sender)){
					Civilizations.getSelectedProtections().get(sender).getGroup().getInteractiveMembersList().send(sender);
				} else {
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no protection selected.");
				}
				return true;
			case "PERMISSIONS":
				if(Civilizations.getSelectedProtections().containsKey(sender)){
					Civilizations.getSelectedProtections().get(sender).getGroup().getProtection().getPermissionSet().getFancyMessage().send(sender);;
				} else {
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no protection selected.");
				}
				return true;
			}
		}
		return false;
	}

}
