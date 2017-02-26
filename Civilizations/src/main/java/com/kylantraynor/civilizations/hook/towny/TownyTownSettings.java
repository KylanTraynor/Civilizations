package com.kylantraynor.civilizations.hook.towny;

import java.io.File;
import java.util.UUID;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Builder;
import com.kylantraynor.civilizations.settings.SettlementSettings;

public class TownyTownSettings extends SettlementSettings{
	
	private Builder builder;
	
	public Builder getBuilder(){
		if(builder !=null) return builder;
		try{
			String ids = this.getString("Builder");
			if(ids == null) return null;
			UUID id = UUID.fromString(ids);
			return Builder.get(id);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void setBuilder(Builder builder){
		this.set("Builder", builder.getSettings().getUniqueId().toString());
		this.builder = builder;
	}
	
	public void save(){
		File f = new File(Civilizations.getTownyTownsDirectory(), getName() + ".yml");
		save(f);
	}
	
}
