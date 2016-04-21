package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Sphere extends Shape {
	
	int radius;

	public Sphere(Location location, int radius) {
		super(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		this.radius = radius;
	}

	@Override
	int getWidth() {
		return this.radius * 2;
	}

	@Override
	int getHeight() {
		return this.radius * 2;
	}

	@Override
	int getLength() {
		return this.radius * 2;
	}

	@Override
	int getVolume() {
		return (int) ((4.0/3.0) * (Math.PI * this.radius * this.radius * this.radius));
	}

	@Override
	int getArea() {
		return (int)(Math.PI * this.radius * this.radius);
	}

	@Override
	boolean isInside(double x, double y, double z) {
		x = x - this.getLocation().getX();
		y = y - this.getLocation().getY();
		z = z - this.getLocation().getZ();
		double distance = Math.sqrt(x * x + y * y + z * z); 
		if(distance <= this.radius) return true;
		return false;
	}
	
	@Override
	public String toString() {
		String result = "SPHERE;" + this.getLocation().getWorld().getName() + ";" +
				this.getLocation().getX() + ";" + this.getLocation().getY() +
				";" + this.getLocation().getZ() + ";" + getRadius();
		return result;
	}
	
	public static Sphere parse(String str){
		String[] components = str.split(";");
		World w = Bukkit.getWorld(components[1]);
		double x = Double.parseDouble(components[2]);
		double y = Double.parseDouble(components[3]);
		double z = Double.parseDouble(components[4]);
		int radius = Integer.parseInt(components[5]);
		return new Sphere(new Location(w, x, y, z), radius);
	}

	public int getRadius() {
		return this.radius;
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					if(isInside(getLocation().getBlockX() + x, getLocation().getBlockY() + y, getLocation().getBlockZ() + z)){
						list.add(getLocation().clone().add(x, y, z));
					}
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		List<Block> list = new ArrayList<Block>();
		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					Location l = new Location(getLocation().getWorld(), getLocation().getBlockX() + x, getLocation().getBlockY() + y, getLocation().getBlockZ() + z);
					if(getLocation().distance(l) <= radius && getLocation().distance(l) > (radius - 1)){
						list.add(getLocation().clone().add(x, y, z).getBlock());
					}
				}
			}
		}
		return list.toArray(new Block[list.size()]);
	}
	
	public boolean intersect(Sphere s){
		int thisX = this.getLocation().getBlockX();
		int thisY = this.getLocation().getBlockY();
		int thisZ = this.getLocation().getBlockZ();
		int sX = s.getLocation().getBlockX();
		int sY = s.getLocation().getBlockY();
		int sZ = s.getLocation().getBlockZ();
		double distance = Math.sqrt((thisX - sX) * (thisX - sX) +
				(thisY - sY) * (thisY - sY) +
				(thisZ - sZ) * (thisZ - sZ));
		
		return distance < (this.getRadius() + s.getRadius());
	}
	
	public boolean intersect(Prism p){
		
		int thisX = this.getLocation().getBlockX();
		int thisY = this.getLocation().getBlockY();
		int thisZ = this.getLocation().getBlockZ();
		
		int x = Math.max(p.getMinX(), Math.min(thisX, p.getMaxX()));
		int y = Math.max(p.getMinY(), Math.min(thisY, p.getMaxY()));
		int z = Math.max(p.getMinZ(), Math.min(thisZ, p.getMaxZ()));
		
		double distance = Math.sqrt((x - thisX) * (x - thisX)+
				(y - thisY) * (y - thisY)+
				(z - thisZ) * (z - thisZ));
		
		return distance < this.getRadius();
	}

	@Override
	public int getMinX() {
		return getLocation().getBlockX() - getRadius();
	}

	@Override
	public int getMinY() {
		return getLocation().getBlockY() - getRadius();
	}

	@Override
	public int getMinZ() {
		return getLocation().getBlockZ() - getRadius();
	}

	@Override
	public int getMaxX() {
		return getLocation().getBlockX() + getRadius();
	}

	@Override
	public int getMaxY() {
		return getLocation().getBlockY() + getRadius();
	}

	@Override
	public int getMaxZ() {
		return getLocation().getBlockZ() + getRadius();
	}

	@Override
	public boolean intersect(Shape s) {
		if(s instanceof Sphere) return intersect((Sphere)s);
		if(s instanceof Prism) return intersect((Prism)s);
		return false;
	}

	@Override
	public double distance(Location l) {
		return Math.max(getLocation().distance(l) - getRadius(), 0);
	}
}
