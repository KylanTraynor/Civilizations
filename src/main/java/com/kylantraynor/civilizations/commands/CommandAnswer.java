package com.kylantraynor.civilizations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.questions.GroupQuestion;
import com.kylantraynor.civilizations.questions.QuestionsHandler;

public class CommandAnswer implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(args.length > 1 && Group.get(Integer.parseInt(args[0])) != null){
			Group g = Group.get(Integer.parseInt(args[0]));
			((GroupQuestion) QuestionsHandler.getQuestion(g)).initiateAnswerRoutine((Player) sender, args);
		} else {
			QuestionsHandler.getQuestion(sender).initiateAnswerRoutine(args);
		}
		return false;
	}

}
