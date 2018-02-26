package com.kylantraynor.civilizations.territories;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InfluenceBuff {
	private InfluenceType type;
	private Instant until = Instant.now().plus(1, ChronoUnit.DAYS); // in days
	private float buff;
	private String label;
	
	public InfluenceBuff(InfluenceType t, float b, String l){
		this(t, b, l, 1);
	}

	public InfluenceBuff(InfluenceType t, float b, String l, int i) {
		this(t,b,l,Instant.now().plus(i, ChronoUnit.DAYS));
	}
	
	public InfluenceBuff(InfluenceType t, float b, String l, Instant i){
		type = t;
		buff = b;
		label = l;
		until = i;
	}

	public InfluenceType getType() {
		return type;
	}

	public Instant getUntil() {
		return until;
	}

	public void setUntil(Instant until) {
		this.until = until;
	}

	public float getBuff() {
		return buff;
	}

	public void setBuff(float buff) {
		this.buff = buff;
	}

	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString(){
		return String.format("%s|%f|%s|\"%s\"", type.toString(), buff, until.toString(), label);
	}
	
	public static InfluenceBuff parse(String s){
		if(!s.matches(".+\\|.+\\|.+\\|\".+\"")) return null;
		String[] sss = s.split("\"");
		String label = sss[1];
		String[] ss = s.split("\\|");
		InfluenceType type = InfluenceType.valueOf(ss[0]);
		float buff = Float.parseFloat(ss[1]);
		Instant until = Instant.parse(ss[2]);
		return new InfluenceBuff(type, buff, label, until);
	}
}