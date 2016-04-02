package com.kylantraynor.civilizations.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.protection.Rank;

public class CommandGroup implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"Null", "INFO"};
		Integer id = null;
		try{
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException e){ id = null;}
		Civilizations.log("INFO", "Group ID: " + id);
		if(id != null && args.length >= 2){
			Group g = Group.get(id);
			if(g == null) return true;
			Civilizations.log("INFO", "Group: " + g.getName());
			switch(args[1].toUpperCase()){
			case "INFO":
				if(sender instanceof Player){
					g.getInteractiveInfoPanel((Player)sender);
				}
				break;
			case "RANK":
				if(sender instanceof Player){
					Player p = (Player) sender;
					List<String> a = new ArrayList<String>();
					for(int i = 2; i < args.length; i++){
						a.add(args[i]);
					}
					processRankCommand(p, g, a.toArray(new String[a.size()]));
				}
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if (args.length == 4 || (args.length == 3)) {
						Rank r = g.getProtection().getRank(args[2]);
						if(r != null){
							try{
								g.getInteractiveRankMembers(r, Integer.parseInt(args[3])).send(p);
							} catch (NumberFormatException e){
								p.sendMessage(Civilizations.messageHeader + ChatColor.RED + "4th Argument needs to be a page number!");
							}
						} else {
							p.sendMessage(g.getChatHeader() + ChatColor.RED + "No rank has the name '" + args[2] + "'.");
						}
					} else if(args.length == 3){
						try{
							g.getInteractiveMembersList(Integer.parseInt(args[2])).send(p);
						} catch (NumberFormatException e){
							p.sendMessage(Civilizations.messageHeader + ChatColor.RED + "3rd Argument needs to be a page number!");
						}
					} else {
						g.getInteractiveMembersList().send(p);
					}
				} else {
					for(UUID i : g.getMembers()){
						sender.sendMessage(Bukkit.getServer().getOfflinePlayer(i).getName());
					}
				}
				break;
			case "PERMISSIONS":
				if(sender instanceof Player){
					g.getProtection().getPermissionSet().getFancyMessage().send(sender);
				} else {
					sender.sendMessage(g.getProtection().getPermissionSet().toString());
				}
			}
		}
		return true;
	}
	
	protected void processRankCommand(Player p, Group g, String[] args) {
		switch(args.length){
		case 1:
			/*
			 * Not Implemented Yet : Give Player's rank info
			 */
		default:
			switch(args[0].toUpperCase()){
			case "MEMBERS":
				Rank pr = g.getProtection().getRank(p);
				if(pr!=null){
					if(args.length == 1){
						g.getInteractiveRankMembers(pr, 0);
					} else {
						g.getInteractiveRankMembers(pr, Integer.parseInt(args[1]));
					}
				} else {p.sendMessage(g.getChatHeader() + ChatColor.RED + "You have no rank in this group.");};
			default:
				Rank r = g.getProtection().getRank(args[0]);
				if(r != null){
					if(args.length >= 2){
						switch(args[1].toUpperCase()){
						case "MEMBERS":
							if(args.length == 2){
								g.getInteractiveRankMembers(r, 0);
							} else {
								g.getInteractiveRankMembers(r, Integer.parseInt(args[2]));
							}
						}
					}
				} else {p.sendMessage(g.getChatHeader() + ChatColor.RED + "Rank '" + args[0] + "' doesn't exist.");};
			}
		}
	}

}
