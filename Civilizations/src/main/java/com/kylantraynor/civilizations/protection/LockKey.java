package com.kylantraynor.civilizations.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.kylantraynor.civilizations.Civilizations;

public class LockKey {
	private static Map<String, LockKey> loadedKeys = new HashMap<String, LockKey>();
	private UUID id;
	private String name;
	
	public static LockKey get(UUID id){
		LockKey key = loadedKeys.get(id.toString());
		if(key == null){
			key = loadKey(id);
		}
		return key;
	}

	private static LockKey loadKey(UUID id) {
		if(Civilizations.useDatabase){
			return Civilizations.currentInstance.getDatabase().getLockKey(id);
		}
		return null;
	}
}