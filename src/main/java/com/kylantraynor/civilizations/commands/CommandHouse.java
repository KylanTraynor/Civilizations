package com.kylantraynor.civilizations.commands;

import java.util.Set;

import com.kylantraynor.civilizations.managers.ProtectionManager;
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
import com.kylantraynor.civilizations.protection.PermissionType;

public class CommandHouse implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// Makes sure the user of this command is a player.
		if(!(sender instanceof Player)){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This command has to be used by a player.");
			return true;
		}
		Player player = (Player) sender;
		
		
		// Makes sure there is more than just /House
		if(args.length == 0){
			args = new String[]{"LIST"};
			//sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "/house [name]");
			//return true;
		}
		// Checks if the list of all the houses is requested.
		if(args[0].equalsIgnoreCase("LIST")){
			House.getHousesListChatMessage().send(player);
			return true;
		}
		
		// When the command is of the kind /House [House Name]
		// Gets the House
		House house = House.get(args[0]);
		if(house != null){
			if(args.length > 1){
				switch(args[1].toUpperCase()){
				// /House [Name] Info
				case "INFO":
					house.getInteractiveInfoPanel((Player) sender).send(sender);
					return true;
				// /House [Name] GetBanner
				case "GETBANNER":
					if(player.isOp()){
						player.getInventory().addItem(house.getBanner().getItemStack());
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You don't have the permission to do this.");
					}
					return true;
				// /House [Name] SetBanner
				case "SETBANNER":
					if(!ProtectionManager.hasPermission(PermissionType.MANAGE_BANNER, house, player, false)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You are not allowed to change the banner of this House.");
					}
					Block target = player.getTargetBlock(null, 15);
					if(target != null){
						if(target.getType() == Material.BANNER || target.getType() == Material.STANDING_BANNER){
							BlockState state = target.getState();
							org.bukkit.block.Banner b = (org.bukkit.block.Banner)state;
							house.setBanner(Banner.get(b));
							return true;
						}
					}
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You need to target a banner to use this command.");
					return true;
				// /House [Name] GetWords
				case "GETWORDS":
					sender.sendMessage(house.getWords());
					return true;
				// /House [Name] Vassals
				case "VASSALS":
					house.getInteractiveVassalsList().send(sender);
					return true;
				// /House [Name] SetWords Some words with spaces
				case "SETWORDS":
					if(!ProtectionManager.hasPermission(PermissionType.MANAGE_HOUSE, house, (Player) sender, false)){
						sender.sendMessage(house.getChatHeader() + ChatColor.RED + "You do not have the permission to do this.");
					}
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						sb.append(args[i]).append(" ");
					}
					house.setWords(sb.toString().trim());
					house.sendMessage("Words of the House have been changed to \"" + house.getWords() + "\".", null);
					return true;
				// /House [Name] Adopt [Player]
				case "ADOPT":
					if(!sender.hasPermission("civilizations.house.adopt") && !sender.isOp()){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do this.");
					}
					if(ProtectionManager.hasPermission(PermissionType.MANAGE_HOUSE, house, (Player) sender, true)){
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
				// /House [Name] Solidor
				case "CREATE":
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "House " + args[0] + " already exists.");
					return true;
				}
			} else {
				((Player) sender).chat("/group " + house.getUniqueId().toString());
				return true;
			}
			
		} else {
			//Create house
			if(args.length > 1){
				switch(args[1].toUpperCase()){
				case "CREATE":
					if(!sender.hasPermission("civilizations.house.create") && !sender.isOp()){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do this.");
					}
					Player p = (Player) sender;
					House pHouse = House.get(p);
					if(pHouse != null){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You already belong to House " + pHouse.getName() + ".");
						return true;
					}
					
					ItemStack is = p.getInventory().getItemInMainHand();
					if(is.getType() == Material.BANNER || is.getType() == Material.STANDING_BANNER){
						BannerMeta bm = (BannerMeta) is.getItemMeta();
						if(Banner.exist(bm)){
							House h = House.get(Banner.get(bm));
							if(h != null) {
								sender.sendMessage(Civilizations.messageHeader + ChatColor.RED +
										"This banner is already used by House " + h.getName() + ".");
								return true;
							}
						}
						House h = new House(args[0], Banner.get(bm));
						if(!p.isOp()){
							h.addMember(p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "You've established House " + h.getName() + "!");
						} else {
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "House " + h.getName() + " has been created.");
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
