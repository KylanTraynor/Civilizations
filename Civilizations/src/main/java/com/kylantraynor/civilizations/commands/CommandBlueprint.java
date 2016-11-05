package com.kylantraynor.civilizations.commands;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Blueprint;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.managers.CacheManager;
import com.kylantraynor.civilizations.managers.SelectionManager;
import com.kylantraynor.civilizations.selection.Selection;

public class CommandBlueprint implements CommandExecutor{
	private HashMap<UUID, Blueprint> currentBlueprints = new HashMap<UUID, Blueprint>();
	private String messageHeader = "" + ChatColor.GOLD + "[" + ChatColor.BLUE + "Blueprint" + ChatColor.GOLD + "] ";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		
		if(!(sender instanceof Player)){
			sender.sendMessage(messageHeader + ChatColor.RED + "This command must be used by a player.");
		}
		Player player = (Player) sender;
		
		if(args.length == 0){
			args = new String[]{"HELP"};
		}
		
		switch(args[0].toUpperCase()){
		case "HELP":
			player.sendMessage(messageHeader + ChatColor.RED + "Blueprints are not yet activated.");
			return true;
			
		case "SET":
			if(!SelectionManager.hasSelection(player)){
				player.sendMessage(messageHeader + ChatColor.RED + "You need to have a selection.");
				return true;
			}
			Selection s = SelectionManager.getSelection(player);
			Blueprint bp = new Blueprint();
			bp.fillDataFrom(s);
			player.sendMessage(messageHeader + ChatColor.GREEN + "Selection has been turned into a blueprint!");
			currentBlueprints.put(player.getUniqueId(), bp);
			return true;
			
		case "SAVE":
			Blueprint b = currentBlueprints.get(player.getUniqueId());
			if(b != null){
				if(args.length == 2){ b.setName(args[1]); }
				if(b.getName() == null){
					player.sendMessage(messageHeader + ChatColor.RED + "Blueprint can't be saved without a name. Please use \blueprint save [name]");
					return true;
				}
				if(b.save(new File(Civilizations.getBlueprintDirectory(), b.getName() + ".bpt"))){
					player.sendMessage(messageHeader + ChatColor.GREEN + "Blueprint " + b.getName() + " has been saved!");
				} else {
					player.sendMessage(messageHeader + ChatColor.RED + "Blueprint " + b.getName() + " couldn't be saved.");
				}
				return true;
			} else {
				player.sendMessage(messageHeader + ChatColor.RED + "You have no blueprint selected.");
				return true;
			}
			
		case "LOAD":
			if(args.length == 2){
				File f = new File(Civilizations.getBlueprintDirectory(), args[1] + ".bpt");
				if(!f.exists()){
					player.sendMessage(messageHeader + ChatColor.RED + "No file with the name " + f.getName() + " could be found.");
					return true;
				}
				Blueprint loadedBp = Blueprint.load(f);
				if(loadedBp == null){
					player.sendMessage(messageHeader + ChatColor.RED + "That blueprint can't be loaded. Ask an admin about this issue.");
					return true;
				}
				currentBlueprints.put(player.getUniqueId(), loadedBp);
				player.sendMessage(messageHeader + ChatColor.GREEN + "Blueprint " + loadedBp.getName() + " has been loaded!");
				return true;
			} else {
				player.sendMessage(messageHeader + ChatColor.RED + "You didn't give a blueprint name to load.");
				return true;
			}
			
		case "BUILD":
			boolean checkHeight = true;
			boolean setAir = true;
			for(String s1 : args){
				if(s1.equalsIgnoreCase("-h")) checkHeight = false;
				if(s1.equalsIgnoreCase("-a")) setAir = false;
				if(s1.equalsIgnoreCase("-ha") || s1.equalsIgnoreCase("-ah")){
					checkHeight = false;
					setAir = false;
				}
			}
			Selection selection = SelectionManager.getSelection(player);
			if(selection == null){
				player.sendMessage(messageHeader + ChatColor.RED + "You need to have a selection.");
				return true;
			}
			Blueprint cbp = currentBlueprints.get(player.getUniqueId());
			if(cbp == null){
				player.sendMessage(messageHeader + ChatColor.RED + "You don't have a blueprint loaded. Use /blueprint load [name]");
				return true;
			}
			if(checkHeight){
				if(selection.getWidth() < cbp.getWidth() || selection.getHeight() < cbp.getHeight() || selection.getLength() < cbp.getDepth()){
					player.sendMessage(messageHeader + ChatColor.RED + "The selection is too small to fit the blueprint.");
					int needX = Math.max(cbp.getWidth() - selection.getWidth(), 0);
					int needY = Math.max(cbp.getHeight() - selection.getHeight(), 0);
					int needZ = Math.max(cbp.getDepth() - selection.getLength(), 0);
					player.sendMessage(messageHeader + ChatColor.RED + "This blueprint needs " + needX + " blocks more in X, " + needZ + " blocks more in Z and " + needY + " blocks more in Y.");
					return true;
				}
			} else {
				if(selection.getWidth() < cbp.getWidth() || selection.getLength() < cbp.getDepth()){
					player.sendMessage(messageHeader + ChatColor.RED + "The selection is too small to fit the blueprint.");
					int needX = Math.max(cbp.getWidth() - selection.getWidth(), 0);
					int needZ = Math.max(cbp.getDepth() - selection.getLength(), 0);
					player.sendMessage(messageHeader + ChatColor.RED + "This blueprint needs " + needX + " blocks more in X and " + needZ + " blocks more in Z");
					return true;
				}
			}
			
			// TODO Check if in a settlement
			HasBuilder settlement = null;
			for(Settlement g : CacheManager.getSettlementList()){
				if(!(g instanceof HasBuilder)) continue;
				if(g.canMergeWith(selection)){
					settlement = (HasBuilder) g;
					break;
				}
			}
			if(settlement == null){
				player.sendMessage(messageHeader + ChatColor.RED + "Can't add a build project outside of a settlement.");
				return true;
			}
			
			// TODO Add to settlement buildprojects.
			if(!settlement.canBuild()){
				player.sendMessage(messageHeader + ChatColor.RED + ((Settlement)settlement).getName() + " doesn't have the requirements to set build projects.");
				return true;
			}
			
			if(settlement.addBuildProject(selection, cbp, setAir)){
				player.sendMessage(((Settlement)settlement).getChatHeader() + ChatColor.GREEN + "Build project added!");
			} else {
				player.sendMessage(((Settlement)settlement).getChatHeader() + ChatColor.RED + "Build project couldn't be added.");
			}
			return true;
		}
		player.sendMessage(messageHeader + ChatColor.RED + "Blueprints are not yet activated.");
		return false;
	}

}
