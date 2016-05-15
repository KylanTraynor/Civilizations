package com.kylantraynor.civilizations.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Economy;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;

public class CommandGroup implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"Null", "MENU"};
		Integer id = null;
		try{
			id = Integer.parseInt(args[0]);
		} catch (NumberFormatException e){ id = null;}
		Civilizations.log("INFO", "Group ID: " + id);
		if(args.length == 1){ args = new String[]{"" + id, "MENU"};}
		if(id != null && args.length >= 2){
			Group g = Group.get(id);
			if(g == null) return true;
			Civilizations.log("INFO", "Group: " + g.getName());
			switch(args[1].toUpperCase()){
			case "MENU":
				if(sender instanceof Player){
					g.openMenu((Player) sender);
				}
				break;
			case "REMOVE":
				if(sender instanceof Player){
					if(g instanceof MarketStall){
						if(((MarketStall) g).isOwner((Player) sender)){
							g.remove();
							sender.sendMessage(ChatColor.GREEN + "Stall has been removed.");
						}
					}
				}
			case "SETNAME":
				if(sender instanceof Player){
					if(g instanceof MarketStall){
						if(((MarketStall)g).isOwner((Player) sender) || ((MarketStall)g).getRenter() == sender){
							StringBuilder sb = new StringBuilder();
							for(int i = 2; i < args.length; i++){
								sb.append(args[i] + " ");
							}
							g.setName(sb.toString().trim());
							sender.sendMessage(g.getChatHeader() + ChatColor.GREEN + "Name changed!");
						} else {
							sender.sendMessage(g.getChatHeader() + ChatColor.RED + "You don't have the permission to do that.");
						}
					}else if(g instanceof Plot){
						if(g.hasPermission(PermissionType.MANAGE_PLOTS, null, (Player) sender)){
							StringBuilder sb = new StringBuilder();
							for(int i = 2; i < args.length; i++){
								sb.append(args[i] + " ");
							}
							g.setName(sb.toString().trim());
							sender.sendMessage(g.getChatHeader() + ChatColor.GREEN + "Name changed!");
						} else {
							sender.sendMessage(g.getChatHeader() + ChatColor.RED + "You don't have the permission to do that.");
						}
					} else {
						if(g.hasPermission(PermissionType.MANAGE, null, (Player) sender)){
							StringBuilder sb = new StringBuilder();
							for(int i = 2; i < args.length; i++){
								sb.append(args[i] + " ");
							}
							g.setName(sb.toString().trim());
							sender.sendMessage(g.getChatHeader() + ChatColor.GREEN + "Name changed!");
						} else {
							sender.sendMessage(g.getChatHeader() + ChatColor.RED + "You don't have the permission to do that.");
						}
					}
				} else {
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						sb.append(args[i] + " ");
					}
					g.setName(sb.toString().trim());
					sender.sendMessage(g.getChatHeader() + ChatColor.GREEN + "Name changed!");
				}
				return true;
			case "TOGGLERENTABLE":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						MarketStall stall = (MarketStall) g;
						if(stall.isOwner(p)){
							stall.setForRent(!stall.isForRent());
							if(stall.isForRent()){
								sender.sendMessage(stall.getChatHeader() +ChatColor.GREEN+ "The stall is now for rent.");
							} else {
								sender.sendMessage(stall.getChatHeader() +ChatColor.GREEN+ "The stall is no longer for rent.");
							}
						}
					}
				}
				break;
			case "PURCHASE":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						MarketStall stall = (MarketStall) g;
						if(stall.getOwner() == null){
							stall.setOwner(p);
							p.sendMessage(stall.getChatHeader() +ChatColor.GREEN+ "You've purchased this stall!");
						}
					}
				}
				break;
			case "SETRENT":
				if(sender instanceof Player && args.length > 2){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						MarketStall stall = (MarketStall) g;
						if(stall.isOwner(p)){
							stall.setRent(Double.parseDouble(args[2]));
							sender.sendMessage(stall.getChatHeader() +ChatColor.GREEN+ "The rent for this stall is now " + Economy.format(stall.getRent()) + ".");
						}
					}
				}
				break;
			case "UPGRADE":
				if(sender instanceof Player){
					if(g.hasPermission(PermissionType.UPGRADE, null, ((Player)sender))){
						if(g.upgrade()){
							sender.sendMessage(g.getChatHeader() + ChatColor.GREEN + "Upgrade successful!");
						} else {
							sender.sendMessage(g.getChatHeader() + ChatColor.RED + "Failed to upgrade.");
						}
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to upgrade this.");
					}
				}
				break;
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
				break;
			case "KICK":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						if(((MarketStall)g).isOwner(p)){
							((MarketStall)g).setRenter(null);
						}
					}
				}
				break;
			case "JOIN":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						if(((MarketStall)g).getRenter() == null){
							((MarketStall)g).setRenter(p);
							((MarketStall)g).payRent();
						}
					}
				}
				break;
			case "LEAVE":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(g instanceof MarketStall){
						if(((MarketStall)g).getRenter() == p){
							((MarketStall)g).setRenter(null);
						}
					}
				}
				break;
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length == 3){
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
			Rank playerRank = g.getProtection().getRank(p);
			if(playerRank != null){
				g.getInteractiveRankPanel(playerRank);
			} else {p.sendMessage(g.getChatHeader() + ChatColor.RED + "You have no rank in this group.");};
			break;
		default:
			switch(args[0].toUpperCase()){
			case "MEMBERS":
				Rank pr = g.getProtection().getRank(p);
				if(pr!=null){
					if(args.length == 1){
						g.getInteractiveRankMembers(pr, 0).send(p);;
					} else {
						g.getInteractiveRankMembers(pr, Integer.parseInt(args[1])).send(p);;
					}
				} else {p.sendMessage(g.getChatHeader() + ChatColor.RED + "You have no rank in this group.");};
				break;
			default:
				Rank r = g.getProtection().getRank(args[0]);
				if(r != null){
					if(args.length >= 2){
						switch(args[1].toUpperCase()){
						case "MEMBERS":
							if(args.length == 2){
								g.getInteractiveRankMembers(r, 0).send(p);;
							} else {
								g.getInteractiveRankMembers(r, Integer.parseInt(args[2])).send(p);;
							}
						}
					}
				} else {p.sendMessage(g.getChatHeader() + ChatColor.RED + "Rank '" + args[0] + "' doesn't exist.");};
				break;
			}
		}
	}

}
