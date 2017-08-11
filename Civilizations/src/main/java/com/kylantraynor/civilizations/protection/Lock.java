package com.kylantraynor.civilizations.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.kylantraynor.civilizations.Civilizations;

public class Lock {
	private static Map<Location, Lock> loadedLocks = new HashMap<Location, Lock>();
	private int level = 1;
	private UUID key = null;
	
	public static Lock getAt(Block b){
		Lock l = loadedLocks.get(b.getLocation());
		if(l == null){
			l = loadLock(b);
		}
		return l;
	}
	
	public static Lock loadLock(Block b){
		if(Civilizations.useDatabase){
			return Civilizations.currentInstance.getDatabase().getLock(b.getLocation());
		}
		return null;
	}
}
