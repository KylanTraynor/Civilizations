package com.kylantraynor.civilizations.territories;

import java.util.HashMap;
import java.util.Map;

// TODO Add the buffs.
// TODO Add this to influent sites instead of a simple float number.

public class Influence {
	private Map<InfluenceType, Float> influences = new HashMap<InfluenceType, Float>();
	
	/**
	 * Gets the influence of the given type.
	 * @param type
	 * @return
	 */
	public Float getInfluence(InfluenceType type){
		return influences.get(type);
	}
	
	/**
	 * Gets the total influence.
	 * @return
	 */
	public Float getInfluence(){
		float result = 1f;
		for(Float f : influences.values()){
			if(f != null) result += f;
		}
		return result > 1 ? result : 1f;
	}
}