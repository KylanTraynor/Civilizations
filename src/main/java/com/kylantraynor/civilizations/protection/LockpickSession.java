package com.kylantraynor.civilizations.protection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.LockpickMenu;

public class LockpickSession {

	private Player player;
	private Block block;
	private int startStage;
	private int stage;
	private Integer[] code;
	private int lockLevel;
	
	public LockpickSession(Player player, Block block){
		this.player = player;
		this.block = block;
		this.startStage = LockManager.getLockLevel(block);
		this.lockLevel = this.startStage;
		this.stage = this.startStage;
		this.code = new Integer[stage];
		for(int i = 0; i < this.stage; i++){
			this.code[i] = (int) (Math.random() * 9);
		}
		MenuManager.openMenu(new LockpickMenu(this), player);
	}

	public Player getPlayer(){
		return player;
	}
	
	public Block getBlock() {
		return block;
	}

	public int getCodeForCurrentStage() {
		return this.code[this.stage - 1];
	}

	public int getStage() {
		return this.stage;
	}

	public void passStage() {
		if(this.stage == 1){
			LockManager.stopLockpicking(player);
			LockManager.unlock(getBlock());
		} else {
			this.stage = this.stage - 1;
		}
	}
	
	public void reset(){
		this.stage = startStage;
	}

	public void end() {
		LockManager.stopLockpicking(player);
		if(getBlock().getType() == Material.CHEST || getBlock().getType() == Material.TRAPPED_CHEST){
			BlockState state = getBlock().getState();
			if(state instanceof Chest){
				getPlayer().openInventory(((Chest) state).getInventory());
			}
		}
	}

	public int getLockLevel() {
		return lockLevel;
	}

	public void setLockLevel(int locklevel) {
		this.lockLevel = locklevel;
	}
	
}
