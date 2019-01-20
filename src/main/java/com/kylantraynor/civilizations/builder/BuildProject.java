package com.kylantraynor.civilizations.builder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.utils.MaterialAndData;
import com.kylantraynor.civilizations.utils.Utils;

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
	
	private List<Material> skippables = new ArrayList<>();
	
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

    /**
     * Get the next {@link BlockData} pending to be built.
     * @return {@link BlockData}
     */
	public BlockData getNext(){
		if(done) return null;
		return blueprint.getDataAt(currentX, currentY, currentZ);
	}

    /**
     * Build the next {@link BlockData} and move on to the next one.
     * @return {@code true} if some material was needed, {@code false} otherwise
     */
	public boolean buildNext() {
		if(done) return false;
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getBlockData().matches(getNext())){
			increment();
			return false;
		} else if(setAir && getNext().getMaterial() == Material.AIR){
			l.getBlock().breakNaturally();
			l.getWorld().playSound(l, Utils.getBreakSoundFromMaterial(getNext().getMaterial()), 1, 1);
			increment();
			return false;
		} else if(getNext().getMaterial() != Material.AIR) {
			l.getBlock().breakNaturally();
			l.getBlock().setBlockData(getNext());
			l.getWorld().playSound(l, Utils.getPlaceSoundFromMaterial(getNext().getMaterial()), 1, 1);
		}
		
		increment();
		return true;
	}

    /**
     * Increment the current indices to target the next block.
     */
	private void increment() {
		if(done) return;
		currentX += 1;
		if(currentX >= blueprint.getWidth()){
			currentX = 0;
			currentZ += 1;
			if(currentX >= blueprint.getDepth()){
				currentZ = 0;
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

    /**
     * Attempt to skip the next {@link BlockData}.
     * @return {@code true} if the block was skipped, {@code false} otherwise
     */
	public boolean trySkipNext() {
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getBlockData().matches(getNext())){
			increment();
			return true;
		} else if (isSkippable(getNext().getMaterial())){
			increment();
			return true;
		}
		return false;
	}

    /**
     * Can the given {@link Material} be skipped?
     * @param mat {@link Material}
     * @return {@code true} if the {@link Material} can be skipped, {@code false} otherwise
     */
	public boolean isSkippable(Material mat){
		for(Material m : skippables){
			if(mat == m) return true;
		}
		return false;
	}

    /**
     * Get the list of {@link Material Materials} that will be skipped.
     * @return List of {@link Material}
     */
	public List<Material> getSkippables() {
		return skippables;
	}

    /**
     * Add the given {@link Material} to the list of materials that can be skipped.
     * @param mat {@link Material} to skip
     */
	public void skip(Material mat) {
		skippables.add(mat);
	}

    /**
     * Check if the next {@link BlockData} requires any supplies to be built.
     * <br/>
     * For example, if a dirt block needs to be placed, but dirt is already there, this function will return false.
     * @return {@code true} if supplies are required, {@code false} otherwise
     */
	public boolean nextRequiresSupply() {
		BlockData bd = getNext();
		if(Utils.requireSupplies(bd)){
			Location l = location.clone().add(currentX, currentY, currentZ);
			return !(l.getBlock().getType() == bd.getMaterial());
		} else return false;
	}

    /**
     * Build the given {@link BlockData} instead of the one from {@link #getNext()}, and move on.
     * @param replacement {@link BlockData}
     * @return {@code true} if the replacement required supplies, {@code false} otherwise
     */
	public boolean buildInstead(BlockData replacement) {
		if(done) return false;
		Location l = location.clone().add(currentX, currentY, currentZ);
		if(l.getBlock().getBlockData().matches(replacement)){
			increment();
			return false;
		} else if(setAir && replacement.getMaterial() == Material.AIR){
			l.getWorld().playSound(l, Utils.getBreakSoundFromMaterial(l.getBlock().getType()), 1, 1);
			l.getBlock().breakNaturally();
			increment();
			return false;
		} else if(replacement.getMaterial() != Material.AIR) {
			l.getBlock().breakNaturally();
			l.getBlock().setBlockData(replacement);
			l.getWorld().playSound(l, Utils.getPlaceSoundFromMaterial(replacement.getMaterial()), 1, 1);
		}
		
		increment();
		return true;
	}
}
