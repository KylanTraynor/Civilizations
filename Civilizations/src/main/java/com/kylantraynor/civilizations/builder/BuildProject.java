package com.kylantraynor.civilizations.builder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.util.MaterialAndData;
import com.kylantraynor.civilizations.util.Util;

public class BuildProject {
	
	public static final int FlipX = 0;
	public static final int FlipY = 1;
	public static final int FlipZ = 2;
	
	private Location location;
	private Blueprint blueprint;
	private int currentY;
	private int currentX;
	private int currentZ;
	private boolean done;
	private boolean setAir;
	private int rotation;
	private int flip;
	
	private List<MaterialAndData> skippables = new ArrayList<MaterialAndData>();
	
	public BuildProject(Location l, Blueprint bp, boolean setAir){
		this.location = l;
		this.blueprint = bp;
		this.setAir = setAir;
		currentX = 0;
		currentY = 0;
		currentZ = 0;
		rotation = 0;
		done = false;
	}
	
	public MaterialAndData getNext(){
		if(done == true) return null;
		return blueprint.getDataAt(currentX, currentY, currentZ);
	}
	
	public boolean buildNext() {
		if(done == true) return false;
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getType() == getNext().getMaterial() && l.getBlock().getData() == getNext().getData()){
			increment();
			return false;
		} else if(setAir && getNext().getMaterial() == Material.AIR){
			l.getBlock().breakNaturally();
			l.getWorld().playSound(l, Util.getBreakSoundFromMaterial(getNext().getMaterial()), 1, 1);
			increment();
			return false;
		} else if(getNext().getMaterial() != Material.AIR) {
			l.getBlock().breakNaturally();
			l.getBlock().setType(getNext().getMaterial());
			l.getBlock().setData(getNext().getData());
			l.getWorld().playSound(l, Util.getPlaceSoundFromMaterial(getNext().getMaterial()), 1, 1);
		}
		
		increment();
		return true;
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

	public Blueprint getBlueprint() {
		return blueprint;
	}

	public int getCurrentX() {
		return currentX;
	}
	
	public int getCurrentY() {
		return currentY;
	}
	
	public int getCurrentZ() {
		return currentZ;
	}

	public Location getLocation() {
		return location;
	}

	public Object getRotation() {
		return rotation % 4;
	}

	public boolean setAir() {
		return setAir;
	}

	public void setCurrent(int cx, int cy, int cz) {
		currentX = cx;
		currentY = cy;
		currentZ = cz;
	}

	public void setRotation(int r) {
		rotation = r % 4;
	}

	public boolean trySkipNext() {
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getType() == getNext().getMaterial() && l.getBlock().getData() == getNext().getData()){
			increment();
			return true;
		} else if (isSkippable(getNext())){
			increment();
			return true;
		}
		return false;
	}
	
	public boolean isSkippable(MaterialAndData mad){
		ItemStack is = mad.toItemStack();
		for(MaterialAndData m : skippables){
			if(m.itemIsSimilar(is)) return true;
		}
		return false;
	}

	public List<MaterialAndData> getSkippables() {
		return skippables;
	}
	
	public void skip(MaterialAndData mad){
		skippables.add(mad);
	}

	public boolean nextRequiresSupply() {
		MaterialAndData mad = getNext();
		if(mad.requiresSupply()){
			Location l = location.clone().add(currentX, currentY, currentZ);
			MaterialAndData block = MaterialAndData.getFrom(l.getBlock());
			if(block.isSimilar(mad)){
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean buildInstead(MaterialAndData replacement) {
		if(done == true) return false;
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getType() == replacement.getMaterial() && l.getBlock().getData() == replacement.getData()){
			increment();
			return false;
		} else if(setAir && replacement.getMaterial() == Material.AIR){
			l.getWorld().playSound(l, Util.getBreakSoundFromMaterial(l.getBlock().getType()), 1, 1);
			l.getBlock().breakNaturally();
			increment();
			return false;
		} else if(replacement.getMaterial() != Material.AIR) {
			l.getBlock().breakNaturally();
			l.getBlock().setType(replacement.getMaterial());
			l.getBlock().setData(replacement.getData());
			l.getWorld().playSound(l, Util.getPlaceSoundFromMaterial(replacement.getMaterial()), 1, 1);
		}
		
		increment();
		return true;
	}
}
