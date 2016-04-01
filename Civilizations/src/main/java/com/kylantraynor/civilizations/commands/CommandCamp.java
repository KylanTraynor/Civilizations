package com.kylantraynor.civilizations.commands;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.questions.ClearQuestion;
import com.kylantraynor.civilizations.questions.JoinQuestion;
import com.kylantraynor.civilizations.questions.LeaveQuestion;

public class CommandCamp extends CommandGroup{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"INFO"};
		switch (args[0].toUpperCase()){
		case "HERE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp closest = Camp.getClosest(p.getLocation());
				if(closest != null){
					if(closest.getLocation().distance(p.getLocation()) <= Camp.getSize() * 2){
						p.sendMessage(Camp.messageHeader + ChatColor.RED + "Too close to another camp.");
						return true;
					}
				}
				Camp c = new Camp(p.getLocation());
				c.addMember(p);
				c.setCreationDate(Instant.now());
				c.setDefaultPermissions();
				c.setExpireOn(Instant.now().plus(1, ChronoUnit.DAYS));
				p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camp created!");
				p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camps only last a day. Make sure to Renew it on the " + ChatColor.GOLD + "/camp" + ChatColor.GREEN + " screen!");
			}
			break;
		case "RENEW":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					if(ChronoUnit.HOURS.between(Instant.now(), c.getExpireOn()) > 22){
						p.sendMessage(Camp.messageHeader + ChatColor.RED + "You can only renew the camp once a day.");
					} else {
						c.setExpireOn(Instant.now().plus(1, ChronoUnit.DAYS));
						p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camp renewed for a day!");
					}
				}
			}
			break;
		case "CLEAR":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new ClearQuestion(c, p).ask();
				}
			}
			break;
		case "JOIN":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(c.isMember(p)){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're already part of this camp.");
				} else {
					if(c.hasOneMemberOnline()){
						p.sendMessage(Camp.messageHeader + ChatColor.BLUE + "You've requested to join this camp. Please wait for an answer.");
						new JoinQuestion(c, p).ask();
					} else {
						p.sendMessage(Camp.messageHeader + ChatColor.RED + "No member of this camp is online to accept your request.");
					}
				}
			}
			break;
		case "LEAVE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new LeaveQuestion(c, p).ask();
				}
			}
			break;
		case "MEMBERS":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					p.chat("/group " + c.getId() + " members");
				}
			}
			break;
		case "PERMISSIONS":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					p.chat("/group " + c.getId() + " permissions");
				}
			}
			break;
		case "INFO": default:
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					c.getInteractiveInfoPanel(p).send(p);
				}
			}
			break;
		}
		return true;
	}

}
