package com.kylantraynor.civilizations.territories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Add the buffs.
// TODO Add this to influent sites instead of a simple float number.

public class Influence {
	private Map<InfluenceType, Float> influences = new HashMap<InfluenceType, Float>();
	private List<InfluenceBuff> buffs = new ArrayList<InfluenceBuff>();
	
	/**
	 * Gets the influence of the given type.
	 * @param type
	 * @return
	 */
	public Float getInfluence(InfluenceType type){
		if(influences.containsKey(type))
			return influences.get(type);
		else 
			return 0f;
	}
	
	/**
	 * Gets the total influence.
	 * @return
	 */
	public Float getTotalInfluence(){
		float result = 1f;
		for(Float f : influences.values()){
			if(f != null) result += f;
		}
		return result > 1 ? result : 1f;
	}
	
	/**
	 * Adds the buffs to the influence. (Should only be called once a day or so)
	 */
	private void update(){
		for(InfluenceBuff buff : buffs.toArray(new InfluenceBuff[buffs.size()])){
			influences.put(buff.getType(), influences.get(buff.getType()) + buff.getBuff());
			if(buff.getDuration() == 1)
				buffs.remove(buff);
			else
				buff.setDuration(buff.getDuration() - 1);
		}
	}
	
	
	public void addBuff(InfluenceType type, float buff, String label, int duration){
		for(InfluenceBuff b : buffs){
			if(b.getLabel().equals(label) && b.getType() == type && b.getBuff() != buff){
				buffs.add(new InfluenceBuff(type, buff, label, duration));
			}
		}
	}
}