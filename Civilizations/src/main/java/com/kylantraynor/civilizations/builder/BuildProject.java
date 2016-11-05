package com.kylantraynor.civilizations.builder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BuildProject {
	private Location location;
	private Blueprint blueprint;
	private int currentY;
	private int currentX;
	private int currentZ;
	private boolean done;
	private boolean setAir;
	
	public BuildProject(Location l, Blueprint bp, boolean setAir){
		this.location = l;
		this.blueprint = bp;
		this.setAir = setAir;
		currentX = 0;
		currentY = 0;
		currentZ = 0;
		done = false;
	}
	
	public ItemStack getNext(){
		if(done == true) return null;
		return blueprint.getDataAt(currentX, currentY, currentZ);
	}
	
	public void buildNext() {
		if(done == true) return;
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(setAir && getNext().getType() == Material.AIR){
			l.getBlock().breakNaturally();
		} else if(getNext().getType() != Material.AIR) {
			l.getBlock().breakNaturally();
			l.getBlock().setType(getNext().getType());
			l.getBlock().setData(getNext().getData().getData());
		}
		
		increment();
	}

	private void increment() {
		if(done == true) return;
		currentZ += 1;
		if(currentZ >= blueprint.getDepth()){
			currentZ = 0;
			currentX += 1;
			if(currentX >= blueprint.getWidth()){
				currentX = 0;
				currentY += 1;
				if(currentY >= blueprint.getHeight()){
					done = true;
				}
			}
		}
	}

	public boolean isDone() {
		return done;
	}

}
