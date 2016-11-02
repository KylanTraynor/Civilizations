package com.kylantraynor.civilizations.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.House;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.Warehouse;
import com.kylantraynor.civilizations.groups.settlements.plots.fort.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.util.Util;

public class CommandPlot implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if(sender instanceof Player){
			
			Player player = (Player) sender;
			
			if(args.length == 0) args = new String[]{"INFO"};
			
			/*
			 * Merges the selection with the plot intersecting.
			 */
			if(args[0].equalsIgnoreCase("ADD")){
				if(!SelectionManager.hasSelection(player)){
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no selection set.");
					return true;
				}
				Shape s = SelectionManager.getSelection(player);
				
				// Checks if the shape intersects with another plot.
				int plotsIntersecting = 0;
				Protection protection = null;
				for(Plot plot : CacheManager.getPlotList()){
					if(plot.getProtection().intersect(s)){
						plotsIntersecting++;
						protection = plot.getProtection();
					}
				}
				if(plotsIntersecting > 1){
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects too many plots.");
					return true;
				} else if(plotsIntersecting == 0 || protection == null) {
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection doesn't intersect any plot.");
					return true;
				}
				
				protection.add(s);
				player.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "The selection has been added to " + protection.getGroup().getName() + "!");
				return true;
			/*
			 * Creates a new plot from the selection 
			 */
			} else if(args[0].equalsIgnoreCase("CREATE")){
				
				if(!SelectionManager.hasSelection((Player) sender)){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no selection set.");
					return true;
				}
				// The +1 is to add the full block, since the coordinate of the block is in a corner, and we want the entire block.
				Shape s = SelectionManager.getSelection((Player) sender);
				
				// Checks if the shape intersects with another plot.
				for(Plot plot : CacheManager.getPlotList()){
					if(plot.getProtection().intersect(s)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects with another plot.");
						return true;
					}
				}
				
				Settlement set = Settlement.getClosest(s.getLocation());
				if(set != null){
					if(!set.canMergeWith(s)){
						set = null;
					}
				}
				if(set != null){
					if(!set.hasPermission(PermissionType.MANAGE_PLOTS, null, (Player) sender)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do that here.");
						return true;
					}
				}
				
				if(args.length >= 2){
					List<String> arguments = new ArrayList<String>();
					if(args.length >= 3){
						for(int i = 2; i < args.length; i++){
							arguments.add(args[i]);
						}
					}
					switch(args[1].toUpperCase()){
					case "HOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A house cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new House(args.length >= 3 ? Util.join(arguments, " ") : "House", s, set);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "House created in " + set.getName() + "!");
						}
						break;
					case "KEEP":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A keep cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Keep(args.length >= 3 ? Util.join(arguments, " ") : "Keep", s, set);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Keep created in " + set.getName() + "!");
						}
						break;
					case "WAREHOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A warehouse cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Warehouse(args.length >= 3 ? Util.join(arguments, " ") : "Warehouse", s, set);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Warehouse created in " + set.getName() + "!");
						}
						break;
					case "STALL":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A stall cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new MarketStall(args.length >= 3 ? Util.join(arguments, " ") : "Stall", s, set);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Market stall created in " + set.getName() + "!");
						}
						break;
					}
				} else {
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "Use /plot create [plot type]");
					return true;
				}
			}
			
		} else {
			sender.sendMessage("This command has to be used by a player.");
		}
		
		return false;
	}
}
