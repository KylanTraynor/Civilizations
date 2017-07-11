package com.kylantraynor.civilizations.selection;

import org.bukkit.Location;
import org.bukkit.World;

import com.kylantraynor.civilizations.shapes.Hull;

public class HullSelection extends Hull implements Selection {

	public HullSelection(){
		super();
	}
	
	public HullSelection(Location l){
		super(l);
	}
	
	public void addPoint(Location l){
		if(getVertices().size() > 0){
			if(getVertices().get(0).getWorld() != l.getWorld()) return;
		}
		super.addPoint(l);
	}
	
	@Override
	public boolean isValid() {
		World w = null;
		for(Location l : getVertices()){
			if(w == null) w = l.getWorld();
			if(w != l.getWorld()){
				return false;
			}
		}
		return true;
	}

	@Override
	public String getValidityReason() {
		return "";
	}

}
