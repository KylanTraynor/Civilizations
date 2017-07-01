package com.kylantraynor.civilizations.database;

import java.util.logging.Level;

import com.kylantraynor.civilizations.Civilizations;

public class Error {
	public static void execute (Civilizations plugin, Exception ex){
		plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement: ", ex);
	}
	
	public static void close(Civilizations plugin, Exception ex){
		plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
	}
}
