package com.kylantraynor.civilizations.territories;

import org.bukkit.entity.Player;

public class PlayerMoveData {
	private Region regionFrom;
	private Region regionTo;
	private Player player;
	private InfluenceMap map;
	public PlayerMoveData(Player p, Region from, Region to, InfluenceMap map){
		this.player = p;
		this.regionFrom = from;
		this.regionTo = to;
		this.map = map;
	}
	
	public Region getFrom() { return regionFrom; }
	public Region getTo() { return regionTo; }
	public Player getPlayer() { return player; }
	public InfluenceMap getMap() { return map; }
	
	public boolean changedRegion(){
		return regionFrom != regionTo;
	}
}
