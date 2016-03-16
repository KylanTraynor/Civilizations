package com.kylantraynor.civilizations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.plots.Plot;

public class Settlement extends Group {
	
	public enum Type{
		CAMP,
		TOWNY
	}
	
	private List<Plot> plots = new ArrayList<Plot>();
	private Location location;
	public Type getType(){return null;}
	public String getIcon(){return null;}
	
	public Settlement(Location l){
		super();
		this.location = l;
		Cache.settlementListChanged = true;
		setChatColor(ChatColor.GRAY);
	}
	
	public List<Plot> getPlots() {return plots;}
	public void setPlots(List<Plot> plts) {this.plots = plts;}
	public boolean addPlot(Plot p){
		if(this.plots.contains(p)){
			return false;
		} else {
			this.plots.add(p);
			return true;
		}
	}
	public boolean removePlot(Plot p){
		if(this.plots.contains(p)){
			this.plots.remove(p);
			return true;
		} else {
			return false;
		}
	}
	public Location getLocation() {return location;}
	public void setLocation(Location location) {this.location = location;}
	
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public void update(){
		Civilizations.updateMap(this);
		super.update();
	}
	
	@Override
	public boolean remove(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			getProtection().hide(p);
		}
		Cache.settlementListChanged = true;
		return super.remove();
	}

	public static List<Settlement> getSettlementList() {
		return Cache.getSettlementList();
	}
	
	public boolean protects(Location l){
		return getProtection().isInside(l);
	}

	public static Settlement getAt(Location location) {
		for(Settlement s : getSettlementList()){
			if(s.protects(location)) return s;
		}
		return null;
	}
	
	public static boolean isProtected(Location l){
		for(Settlement s : getSettlementList()){
			if(s.protects(l)){
				return true;
			}
		}
		return false;
	}
	
	public static Settlement getClosest(Location l){
		Double distance = null;
		Settlement closest = null;
		for(Settlement s : getSettlementList()){
			if(distance == null){
				closest = s;
			} else if(distance > l.distance(s.getLocation())) {
				distance = l.distance(s.getLocation());
				closest = s;
			}
		}
		return closest;
	}
}
