package com.kylantraynor.civilizations.territories;

public interface InfluentEntity {
	public abstract float getInfluence();
	public abstract void setInfluence(float newInfluence);
	public abstract void addInfluence(float addedInfluence);
	public abstract void removeInfluence(float removedInfluence);
}
