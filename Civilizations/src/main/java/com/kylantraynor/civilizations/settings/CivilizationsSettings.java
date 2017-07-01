package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class CivilizationsSettings extends YamlConfiguration {
	
	private int mergeDistanceSquared = 0;
	private Instant taxationDate;
	private boolean hasChanged = true;
	private String wikiUrl;
	
	public UUID getBlueprintId(String name){
		if(this.contains("UUIDConversions.Blueprints." + name.toUpperCase())){
			try{
				return UUID.fromString(this.getString("UUIDConversions.Blueprints." + name.toUpperCase()));
			} catch (IllegalArgumentException e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	public void setBlueprintId(String name, UUID id){
		this.set("UUIDConversions.Blueprints." + name, id.toString());
		this.setChanged(true);
	}
	
	public Instant getTaxationDate(){
		if(taxationDate != null) return taxationDate;
		String s = this.getString("Economy.TaxationDate");
		if(s == null){
			setTaxationDate(Instant.now());
		} else {
			try{
				taxationDate = Instant.parse(s);
			} catch (Exception e){
				e.printStackTrace();
				setTaxationDate(Instant.now());
			}
		}
		return taxationDate;
	}
	
	public void setTaxationDate(Instant newTaxationDate){
		taxationDate = newTaxationDate;
		this.set("Economy.TaxationDate", taxationDate.toString());
		this.setChanged(true);
	}
	
	public int getSettlementMergeDistance(){
		return this.getInt("Settlements.MergeDistance", 25);
	}
	
	public int getSettlementMergeDistanceSquared(){
		if(mergeDistanceSquared == 0){
			mergeDistanceSquared = getSettlementMergeDistance() * getSettlementMergeDistance();
		}
		return mergeDistanceSquared;
	}
	
	public void setSettlementMergeDistance(int distance){
		this.set("Settlements.MergeDistance", distance);
		mergeDistanceSquared = distance * distance;
		setChanged(true);
	}
	
	public String getWikiUrl(){
		if(wikiUrl == null){
			wikiUrl = this.getString("General.WikiRoot", null);
		}
		return wikiUrl;
	}
	
	public void setWikiUrl(String url){
		this.set("General.WikiRoot", url);
		wikiUrl = url;
		setChanged(true);
	}
	
	public void setChanged(boolean changed){
		this.hasChanged = changed;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}

	public List<String> getColonizableWorlds() {
		List<String> list = (List<String>) this.getList("General.ColonizableWorlds");
		if(list == null){
			list = new ArrayList<String>();
			list.add("world");
			setColonizableWorlds(list);
		}
		return list;
	}
	
	public void setColonizableWorlds(List<String> worlds){
		this.set("General.ColonizableWorlds", worlds);
	}

	public String getSQLiteFilename() {
		return this.getString("SQLite.Filename", "civs");
	}
}
