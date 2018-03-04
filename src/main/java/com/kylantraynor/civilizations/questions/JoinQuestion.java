package com.kylantraynor.civilizations.questions;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.protection.PermissionType;

public class JoinQuestion extends GroupQuestion{
	
	private Player asking;

	public JoinQuestion(Group group, Player player) {
		super(new FancyMessage(player.getName() + " would like to join " + group.getName() + ".?\n").color(group.getChatColor()).then("YES").color(ChatColor.GOLD).command("/civilizationsanswer " + group.getIdentifier().toString()+ " YES").
				then(" - ").color(ChatColor.GRAY).then("NO").color(ChatColor.GOLD).command("/civilizationsanswer " + group.getIdentifier().toString() + " NO"), group, PermissionType.INVITE);
		this.asking = player;
	}
	
	@Override
	public void answer(Player p, String answer){
		if(answer.equalsIgnoreCase("" + getGroup().getIdentifier().toString() + " YES")){
			getGroup().addMember(asking);
			asking.sendMessage(getGroup().getChatColor() + p.getName() + " added you to " + getGroup().getName() + ".");
			getGroup().sendMessage(p.getName() + " has added " + asking.getName() + " to " + getGroup().getName() + ".", null);
		} else {
			asking.sendMessage(ChatColor.RED + "Your request to join " + getGroup().getName() + " has been denied.");
			getGroup().sendMessage(asking.getName() + " has been denied to join " + getGroup().getName() + ".", null);
		}
	}
	
	@Override
	public boolean validate(String answer){
		if(answer.equalsIgnoreCase("" + getGroup().getIdentifier().toString() + " YES") ||
				answer.equalsIgnoreCase("" + getGroup().getIdentifier().toString() + " NO")) return true;
		return false;
	}
	
	@Override
	public String getInvalidMessage(){
		return ChatColor.RED + "Please answer Yes or No.";
	}
}
