package com.kylantraynor.civilizations.territories;

public interface InfluentSite extends InfluentEntity{
	public abstract float getX();
	public abstract float getZ();
	public abstract String getName();
	public abstract Region getRegion();
}