package com.kylantraynor.civilizations.groups;

import org.bukkit.ChatColor;

import mkremins.fanciful.civilizations.FancyMessage;

public class GroupAction {
	private String name;
	private String tooltip;
	private String command;
	private ActionType type;
	private boolean enabled;
	
	public GroupAction(String name, String description, ActionType type, String command, boolean enabled){
		
		this.name = name;
		this.tooltip = description;
		this.type = type;
		this.command = command;
		this.enabled = enabled;
		
	}
	
	public FancyMessage addTo(FancyMessage fm){
		fm.then(this.name);
		if(this.enabled){
			fm.color(ChatColor.GOLD);
		} else {
			fm.color(ChatColor.GRAY);
		}
		fm.tooltip(this.tooltip);
		if(command != null && this.enabled){
			if(this.type == ActionType.SUGGEST){
				fm.suggest(command);
			} else {
				fm.command(command);
			}
		}
		return fm;
	}
}
