package com.kylantraynor.civilizations.hook.towny;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.commands.CommandGroup;
import com.kylantraynor.civilizations.groups.settlements.Camp;

public class CommandTownyTown extends CommandGroup{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"Null", "INFO"};
		if(!args[0].equals("Null") && args.length >= 2){
			TownyTown t = TownyTown.get(args[0]);
			if(t == null) return true;
			switch(args[1].toUpperCase()){
			case "INFO":
				
				break;
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length >= 3){
						if(args[2].equalsIgnoreCase("RANK")){
							if(args.length == 4){
								p.chat("/group " + t.getId() + " members rank " + args[3]);
							} else if(args.length > 4){
								p.chat("/group " + t.getId() + " members rank " + args[4] + " " + args[5]);
							} else {
								p.sendMessage(Civilizations.messageHeader + ChatColor.RED + "Arguments must be " + ChatColor.WHITE + "Members Rank <rank name> [page]");
							}
							return true;
						} else {
							p.chat("/group " + t.getId() + " members " + args[2]);
						}
					} else {
						p.chat("/group " + t.getId() + " members");
					}
				}
				break;
			case "PERMISSIONS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					p.chat("/group " + t.getId() + " permissions");
				}
				break;
			}
		}
		return true;
	}

}
