package com.kylantraynor.civilizations.hook.towny;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.commands.CommandGroup;

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
					if(args.length > 2){
						t.getInteractiveMembersList(Integer.getInteger(args[2])).send(p);
					} else {
						t.getInteractiveMembersList().send(p);
					}
				}
				break;
			}
		}
		return true;
	}

}
