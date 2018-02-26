package com.kylantraynor.civilizations.commands;

import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.banners.Banner;
import com.kylantraynor.civilizations.groups.House;
import com.kylantraynor.civilizations.groups.Nation;
import com.kylantraynor.civilizations.protection.PermissionType;

public class CommandNation implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		// Makes sure the user of this command is a player.
		if(!(sender instanceof Player)){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This command has to be used by a player.");
			return true;
		}
		// Makes sure there is more than just /Nation
		if(args.length == 0){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "/Nation [name]");
			return true;
		}
		// TODO Need to add the list of all the nations.
		if(args[0].equalsIgnoreCase("LIST")){
			return true;
		}
		// When the command is of the kind /Nation [Nation Name]
		// Gets the Nation
		Nation nation = Nation.get(args[0]);
		if(nation != null){
			if(args.length > 1){
				switch(args[1].toUpperCase()){
				// /Nation [Name] Info
				case "INFO":
					nation.getInteractiveInfoPanel((Player) sender).send(sender);
					return true;
				// /Nation [Name] GetBanner
				case "GETBANNER":
					Player p2 = (Player ) sender;
					if(p2.isOp()){
						p2.getInventory().addItem(nation.getBanner().getItemStack());
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You don't have the permission to do this.");
					}
					return true;
				// /Nation [Name] SetBanner
				case "SETBANNER":
					Player p = (Player) sender;
					if(!nation.hasPermission(PermissionType.MANAGE_BANNER, null, p)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You are not allowed to change the banner of this Nation.");
					}
					Block target = p.getTargetBlock(null, 15);
					if(target != null){
						if(target.getType() == Material.BANNER || target.getType() == Material.STANDING_BANNER){
							BlockState state = target.getState();
							org.bukkit.block.Banner b = (org.bukkit.block.Banner)state;
							nation.setBanner(Banner.get(b));
							return true;
						}
					}
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You need to target a banner to use this command.");
					return true;
				// /Nation [Name] GetWords
				case "GETWORDS":
					sender.sendMessage(nation.getWords());
					return true;
				// /Nation [Name] Vassals
				/*case "VASSALS":
					house.getInteractiveVassalsList().send(sender);
					return true;*/
				// /Nation [Name] SetWords Some words with spaces
				case "SETWORDS":
					if(!nation.hasPermission(PermissionType.MANAGE_HOUSE, null, (Player) sender)){
						sender.sendMessage(nation.getChatHeader() + ChatColor.RED + "You do not have the permission to do this.");
					}
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						sb.append(args[i]).append(" ");
					}
					nation.setWords(sb.toString().trim());
					nation.sendMessage("Words of the Nation have been changed to \"" + nation.getWords() + "\".", null);
					return true;
				// /Nation [Name] Adopt [Player]
					/*
				case "ADOPT":
					if(!sender.hasPermission("civilizations.house.adopt") && !sender.isOp()){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do this.");
					}
					if(house.hasPermission(PermissionType.MANAGE_HOUSE, null, (Player) sender)){
						sender.sendMessage(house.getChatHeader() + ChatColor.RED + "You do not have the permission to do this.");
					}
					if(args.length > 2){
						for(Player p1 : Bukkit.getServer().getOnlinePlayers()){
							if(p1.getName().equalsIgnoreCase(args[2])){
								House p1House = House.get(p1);
								if(p1House == null){
									house.addMember(p1);
									house.sendMessage(ChatColor.GREEN + p1.getName() + " is now a member of House " + house.getName(), null);
									return true;
								} else {
									sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + p1.getName() + " is already a member of House " + p1House.getName());
									return true;
								}
							}
						}
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "/house " + args[0] + " adopt [playername]");
						return true;
					}
					*/
				// /House [Name] Create
				case "CREATE":
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "House " + args[0] + " already exists.");
					return true;
				}
			} else {
				((Player) sender).chat("/group " + nation.getId());
				return true;
			}
			
		} else {
			//Create nation
			if(args.length > 1){
				switch(args[1].toUpperCase()){
				case "CREATE":
					if(!sender.hasPermission("civilizations.nation.create") && !sender.isOp()){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do this.");
					}
					Player p = (Player) sender;
					Nation pNation = Nation.get(p);
					if(pNation != null){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You already belong to Nation " + pNation.getName() + ".");
						return true;
					}
					
					ItemStack is = p.getInventory().getItemInMainHand();
					if(is.getType() == Material.BANNER || is.getType() == Material.STANDING_BANNER){
						BannerMeta bm = (BannerMeta) is.getItemMeta();
						if(Banner.exist(bm)){
							//TODO check if the banner is owned by a house or by a nation.
							Nation n = Nation.get(Banner.get(bm));
							if(n != null) {
								sender.sendMessage(Civilizations.messageHeader + ChatColor.RED +
										"This banner is already used by Nation " + n.getName() + ".");
							}
							return true;
						}
						Nation n = new Nation(args[0], Banner.get(bm));
						if(!p.isOp()){
							n.addMember(p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "You've established Nation " + n.getName() + "!");
						} else {
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Nation " + n.getName() + " has been created.");
						}
						return true;
					}
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You need a banner in your hand to use this command.");
					return true;
				}
			}
		}
		
		return false;
	}
			
}
