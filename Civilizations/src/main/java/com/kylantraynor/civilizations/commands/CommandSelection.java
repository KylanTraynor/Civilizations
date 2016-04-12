package com.kylantraynor.civilizations.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;

public class CommandSelection implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length >= 1){
			switch(args[0].toUpperCase()){
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
