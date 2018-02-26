package com.kylantraynor.civilizations.questions;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.protection.PermissionType;

public class GroupQuestion extends Question{

	private Group group;
	private PermissionType permission;

	public GroupQuestion(FancyMessage string, Group g, PermissionType perm) {
		super(string, null);
		this.group = g;
		this.permission = perm;
	}
	
	public void ask(){
		this.group.sendMessage(getQuestion(), permission);
		QuestionsHandler.registerGroupQuestion(this);
	}
	
	public Group getGroup(){return group;}
	
	public PermissionType getPermission(){return permission;}
	
	public void initiateAnswerRoutine(Player sender, String[] s){
		StringBuilder builder = new StringBuilder();
		for(String string : s){
			builder.append(string + " ");
		}
		if(validate(builder.toString().trim())){
			QuestionsHandler.killQuestion(this.group);
			answer(sender, builder.toString().trim());
		} else {
			sender.sendMessage(getInvalidMessage());
		}
	}

}
