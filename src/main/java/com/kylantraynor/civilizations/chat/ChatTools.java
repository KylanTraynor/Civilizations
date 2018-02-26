package com.kylantraynor.civilizations.chat;

import org.bukkit.ChatColor;

public class ChatTools {
	
	static private String delimiter = ".oOo.__________________________________________________.oOo.";
	
	/**
	 * Centers the Title in the chatbox and surrounds it with decoration.
	 * @param title The title to center.
	 * @param color The color of the title part.
	 * @return The formated title.
	 * 
	 * @author Shade (xshade.ca)
	 * @author Modified by KylanTraynor
	 */
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
