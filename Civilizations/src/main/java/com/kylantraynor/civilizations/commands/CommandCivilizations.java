package com.kylantraynor.civilizations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;

public class CommandCivilizations implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length > 0 && sender.isOp()){
			switch(args[0].toUpperCase()){
			case "TOGGLE":
				if(args.length > 1){
					switch(args[1].toUpperCase()){
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
