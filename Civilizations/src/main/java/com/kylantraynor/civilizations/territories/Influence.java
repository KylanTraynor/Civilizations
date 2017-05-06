package com.kylantraynor.civilizations.territories;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kylantraynor.civilizations.util.MutableDouble;

// TODO Add the buffs.
// TODO Add this to influent sites instead of a simple float number.

public class Influence {
	private Map<InfluenceType, MutableDouble> influences = new HashMap<InfluenceType, MutableDouble>();
	private List<InfluenceBuff> buffs = new ArrayList<InfluenceBuff>();
	
	/**
	 * Gets the influence of the given type.
	 * @param type
	 * @return
	 */
	public double getInfluence(InfluenceType type){
		if(influences.containsKey(type))
			return influences.get(type).value;
		else 
			return 0.0;
	}
	
	/**
	 * Sets the influence of the given type.
	 * @param type
	 * @param value
	 */
	public void setInfluence(InfluenceType type, double value){
		influences.put(type, new MutableDouble(value));
	}
	
	/**
	 * Gets the total influence.
	 * @return
	 */
	public double getTotalInfluence(){
		float result = 1f;
		for(MutableDouble f : influences.values()){
			if(f != null) result += f.value;
		}
		return result > 1 ? result : 1f;
	}
	
	/**
	 * Adds the buffs to the influence. (Should only be called once a day or so)
	 */
	private void update(){
		for(InfluenceBuff buff : buffs.toArray(new InfluenceBuff[buffs.size()])){
			influences.put(buff.getType(), new MutableDouble(getInfluence(buff.getType()) + buff.getBuff()));
			if(buff.getUntil().isBefore(Instant.now())){
				buffs.remove(buff);
			}
		}
	}
	
	
	public void addBuff(InfluenceType type, float buff, String label, Instant until){
		buffs.add(new InfluenceBuff(type, buff, label, until));
	}
}