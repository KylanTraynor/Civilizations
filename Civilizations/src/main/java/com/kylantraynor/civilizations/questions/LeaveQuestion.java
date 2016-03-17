package com.kylantraynor.civilizations.questions;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;

public class LeaveQuestion extends Question{
	
	private Player asked;
	private Group group;

	public LeaveQuestion(Group group, Player player) {
		super(new FancyMessage("Are you sure you want to leave " + group.getName() + "?\n").color(ChatColor.GREEN).then("YES").color(ChatColor.GOLD).command("/civilizationsanswer YES").
				then(" - ").color(ChatColor.GRAY).then("NO").color(ChatColor.GOLD).command("/civilizationsanswer NO"), player);
		this.asked = player;
		this.group = group;
	}
	
	@Override
	public void answer(String answer){
		if(answer.equalsIgnoreCase("YES")){
			group.removeMember(asked);
			asked.sendMessage(ChatColor.GREEN + "You left " + group.getName());
		} else {
			asked.sendMessage(ChatColor.GREEN + "You stay in " + group.getName());
		}
	}
	
	@Override
	public boolean validate(String answer){
		if(answer.equalsIgnoreCase("YES") ||
				answer.equalsIgnoreCase("NO")) return true;
		return false;
	}
	
	@Override
	public String getInvalidMessage(){
		return ChatColor.RED + "Please answer Yes or No.";
	}
}
