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
		addBuff(new InfluenceBuff(type, buff, label, until));
	}
	
	private void addBuff(InfluenceBuff buff) {
		buffs.add(buff);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("TOTAL 5");
		sb.append("\n" + InfluenceType.LEGITIMACY.toString()+":"+getInfluence(InfluenceType.LEGITIMACY));
		sb.append("\n" + InfluenceType.MILITARY.toString()+":"+getInfluence(InfluenceType.MILITARY));
		sb.append("\n" + InfluenceType.DIPLOMATIC.toString()+":"+getInfluence(InfluenceType.DIPLOMATIC));
		sb.append("\n" + InfluenceType.CULTURAL.toString()+":"+getInfluence(InfluenceType.CULTURAL));
		sb.append("\n" + InfluenceType.COMMERCIAL.toString()+":"+getInfluence(InfluenceType.COMMERCIAL));
		sb.append("\nBUFFS");
		for(InfluenceBuff b : buffs){
			sb.append("\n" + b.toString());
		}
		return sb.toString();
	}
	
	public static Influence parse(String string) {
		String[] lines = string.split("\n");
		int types = Integer.parseInt(lines[0].split("\\s")[1]);
		Influence result = new Influence();
		for(int i = 1; i < 1 + types; i++){
			String[] ss = lines[i].split("\\:");
			InfluenceType t = InfluenceType.valueOf(ss[0]);
			double value = Double.parseDouble(ss[1]);
			result.setInfluence(t, value);
		}
		for(int i = 1 + types + 1; i < lines.length; i++){
			result.addBuff(InfluenceBuff.parse(lines[i]));
		}
		return result;
	}
}