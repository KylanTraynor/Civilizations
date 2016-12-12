package com.kylantraynor.civilizations.territories;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.kylantraynor.civilizations.settings.HonorificTitleSettings;

public class HonorificTitle {
	
	private static Map<UUID, HonorificTitle> all = new HashMap<UUID, HonorificTitle>();
	private HonorificTitleSettings settings = new HonorificTitleSettings();
	
	public String getName() {
		return getSettings().getName();
	}

	public HonorificTitleSettings getSettings(){
		return settings;
	}
	
	public HonorificTitle(String name){
		getSettings().setName(name);
		all.put(getSettings().getUniqueId(), this);
	}
	
	public HonorificTitle(HonorificTitleSettings settings){
		this.settings = settings;
		all.put(getSettings().getUniqueId(), this);
	}

	public static HonorificTitle get(UUID id) {
		return all.get(id);
	}
}
