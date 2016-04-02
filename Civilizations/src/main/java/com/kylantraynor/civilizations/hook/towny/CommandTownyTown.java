package com.kylantraynor.civilizations.hook.towny;

import java.util.ArrayList;
import java.util.List;

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
			case "RANK":
				if(sender instanceof Player){
					Player p = (Player) sender;
					List<String> a = new ArrayList<String>();
					for(int i = 2; i < args.length; i++){
						a.add(args[i]);
					}
					processRankCommand(p, t, a.toArray(new String[a.size()]));
				}
				break;
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length >= 3){
						p.chat("/group " + t.getId() + " members " + args[2]);
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
