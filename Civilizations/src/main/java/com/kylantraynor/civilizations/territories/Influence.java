package com.kylantraynor.civilizations.territories;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kylantraynor.civilizations.util.MutableDouble;

// TODO Add this to influent sites instead of a simple float number.

public class Influence {
	private Map<InfluenceType, Double> influences = new HashMap<InfluenceType, Double>();
	private List<InfluenceBuff> buffs = new ArrayList<InfluenceBuff>();
	
	/**
	 * Gets the influence of the given type.
	 * @param type
	 * @return
	 */
	public double getInfluence(InfluenceType type){
		if(influences.containsKey(type))
			return influences.get(type);
		else 
			return 0.0;
	}
	
	/**
	 * Sets the influence of the given type.
	 * @param type
	 * @param value
	 */
	public void setInfluence(InfluenceType type, double value){
		influences.put(type, value);
	}
	
	/**
	 * Gets the total influence.
	 * @return
	 */
	public double getTotalInfluence(){
		double result = 1;
		for(Double d : influences.values()){
			if(d != null) result += d;
		}
		return result > 1 ? result : 1;
	}
	
	/**
	 * Adds the buffs to the influence. (Should only be called once a day or so)
	 */
	public void update(){
		for(InfluenceBuff buff : buffs.toArray(new InfluenceBuff[buffs.size()])){
			double d = getInfluence(buff.getType());
			influences.put(buff.getType(), d + buff.getBuff());
			if(buff.getUntil().isBefore(Instant.now())){
				buffs.remove(buff);
			}
		}
	}
	
	
	public void addBuff(InfluenceType type, float buff, String label, Instant until){
		buffs.add(new InfluenceBuff(type, buff, label, until));
	}
}