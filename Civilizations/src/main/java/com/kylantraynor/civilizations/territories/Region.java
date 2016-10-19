package com.kylantraynor.civilizations.territories;

import org.dynmap.markers.Marker;

import com.kylantraynor.civilizations.groups.Nation;
import com.kylantraynor.civilizations.groups.NationMember;
import com.kylantraynor.voronoi.VCell;
import com.kylantraynor.voronoi.VectorXZ;
import com.kylantraynor.voronoi.Voronoi;

public class Region {
	
	private InfluentSite site;
	private String name;
	
	public Region(InfluentSite site){
		this.site = site;
	}
	
	public VCell getCell(Voronoi voronoi){
		return voronoi.getCellAt(new VectorXZ(site.getX(), site.getZ()));
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
