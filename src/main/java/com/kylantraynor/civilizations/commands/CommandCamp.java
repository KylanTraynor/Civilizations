package com.kylantraynor.civilizations.commands;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.managers.GroupManager;
import com.kylantraynor.civilizations.questions.ClearQuestion;
import com.kylantraynor.civilizations.questions.JoinQuestion;
import com.kylantraynor.civilizations.questions.LeaveQuestion;
import com.kylantraynor.civilizations.utils.Utils;

public class CommandCamp extends CommandGroup{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"INFO"};
		switch (args[0].toUpperCase()){
		case "HERE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = GroupManager.createCamp(p, p.getLocation());
				if(c != null){
					p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camp created!");
					p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camps only last a day. Make sure to Renew it on the " + ChatColor.GOLD + "/camp" + ChatColor.GREEN + " screen!");
				}
				return true;
			}
			break;
		case "RENEW":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					c.getInteractiveInfoPanel(p).send(p);
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					if(ChronoUnit.HOURS.between(Instant.now(), c.getSettings().getExpiryDate()) > Camp.campDuration - 1){
						c.getInteractiveInfoPanel(p).send(p);
						p.sendMessage(Camp.messageHeader + ChatColor.RED + "You can only renew the camp once a day.");
					} else {
						c.setExpireOn(Instant.now().plus(Camp.campDuration, ChronoUnit.HOURS));
						c.getInteractiveInfoPanel(p).send(p);
						p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "Camp renewed for " + Utils.durationToString(Instant.now(), c.getExpireOn())+ "!");
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
					c.getInteractiveInfoPanel(p).send(p);
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new ClearQuestion(c, p).ask();
				}
			}
			break;
		case "CLAIM":
		case "JOIN":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(c.isMember(p)){
					c.getInteractiveInfoPanel(p).send(p);
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're already part of this camp.");
				} else {
					if(c.hasOneMemberOnline()){
						p.sendMessage(Camp.messageHeader + ChatColor.BLUE + "You've requested to join this camp. Please wait for an answer.");
						new JoinQuestion(c, p).ask();
					} else {
						if(c.getMembers().size() > 0) {
							c.getInteractiveInfoPanel(p).send(p);
							p.sendMessage(Camp.messageHeader + ChatColor.RED + "No member of this camp is online to accept your request.");
						} else {
							c.addMember(p);
							c.getInteractiveInfoPanel(p).send(p);
							p.sendMessage(Camp.messageHeader + ChatColor.GREEN + "This camp was abandonned. You've claimed it for yourself!");
						}
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
					c.getInteractiveInfoPanel(p).send(p);
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new LeaveQuestion(c, p).ask();
				}
			}
			break;
		case "RANK":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				List<String> a = Arrays.asList(args).subList(1, args.length - 1);
				processRankCommand(p, c, a.toArray(new String[a.size()]));
			}
			break;
		case "MEMBERS":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					if(args.length >= 2){
						p.chat("/group " + c.getIdentifier().toString() + " members " + args[1]);
					} else {
						p.chat("/group " + c.getIdentifier().toString() + " members");
					}
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
					p.chat("/group " + c.getIdentifier().toString() + " permissions");
				}
			}
			break;
		case "UPGRADE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(Camp.messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					p.chat("/group " + c.getIdentifier().toString() + " upgrade");
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
