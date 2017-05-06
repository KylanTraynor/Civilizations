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
}
