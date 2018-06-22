package com.kylantraynor.civilizations.territories;

public interface InfluentSite extends InfluentEntity{
	float getX();
	float getZ();
	String getName();
	Region getRegion();
}