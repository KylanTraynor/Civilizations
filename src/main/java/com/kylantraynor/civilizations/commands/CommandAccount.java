package com.kylantraynor.civilizations.commands;

import com.kylantraynor.civilizations.managers.AccountManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.players.CivilizationsAccount;

import java.util.concurrent.ExecutionException;

public class CommandAccount implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			try{
				CivilizationsAccount account = AccountManager.getAccount(player.getUniqueId());
				account.openMenu(player);
			} catch (ExecutionException ex){
				ex.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to get Account.");
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "There are currently no console commands for /account.");
			return true;
		}
	}

}
