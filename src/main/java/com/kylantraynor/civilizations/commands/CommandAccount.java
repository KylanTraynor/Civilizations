package com.kylantraynor.civilizations.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.players.CivilizationsAccount;

public class CommandAccount implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			CivilizationsAccount account = CivilizationsAccount.get(player.getUniqueId());
			account.openMenu(player);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "There are currently no console commands for /account.");
			return true;
		}
	}

}
