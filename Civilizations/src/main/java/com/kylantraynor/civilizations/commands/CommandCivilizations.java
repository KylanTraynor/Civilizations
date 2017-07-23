package com.kylantraynor.civilizations.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.chat.ChatTools;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.economy.Economy;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.util.MaterialAndData;

public class CommandCivilizations implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isOp()){
			if(args.length == 0) args = new String[]{"INFO"};
			switch(args[0].toUpperCase()){
			default:
			case "INFO":
				sender.sendMessage(ChatTools.formatTitle("Civilizations Plugin", ChatColor.GRAY));
				sender.sendMessage(ChatColor.GRAY + "Wiki Root: " + ChatColor.GOLD + Civilizations.getSettings().getWikiUrl());
				sender.sendMessage(ChatColor.GRAY + "Debug: " + ChatColor.GOLD + Civilizations.currentInstance.isDEBUG());
				sender.sendMessage(ChatColor.GRAY + "Using Chat: " + ChatColor.GOLD + Civilizations.currentInstance.useChat);
				break;
			case "UPDATEGROUPS":
				if(sender.isOp()){
					GroupManager.updateAllGroups();
				}
				break;
			case "GET":
				if(args.length < 2) {sender.sendMessage(ChatColor.RED + "/Civilizations Get <Property>"); return true;}
				switch(args[1].toUpperCase()){
				case "WIKIROOT":
					sender.sendMessage(ChatColor.GREEN + "Wiki links start with : " + Civilizations.getSettings().getWikiUrl());
					break;
				case "BALANCE":
					if(args.length < 3) {sender.sendMessage(ChatColor.RED + "/Civilizations Get Balance <Player>"); return true;}
					OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
					if(p.getUniqueId() == null) {sender.sendMessage(ChatColor.RED + "No player exists with this name.");return true;}
					EconomicEntity e = EconomicEntity.get(p.getUniqueId());
					if(e == null) {sender.sendMessage(ChatColor.RED + "This players isn't a valid Economic Entity."); return true;}
					sender.sendMessage(ChatColor.GREEN + p.getName() + " Balance: " + Economy.format(e.getBalance()));
					Civilizations.DEBUG("Is Player? " + e.isPlayer());
					break;
				}
			case "SET":
				if(!sender.isOp()) {sender.sendMessage(ChatColor.RED + "You don't have the permission to do this."); return true;}
				if(args.length <= 2) {sender.sendMessage(ChatColor.RED + "/Civilizations Set <property> <value>"); return true;}
				switch(args[1].toUpperCase()){
				case "WIKIROOT":
					Civilizations.getSettings().setWikiUrl(args[2]);
					sender.sendMessage(ChatColor.GREEN + "Wiki links have been set to " + Civilizations.getSettings().getWikiUrl() + "[entry name]!");
					break;
				default:
					sender.sendMessage(ChatColor.RED + "Unkown Property. (Try WikiRoot)");
				}
				return true;
			case "BUILDER":
				if(!sender.isOp()) {sender.sendMessage(ChatColor.RED + "You don't have the permission to do this."); return true;}
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
							sender.sendMessage(ChatColor.RED + "/civilizations builder setReplacement <material>(:data) <material>(:data)");
						}
						break;
					}
				}
				return true;
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
