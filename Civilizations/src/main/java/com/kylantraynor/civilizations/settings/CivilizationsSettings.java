package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class CivilizationsSettings extends YamlConfiguration {
	
	private int mergeDistanceSquared = 0;
	private Instant taxationDate;
	private boolean hasChanged = true;
	
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
			mergeDistanceSquared = getSettlementMergeDistanceSquared() * getSettlementMergeDistanceSquared();
		}
		return mergeDistanceSquared;
	}
	
	public void setSettlementMergeDistance(int distance){
		this.set("Settlements.MergeDistance", distance);
		mergeDistanceSquared = distance * distance;
		setChanged(true);
	}
	
	public void setChanged(boolean changed){
		this.hasChanged = changed;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
}
