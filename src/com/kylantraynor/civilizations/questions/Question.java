package com.kylantraynor.civilizations.questions;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Question {
	
	private FancyMessage question;
	private CommandSender sender;

	public Question(FancyMessage string, CommandSender sender) {
		this.question = string;
		this.sender = sender;
	}

	public void answer(String answer) {
		
	}
	
	public void ask(){
		this.question.send(sender);
		QuestionsHandler.registerQuestion(this);
	}

	public boolean validate(String answer) {
		return true;
	}

	public String getInvalidMessage() {
		return ChatColor.RED + "Invalid Answer";
	}

	public CommandSender getSender() {
		return sender;
	}
	
	public FancyMessage getQuestion(){
		return question;
	}
	
	public void initiateAnswerRoutine(String[] s){
		StringBuilder builder = new StringBuilder();
		for(String string : s){
			builder.append(string + " ");
		}
		if(validate(builder.toString().trim())){
			QuestionsHandler.killQuestion(this.sender);
			answer(builder.toString().trim());
		} else {
			this.sender.sendMessage(getInvalidMessage());
		}
	}

	public void answer(Player p, String answer) {
		// TODO Auto-generated method stub
		
	}

}
