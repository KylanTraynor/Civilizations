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
			if(args.length == 0){
                try{
                    CivilizationsAccount account = AccountManager.getAccount(player.getUniqueId());
                    account.openMenu(player);
                } catch (ExecutionException ex){
                    ex.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Failed to get Account.");
                }
            } else if(args.length == 1){
			    switch(args[0].toUpperCase()){
			        case "LOGIN":
                        if(AccountManager.isActive(player.getUniqueId())){
                            sender.sendMessage(ChatColor.RED + "You are already logged in.");
                        } else {

                            try{
                                CivilizationsAccount ca = AccountManager.login(player, true);
                                sender.sendMessage(ChatColor.GREEN + "Successfully logged in to your account.");
                            } catch (ExecutionException ex) { sender.sendMessage(ChatColor.RED + "Error while loading account."); }
                        }
                        break;
                    case "LOGOUT":
                        CivilizationsAccount ca = AccountManager.logout(player);
                        if(ca != null){
                            sender.sendMessage(ChatColor.GREEN + "Successfully logged out of your account.");
                        } else sender.sendMessage(ChatColor.RED + "You are already not logged in.");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown command.");
                }
            }
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "There are currently no console commands for /account.");
			return true;
		}
	}

}