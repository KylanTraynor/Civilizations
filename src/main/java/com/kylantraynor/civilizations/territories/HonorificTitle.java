package com.kylantraynor.civilizations.territories;

import java.util.UUID;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.settings.HonorificTitleSettings;
import com.kylantraynor.civilizations.utils.Identifier;

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

	public static HonorificTitle get(Identifier id) {
		for(Group g : Group.getList()){
			if(g instanceof HonorificTitle && g.getIdentifier().equals(id)){
				return (HonorificTitle) g;
			}
		}
		return null;
	}
}
