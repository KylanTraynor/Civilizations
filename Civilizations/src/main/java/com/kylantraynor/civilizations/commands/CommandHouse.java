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
import com.kylantraynor.civilizations.protection.PermissionType;

public class CommandHouse implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This command has to be used by a player.");
			return true;
		}
		
		if(args.length == 0){
			sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "/house [name]");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("LIST")){
			return true;
		}
		
		House house = House.get(args[0]);
		if(house != null){
			if(args.length > 1){
				switch(args[1].toUpperCase()){
				case "INFO":
					house.getInteractiveInfoPanel((Player) sender).send(sender);;
					return true;
				case "GETBANNER":
					Player p2 = (Player ) sender;
					if(p2.isOp()){
						p2.getInventory().addItem(house.getBanner().getItemStack());
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You don't have the permission to do this.");
					}
					return true;
				case "SETBANNER":
					Player p = (Player) sender;
					if(!house.hasPermission(PermissionType.MANAGE_BANNER, null, p)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You are not allowed to change the banner of this house.");
					}
					Block target = p.getTargetBlock((Set<Material>) null, 15);
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
				case "GETWORDS":
					sender.sendMessage(house.getWords());
					return true;
				case "SETWORDS":
					if(!house.hasPermission(PermissionType.MANAGE_HOUSE, null, (Player) sender)){
						sender.sendMessage(house.getChatHeader() + ChatColor.RED + "You do not have the permission to do this.");
					}
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < args.length; i++){
						sb.append(args[i] + " ");
					}
					house.setWords(sb.toString().trim());
					house.sendMessage("Words of the house have been changed to \"" + house.getWords() + "\".", null);
					return true;
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
									house.sendMessage(ChatColor.GREEN + p1.getName() + " is now a member of house " + house.getName(), null);
									return true;
								} else {
									sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + p1.getName() + " is already a member of house " + p1House.getName());
									return true;
								}
							}
						}
					} else {
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "/house " + args[0] + " adopt [playername]");
						return true;
					}
					
				case "CREATE":
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "House " + args[0] + " already exists.");
					return true;
				}
			} else {
				((Player) sender).chat("/group " + house.getId());
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
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You already belong to house " + pHouse.getName() + ".");
						return true;
					}
					
					ItemStack is = p.getInventory().getItemInMainHand();
					if(is.getType() == Material.BANNER || is.getType() == Material.STANDING_BANNER){
						BannerMeta bm = (BannerMeta) is.getItemMeta();
						House h = new House(args[0], Banner.get(bm));
						if(!p.isOp()){
							h.addMember(p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "You've established house " + h.getName() + "!");
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
