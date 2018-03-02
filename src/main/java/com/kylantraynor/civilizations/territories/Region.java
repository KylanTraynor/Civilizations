package com.kylantraynor.civilizations.territories;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Nation;
import com.kylantraynor.civilizations.groups.NationMember;
import com.kylantraynor.voronoi.VCell;

public class Region {
	
	private InfluentSite site;
	private String name;
	
	public Region(InfluentSite site){
		this.site = site;
	}
	
	public VCell getCell(InfluenceMap map){
		VCell cell = map.getCell(site);
		if(cell == null)
			Civilizations.log("SEVERE", "Couldn't find voronoi cell!");
		return cell;
	}
	
	public String getName(){
		if(name != null) return name;
		return "Domain of " + site.getName();
	}
	
	public Nation getNation(){
		if(site instanceof NationMember){
			((NationMember) site).getNation();
		}
		return null;
	}

	public InfluentSite getSite() {
		return site;
	}
}
