package com.kylantraynor.civilizations.protection;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LockpickSession {

	private Player player;
	private Block block;
	private int stage;
	
	public LockpickSession(Player player, Block block){
		this.player = player;
		this.block = block;
		this.stage = 5;
	}

	public Player getPlayer(){
		return player;
	}
	
	public Block getBlock() {
		return block;
	}
	
}
