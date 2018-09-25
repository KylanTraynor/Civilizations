package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.economy.TaxBase;
import com.kylantraynor.civilizations.economy.TaxInfo;

public class CivilizationsSettings extends YamlConfiguration {
	
	private int mergeDistanceSquared = 0;
	private Instant taxationDate;
	private boolean hasChanged = true;
	private String wikiUrl;

	private static String DIAMONDVALUE = "Economy.Values.Diamond";
	private static String GOLDNUGGETVALUE = "Economy.Values.GoldNugget";
	private static String IRONNUGGETVALUE = "Economy.Values.IronNugget";

	private static String DEBUGCONSOLEINFO = "Debugging.ConsoleInfo";
	private static String DEBUGCLEARBUILDPROJECTS = "Debugging.ClearBuildProjectsOnRestart";

	public boolean getDebug(){
		if(this.contains(DEBUGCONSOLEINFO)){
		    return this.getBoolean(DEBUGCONSOLEINFO);
        } else {
		    setDebug(false);
		    return false;
        }
	}

	public void setDebug(boolean debug){
	    this.set(DEBUGCONSOLEINFO, debug);
    }

    public boolean getClearBuildProjectsOnRestart(){
        if(this.contains(DEBUGCLEARBUILDPROJECTS)){
            return this.getBoolean(DEBUGCLEARBUILDPROJECTS);
        } else {
            this.set(DEBUGCLEARBUILDPROJECTS, false);
            return false;
        }
    }
	
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

	public int getDiamondValue(){
	    return this.getInt(DIAMONDVALUE, 10000);
    }

	public int getGoldNuggetValue(){
        return this.getInt(GOLDNUGGETVALUE, 100);
    }

    public int getIronNuggetValue(){
	    return this.getInt(IRONNUGGETVALUE, 1);
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
		this.setChanged(true);
	}

	public String getSQLiteFilename() {
		return this.getString("SQLite.Filename", "civs");
	}
	
	public TaxInfo getTaxInfo(String tax) {
		String root = "Economy.Taxes.";
		if(this.contains(root + tax)){
			double value = this.getDouble(root + tax + ".value", 0);
			boolean isPercent = this.getBoolean(root + tax + ".isPercent", false);
			TaxBase base = TaxBase.valueOf(this.getString(root + tax + ".base", "FromBalance"));
			return new TaxInfo(tax, base, value, isPercent);
		} else {
			return null;
		}
	}

	public void setTaxInfo(String tax, TaxBase base, double value,
			boolean isPercent) {
		
		String root = "Economy.Taxes.";
		this.set(root + tax + ".value", value);
		this.set(root + tax + ".isPercent", isPercent);
		this.set(root + tax + ".base", base.toString());
		
		this.setChanged(true);
	}
	
	public Set<String> getTaxes() {
		String root = "Economy.Taxes";
		return this.getConfigurationSection(root).getKeys(false);
	}
}
