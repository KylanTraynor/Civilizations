package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Prism extends Shape {
	
	public Prism(Location location, int width, int height, int length) {
		super(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		this.width = width;
		this.height = height;
		this.length = length;
	}

	@Override
	int getWidth() {
		return this.width;
	}

	@Override
	int getHeight() {
		return this.height;
	}

	@Override
	int getLength() {
		return this.length;
	}

	@Override
	int getVolume() {
		return getWidth() * getHeight() * getLength();
	}

	@Override
	int getArea() {
		return getWidth() * getLength();
	}
	
	Location[] getCorners(){
		Location c1 = getLocation().clone();
		Location c2 = getLocation().clone().add(this.width, this.height, this.length);
		return new Location[]{c1, c2};
	}

	@Override
	boolean isInside(double x, double y, double z) {
		if(x >= getLocation().getBlockX() && x <= getLocation().getBlockX() + width){
			if(y >= getLocation().getBlockY() && y <= getLocation().getBlockY() + height){
				if(z >= getLocation().getBlockZ() && z <= getLocation().getBlockZ() + length){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String result = "PRISM;" + getLocation().getWorld().getName() + ";" +
				getLocation().getX() + ";" + getLocation().getY() +
				";" + getLocation().getZ() + ";" + getWidth() + ";" +
				getHeight() + ";" + getLength();
		return result;
	}
	
	public static Prism parse(String str){
		String[] components = str.split(";");
		World w = Bukkit.getWorld(components[1]);
		double x = Double.parseDouble(components[2]);
		double y = Double.parseDouble(components[3]);
		double z = Double.parseDouble(components[4]);
		int width = Integer.parseInt(components[5]);
		int height = Integer.parseInt(components[6]);
		int length = Integer.parseInt(components[7]);
		return new Prism(new Location(w, x, y, z), height, height, height);
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				for(int z = 0; z < length; z++){
					list.add(getLocation().clone().add(x, y, z));
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		List<Block> list = new ArrayList<Block>();
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				for(int z = 0; z < length; z++){
					if((x == 0 || x == width - 1) || (y == 0 || y == height - 1) || (z == 0 || z == length - 1)){
						list.add(getLocation().clone().add(x, y, z).getBlock());
					}
				}
			}
		}
		return list.toArray(new Block[list.size()]);
	}
	
	public boolean intersect(Prism p){
		return (this.getMinX() <= p.getMaxX() && this.getMaxX() >= p.getMinX()) &&
				(this.getMinY() <= p.getMaxY() && this.getMaxY() >= p.getMinY()) &&
				(this.getMinZ() <= p.getMaxZ() && this.getMaxZ() >= p.getMinZ());
	}
	
public boolean intersect(Sphere s){
		
		int sX = s.getLocation().getBlockX();
		int sY = s.getLocation().getBlockY();
		int sZ = s.getLocation().getBlockZ();
		
		int x = Math.max(this.getMinX(), Math.min(sX, this.getMaxX()));
		int y = Math.max(this.getMinY(), Math.min(sY, this.getMaxY()));
		int z = Math.max(this.getMinZ(), Math.min(sZ, this.getMaxZ()));
		
		double distance = Math.sqrt((x - sX) * (x - sX)+
				(y - sY) * (y - sY)+
				(z - sZ) * (z - sZ));
		
		return distance < s.getRadius();
	}

	@Override
	int getMinX() {
		return getLocation().getBlockX();
	}

	@Override
	int getMinY() {
		return getLocation().getBlockY();
	}

	@Override
	int getMinZ() {
		return getLocation().getBlockZ();
	}

	@Override
	int getMaxX() {
		return getLocation().getBlockX() + getWidth();
	}

	@Override
	int getMaxY() {
		return getLocation().getBlockY() + getHeight();
	}

	@Override
	int getMaxZ() {
		return getLocation().getBlockZ() + getLength();
	}
	
	@Override
	public boolean intersect(Shape s) {
		if(s instanceof Sphere) return intersect((Sphere)s);
		if(s instanceof Prism) return intersect((Prism)s);
		return false;
	}

	@Override
	public double distance(Location l) {
		double x = Math.max(getMinX(), Math.min(getMaxX(), l.getX()));
		double y = Math.max(getMinY(), Math.min(getMaxY(), l.getY()));
		double z = Math.max(getMinZ(), Math.min(getMaxZ(), l.getZ()));
		
		return new Location(l.getWorld(), x, y, z).distance(l);
	}
	
	public boolean expand(BlockFace direction, int amount){
		switch(direction){
		case UP:
			height += amount;
			return true;
		case DOWN:
			height += amount;
			getLocation().add(0, -amount, 0);
			return true;
		case EAST:
			width += amount;
			return true;
		case NORTH:
			length += amount;
			getLocation().add(0, 0, -amount);
			return true;
		case SOUTH:
			length += amount;
			return true;
		case WEST:
			width += amount;
			getLocation().add(-amount, 0, 0);
			return true;
		default:
			return false;
		}
	}
}