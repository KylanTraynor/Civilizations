package com.kylantraynor.civilizations.territories;

import java.util.UUID;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.settings.HonorificTitleSettings;

public class HonorificTitle extends Group {
	public String getName() {
		return getSettings().getName();
	}
	
	public void initSettings(){
		setSettings(new HonorificTitleSettings());
	}

	public HonorificTitleSettings getSettings(){
		return (HonorificTitleSettings) super.getSettings();
	}
	
	public HonorificTitle(String name){
		super();
		getSettings().setName(name);
	}

	public static HonorificTitle get(UUID id) {
		for(Group g : Group.getList()){
			if(g instanceof HonorificTitle && g.getUniqueId().equals(id)){
				return (HonorificTitle) g;
			}
		}
		return null;
	}
}
