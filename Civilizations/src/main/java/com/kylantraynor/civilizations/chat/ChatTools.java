package com.kylantraynor.civilizations.chat;

import org.bukkit.ChatColor;

public class ChatTools {
	
	static String delimiter = ".oOo.__________________________________________________.oOo.";
	
	public static String formatTitle(String title, ChatColor color){
		if(title == null) title = "";
		if(color == null) color = ChatColor.GRAY;
		String line = "" + delimiter;
		int pivot = line.length() / 2;
		String center = ".[ " + color + title + ChatColor.GRAY + " ].";
		String out = ChatColor.GRAY + line.substring(0, Math.max(0, (pivot - center.length() / 2)));
		out += center + line.substring(pivot + center.length() / 2);
		return out;
	}
	
	public static String getDelimiter(){
		return "" + delimiter.replaceFirst("______", "");
	}
	
}
