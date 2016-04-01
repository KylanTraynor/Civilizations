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
			case "MEMBERS":
				if(Civilizations.getSelectedProtections().containsKey(sender)){
					Civilizations.getSelectedProtections().get(sender).getGroup().getInteractiveMembersList().send(sender);
				} else {
					sender.sendMessage(ChatColor.RED + "You have no protection selected.");
				}
				break;
			case "PERMISSIONS":
				if(Civilizations.getSelectedProtections().containsKey(sender)){
					Civilizations.getSelectedProtections().get(sender).getGroup().getProtection().getPermissionSet().getFancyMessage().send(sender);;
				} else {
					sender.sendMessage(ChatColor.RED + "You have no protection selected.");
				}
			}
		}
		return true;
	}

}
