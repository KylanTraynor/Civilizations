package com.kylantraynor.civilizations.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.util.MaterialAndData;

public class CommandCivilizations implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0 && sender.isOp()){
			switch(args[0].toUpperCase()){
			case "UPDATEGROUPS":
				if(sender.isOp()){
					GroupManager.updateAllGroups();
				}
				break;
			case "BUILDER":
				if(sender.isOp()){
					if(args.length > 1){
						switch(args[1].toUpperCase()){
						case "CANCELALL":
							GroupManager.cancelAllBuilds();
							sender.sendMessage(ChatColor.GREEN + "All build projects have been cancelled.");
							break;
						case "SETREPLACEMENT":
							if(args.length == 4){
								MaterialAndData.addPasteReplacementFor(args[2], args[3]);
								sender.sendMessage(ChatColor.GREEN + "Replacement added!");
							} else {
								sender.sendMessage(ChatColor.RED + "/civilizations builder setReplacement [material]<:data> [material]<:data>");
							}
							break;
						}
					}
				}
				break;
			case "TOGGLE":
				if(args.length > 1){
					switch(args[1].toUpperCase()){
					case "CHAT":
						Civilizations.useChat = !Civilizations.useChat;
						break;
					case "CLEARING":
						Civilizations.setClearing(!Civilizations.isClearing());
						break;
					case "PROTECTIONMODE":
						if(!(sender instanceof Player)) return false;
						if(Civilizations.getPlayersInProtectionMode().contains((Player) sender)){
							Civilizations.getPlayersInProtectionMode().remove((Player) sender);
							sender.sendMessage(Civilizations.messageHeader + "Protection mode turned off.");
						} else {
							Civilizations.getPlayersInProtectionMode().add((Player) sender);
							sender.sendMessage(Civilizations.messageHeader + "Protection mode turned on.");
						}
						break;
					case "DEBUG":
						if(sender.isOp()){
							Civilizations.currentInstance.setDEBUG(!Civilizations.currentInstance.isDEBUG());
							sender.sendMessage(Civilizations.messageHeader + "DEBUG: " + Civilizations.currentInstance.isDEBUG());
						}
					}
				}
			}
		}
		return true;
	}

}
