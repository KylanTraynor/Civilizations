package com.kylantraynor.civilizations.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.House;
import com.kylantraynor.civilizations.groups.settlements.plots.Keep;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.Warehouse;
import com.kylantraynor.civilizations.groups.settlements.plots.market.MarketStall;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;

public class CommandPlot implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if(sender instanceof Player){
			
			if(args.length == 0) args = new String[]{"INFO"};
			
			boolean hasProtectionSelected = Civilizations.getSelectedProtections().containsKey(sender);
			boolean hasSelectionPoints = Civilizations.getSelectionPoints().containsKey(sender);
			
			/*
			 * Merges the selection with the plot intersecting.
			 */
			if(args[0].equalsIgnoreCase("MERGE")){
				if(!hasSelectionPoints){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no selection points set.");
					return true;
				}

				Location[] points = Civilizations.getSelectionPoints().get(sender);
				
				if(points[0] == null || points[1] == null){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "Selection points are missing.");
					return true;
				}
				
				Location middlePoint = new Location(points[0].getWorld(), (points[0].getX() + points[1].getX())/2,
						(points[0].getY() + points[1].getY()) /2, (points[0].getZ() + points[1].getZ()) / 2);
				
				int width = (int) Math.abs(points[1].getX() - points[0].getX());
				int height = (int) Math.abs(points[1].getY() - points[0].getY());
				int length = (int) Math.abs(points[1].getZ() - points[0].getZ());
				
				Location firstCorner = new Location(middlePoint.getWorld(),
						middlePoint.getX() - (width) / 2.0,
						middlePoint.getY() - (height) / 2.0,
						middlePoint.getZ() - (length) / 2.0);
				
				Shape s = new Prism(firstCorner, width, height, length);
				
				// Checks if the shape intersects with another plot.
				int plotsIntersecting = 0;
				for(Plot plot : Cache.getPlotList()){
					if(plot.getProtection().intersect(s)){
						plotsIntersecting++;
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects with another plot.");
						return true;
					}
				}
				if(plotsIntersecting > 1){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects with to many plots.");
					return true;
				}
				
			/*
			 * Creates a new plot from the selection 
			 */
			} else if(args[0].equalsIgnoreCase("CREATE")){
				
				if(!hasSelectionPoints){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no selection points set.");
					return true;
				}
				
				Location[] points = Civilizations.getSelectionPoints().get(sender);
				
				if(points[0] == null || points[1] == null){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "Selection points are missing.");
					return true;
				}
				
				Location middlePoint = new Location(points[0].getWorld(), (points[0].getX() + points[1].getX())/2,
						(points[0].getY() + points[1].getY()) /2, (points[0].getZ() + points[1].getZ()) / 2);
				
				int width = (int) Math.abs(points[1].getX() - points[0].getX());
				int height = (int) Math.abs(points[1].getY() - points[0].getY());
				int length = (int) Math.abs(points[1].getZ() - points[0].getZ());
				
				Location firstCorner = new Location(middlePoint.getWorld(),
						middlePoint.getX() - (width) / 2.0,
						middlePoint.getY() - (height) / 2.0,
						middlePoint.getZ() - (length) / 2.0);
				
				Shape s = new Prism(firstCorner, width, height, length);
				
				// Checks if the shape intersects with another plot.
				for(Plot plot : Cache.getPlotList()){
					if(plot.getProtection().intersect(s)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects with another plot.");
						return true;
					}
				}
				
				Settlement set = Settlement.getClosest(middlePoint);
				if(set != null && set.distance(middlePoint) > Civilizations.settlementMergeRadius){
					set = null;
				} else if(set != null && !set.hasPermission(PermissionType.MANAGE_PLOTS, null, (Player) sender)){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do that here.");
					return true;
				}
				
				if(args.length >= 2){
					switch(args[1].toUpperCase()){
					case "HOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A house cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new House("House", s, set);
							Civilizations.getSelectionPoints().remove(sender);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "House created in " + set.getName() + "!");
						}
						break;
					case "KEEP":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A keep cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Keep("Keep", s, set);
							Civilizations.getSelectionPoints().remove(sender);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Keep created in " + set.getName() + "!");
						}
						break;
					case "WAREHOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A warehouse cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Warehouse("Warehouse", s, set);
							Civilizations.getSelectionPoints().remove(sender);
							Civilizations.getSelectedProtections().put((Player) sender, p.getProtection());
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Warehouse created in " + set.getName() + "!");
						}
						break;
					case "STALL":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A stall cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new MarketStall("Stall", s, set);
							Civilizations.getSelectionPoints().remove(sender);
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
