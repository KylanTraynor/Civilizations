package com.kylantraynor.civilizations.groups.settlements.plots;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.shapes.Shape;

public class Keep extends Plot{

	public Keep(String name, Shape shape, Settlement settlement) {
		super(name, shape, settlement);
	}
	
	public Keep(String name, List<Shape> shapes, Settlement settlement) {
		super(name, shapes, settlement);
	}

	/**
	 * Loads Keep from its configuration file.
	 * @param cf
	 * @return Group
	 */
	public static Keep load(YamlConfiguration cf, HashMap<String, Settlement> settlements){
		if(cf == null) return null;
		Instant creation;
		String name = cf.getString("Name");
		String settlementPath = cf.getString("SettlementPath");
		String shapes = cf.getString("Shape");
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.log("WARNING", "Couldn't find creation date for a keep. Replacing it by NOW.");
		}
		
		Keep g = new Keep(name, Plot.parseShapes(shapes), settlements.get(settlementPath));
		g.setCreationDate(creation);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			g.getMembers().add(UUID.fromString((cf.getString("Members."+i))));
			i+=1;
		}
		
		return g;
	}
	/**
	 * Saves the keep to its file.
	 * @return true if the group has been saved, false otherwise.
	 */
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Name", getName());
		if(getSettlement() != null){
			fc.set("SettlementPath", getSettlement().getFile().getAbsolutePath());
		}
		fc.set("Shape", getShapesString());
		fc.set("Creation", getCreationDate().toString());
		
		int i = 0;
		for(UUID id : getMembers()){
			fc.set("Members." + i, id.toString());
			i += 1;
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}