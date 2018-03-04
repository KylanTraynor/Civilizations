package com.kylantraynor.civilizations.commands;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.territories.InfluenceMap;
import com.kylantraynor.civilizations.territories.InfluentSite;
import com.kylantraynor.civilizations.utils.Utils;

public class CommandPlot implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if(sender instanceof Player){
			
			Player player = (Player) sender;
			
			if(args.length == 0) args = new String[]{"INFO"};
			if(args[0].equalsIgnoreCase("INFO")){
				Plot p = Plot.getAt(player.getLocation());
				if(p == null){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "There is no plot here.");
				} else {
					p.getInteractiveInfoPanel(player).send(player);
				}
			/*
			 * Displays the plot menu.
			 */
			} else if(args[0].equalsIgnoreCase("MENU")){
				Plot p = Plot.getAt(player.getLocation());
				if(p == null){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "There is no plot here.");
				} else {
					p.openMenu(player);
				}
			/*
			 * Merges the selection with the plot intersecting.
			 */
			} else if(args[0].equalsIgnoreCase("ADD")){
				if(!SelectionManager.hasSelection(player)){
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You have no selection set.");
					return true;
				}
				Shape s = (Shape)SelectionManager.getSelection(player);
				
				// Checks if the shape intersects with another plot.
				int plotsIntersecting = 0;
				Plot p = null;
				for(Plot plot : Plot.getAll()){
					if(plot.intersect(s)){
						plotsIntersecting++;
						p = plot;
					}
				}
				if(plotsIntersecting > 1){
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects too many plots.");
					return true;
				} else if(plotsIntersecting == 0 || p == null) {
					player.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection doesn't intersect any plot.");
					return true;
				}
				
				p.getSettings().addShape(s);
				player.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "The selection has been added to " + p.getName() + "!");
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
				Shape s = (Shape) SelectionManager.getSelection((Player) sender);
				if(s == null){
					sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have a shape selected.");
					return true;
				}
				// Checks if the shape intersects with another plot.
				for(Plot plot : Plot.getAll()){
					if(plot.intersect(s)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "The selection intersects with another plot.");
						return true;
					}
				}
				
				//Settlement set = Settlement.getAt(s.getLocation());
				//if(set != null) Civilizations.DEBUG("Distance to " + set.getName() + ": " + set.distance(s));
				Settlement set = Settlement.getClosest(s.getLocation());
				if(set != null){
					if(!set.canMergeWith(s)){
						set = null;
					}
				}
				if(set != null){
					if(!ProtectionManager.hasPermission(PermissionType.MANAGE_PLOTS, set, (Player) sender, true)){
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to do that here.");
						return true;
					}
				}
				
				if(args.length >= 2){
					List<String> arguments = new ArrayList<>();
					if(args.length >= 3){
						arguments = Arrays.asList(args).subList(2, args.length - 1);
					}
					switch(args[1].toUpperCase()){
					case "HOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A house cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Plot(args.length >= 3 ? Utils.join(arguments, " ") : "House", s, set);
							p.setPersistent(true);
							p.setPlotType(PlotType.HOUSE);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "House created in " + set.getName() + "!");
						}
						break;
					case "KEEP":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A keep cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Plot(args.length >= 3 ? Utils.join(arguments, " ") : "Keep", s, set);
							p.setPersistent(true);
							p.setPlotType(PlotType.KEEP);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Keep created in " + set.getName() + "!");
						}
						break;
					case "WAREHOUSE":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A warehouse cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Plot(args.length >= 3 ? Utils.join(arguments, " ") : "Warehouse", s, set);
							p.setPersistent(true);
							p.setPlotType(PlotType.WAREHOUSE);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Warehouse created in " + set.getName() + "!");
						}
						break;
					case "STALL": case "MARKETSTALL":
						if(set == null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "A stall cannot be created outside of a settlement.");
							return true;
						} else {
							Plot p = new Plot(args.length >= 3 ? Utils.join(arguments, " ") : "Stall", s, set);
							p.setPersistent(true);
							p.setPlotType(PlotType.MARKETSTALL);
							SelectionManager.clear(player);
							Civilizations.getSelectedProtections().put((Player) sender, p);
							sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Market stall created in " + set.getName() + "!");
						}
						break;
					case "FIELD": case "CROPFIELD": case "FARMLAND":
						long requiredArea = 80;
						long maxArea = 128 * 128;
						if(s.getArea() < requiredArea){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This area is too small for a field. Required: " + requiredArea + "  m� ("+s.getArea()+")" );
							return true;
						} else if(s.getArea() > maxArea){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This area is too big for a single field. Max: " + maxArea + " m� ("+s.getArea()+")");
						}
						if(set != null){
							sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This field is too close to " + set.getName());
							return true;
						}
						InfluenceMap map = Civilizations.getInfluenceMap(s.getWorld());
						if(map != null){
							set = (Settlement) map.getInfluentSiteAt(s.getLocation());
							if(set != null){
								if(!ProtectionManager.hasPermission(PermissionType.MANAGE_PLOTS, set, player, true)){
									sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "You do not have the permission to manage plots in " + ((InfluentSite) set).getRegion().getName()+ ".");
									return true;
								}
							}
							
						}
						Plot p = new Plot(args.length >= 3 ? Utils.join(arguments, " ") : "Field", s, null);
						p.setPersistent(true);
						p.setPlotType(PlotType.CROPFIELD);
						p.setOwner(player);
						SelectionManager.clear(player);
						Civilizations.getSelectedProtections().put(player, p);
						sender.sendMessage(Civilizations.messageHeader + ChatColor.GREEN + "Field created!");
						break;
					default:
						sender.sendMessage(Civilizations.messageHeader + ChatColor.RED + "This is not a valid plot type. Try House, Warehouse, Stall, Field.");
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
