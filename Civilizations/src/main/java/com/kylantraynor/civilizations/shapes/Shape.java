package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Civilizations;

public abstract class Shape implements Visualizable{
	int width;
	int height;
	int length;
	List<Player> players = new ArrayList<Player>();
	private Location location;
	
	public Shape(Location location){
		this.location = location;
	}
	
	public abstract boolean intersect(Shape s);
	public abstract double distanceSquared(Location location);
	public abstract double distanceSquared(Shape shape);
	public double getMinX() {return getMinBlockX(); }
	public double getMinY() {return getMinBlockY(); }
	public double getMinZ() {return getMinBlockZ(); }
	public double getMaxX() {return getMaxBlockX() + 1; }
	public double getMaxY() {return getMaxBlockY() + 1; }
	public double getMaxZ() {return getMaxBlockZ() + 1; }
	public abstract int getMinBlockX();
	public abstract int getMinBlockY();
	public abstract int getMinBlockZ();
	public abstract int getMaxBlockX();
	public abstract int getMaxBlockY();
	public abstract int getMaxBlockZ();
	public abstract int getBlockWidth();
	public abstract int getBlockHeight();
	public abstract int getBlockLength();
	public double getWidth() {return getMaxX() - getMinX(); }
	public double getHeight() {return getMaxY() - getMinY(); }
	public double getLength() {return getMaxZ() - getMinZ(); }
	public abstract long getVolume();
	public abstract long getArea();
	public abstract boolean isInside(double x, double y, double z);
	public abstract String toString();
	public abstract Location[] getBlockLocations();
	public abstract Block[] getBlockSurface();
	
	public boolean isInside(Location location){
		if(location.getWorld().equals(this.location.getWorld())){
			return isInside(location.getX(), location.getY(), location.getZ());
		} else {
			return false;
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * Gets the list of players this shape is displayed to.
	 * @return
	 */
	public List<Player> getPlayers(){ return players; }
	/**
	 * Sets the list of players this shape is displayed to.
	 * @param players
	 */
	public void setPlayers(List<Player> players){ this.players = players; }
	/**
	 * Adds a player to the list of players this shape is displayed to.
	 * @param player
	 * @return
	 */
	public boolean addPlayer(Player player){
		if(players.contains(player) && player != null){
			return false;
		} else {
			return players.add(player);
		}
	}
	/**
	 * Removes a player from the list of players this shape is displayed to.
	 * @param player
	 * @return
	 */
	public boolean removePlayer(Player player){
		if(player != null){
			return players.remove(player);
		} else {
			return false;
		}
	}
	
	public static Shape parse(String s){
		if(s.split(";").length > 1){
			switch(s.split(";")[0].toUpperCase()){
			case "PRISM":
				return Prism.parse(s);
			case "SPHERE":
				return Sphere.parse(s);
			}
			return null;
		} else {
			return null;
		}
	}
	
	public void show(Player player) {
		if(addPlayer(player)){
			for(Block b : getBlockSurface()){
				if(b.getLocation().distanceSquared(player.getLocation()) <= 10000 && !walkThroughBlock(b)){
					player.sendBlockChange(b.getLocation(), Material.GOLD_BLOCK, (byte) 0);
				}
			}
		}
	}

	public void hide(Player player) {
		if(removePlayer(player)){
			for(Block b : getBlockSurface()){
				if(b.getLocation().distanceSquared(player.getLocation()) <= 10000 && !walkThroughBlock(b)){
					player.sendBlockChange(b.getLocation(), b.getLocation().getBlock().getType(), b.getLocation().getBlock().getData());
				}
			}
		}
	}
	
	public boolean walkThroughBlock(Block block){
		if(block.getType() == Material.AIR) return true;
		if(block.getType() == Material.DIRT) return false;
		if(block.getType() == Material.LONG_GRASS) return true;
		if(block.getType() == Material.RAILS) return true;
		if(block.getType() == Material.CAKE_BLOCK) return true;
		if(block.getType() == Material.CHEST) return true;
		if(block.getType() == Material.TRAPPED_CHEST) return true;
		if(block.getType() == Material.COBBLE_WALL) return true;
		return false;
	}
	
	public double distance(Location l){
		return Math.sqrt(distanceSquared(l));
	}
	public double distance(Shape s){
		return Math.sqrt(distanceSquared(s));
	}

	public List<Location> getVertices() {
		List<Location> result = new ArrayList<Location>();
		result.add(new Location(this.getLocation().getWorld(), getMinX(), getMinY(), getMinZ()));
		result.add(new Location(this.getLocation().getWorld(), getMaxX(), getMinY(), getMinZ()));
		result.add(new Location(this.getLocation().getWorld(), getMinX(), getMaxY(), getMinZ()));
		result.add(new Location(this.getLocation().getWorld(), getMaxX(), getMaxY(), getMinZ()));
		result.add(new Location(this.getLocation().getWorld(), getMinX(), getMinY(), getMaxZ()));
		result.add(new Location(this.getLocation().getWorld(), getMaxX(), getMinY(), getMaxZ()));
		result.add(new Location(this.getLocation().getWorld(), getMinX(), getMaxY(), getMaxZ()));
		result.add(new Location(this.getLocation().getWorld(), getMaxX(), getMaxY(), getMaxZ()));
		return result;
	}
	
}
