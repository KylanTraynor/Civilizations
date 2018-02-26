package com.kylantraynor.civilizations.menus;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class GetInputStringPrompt extends StringPrompt{

	private String reason;
	private GroupMenu menu;
	private Object argument;
	
	public GetInputStringPrompt(GroupMenu gm ,String r, Object arg){
		this.reason = r;
		this.menu = gm;
		this.argument = arg;
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String answer) {
		menu.textInputResult(answer.split(" ")[0], reason, argument);
		return END_OF_CONVERSATION;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		switch(reason.toUpperCase()){
		case "RANK_NAMING":
			return ChatColor.AQUA + "Type the new name of the Rank or type " + ChatColor.GOLD + "CANCEL" + ChatColor.AQUA + ". (Underscores will be replaced with spaces)";
		case "RANK_NEW":
			return ChatColor.AQUA + "Type the name of the new Rank or type " + ChatColor.GOLD + "CANCEL" + ChatColor.AQUA + ". (Underscores will be replaced with spaces)";
		}
		return "Please type below.";
	}

}
