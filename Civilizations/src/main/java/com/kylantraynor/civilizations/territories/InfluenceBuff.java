package com.kylantraynor.civilizations.territories;

public class InfluenceBuff {
	private InfluenceType type;
	private int duration = 1; // in days
	private float buff;
	private String label;
	
	public InfluenceBuff(InfluenceType t, float b, String l){
		this(t, b, l, 1);
	}

	public InfluenceBuff(InfluenceType t, float b, String l, int i) {
		type = t;
		buff = b;
		label = l;
		duration = i;
	}

	public InfluenceType getType() {
		return type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
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
