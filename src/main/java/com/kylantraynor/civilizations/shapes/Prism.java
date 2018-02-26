package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Prism extends Shape {
	
	public Prism(Location location, int width, int height, int length) {
		super(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		this.width = (width > 0 ? width : -width);
		this.height = (height > 0 ? height : -height);
		this.length = (length > 0 ? length : -length);
	}

	@Override
	public int getBlockWidth() {
		return this.width - 1;
	}

	@Override
	public int getBlockHeight() {
		return this.height - 1;
	}

	@Override
	public int getBlockLength() {
		return this.length - 1;
	}

	@Override
	public long getVolume() {
		return getBlockWidth() * getBlockHeight() * getBlockLength();
	}

	@Override
	public long getArea() {
		return getBlockWidth() * getBlockLength();
	}
	
	Location[] getCorners(){
		Location c1 = getLocation().clone();
		Location c2 = getLocation().clone().add(getBlockWidth(), getBlockHeight(), getBlockLength());
		return new Location[]{c1, c2};
	}

	@Override
	public boolean isInside(double x, double y, double z) {
		if(x >= getMinX() && x < getMaxX()){
			if(y >= getMinY() && y < getMaxY()){
				if(z >= getMinZ() && z < getMaxZ()){
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
				";" + getLocation().getZ() + ";" + (int)getWidth() + ";" +
				(int)getHeight() + ";" + (int)getLength();
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
		return new Prism(new Location(w, x, y, z), width, height, length);
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		for(int x = 0; x <= getBlockWidth(); x++){
			for(int y = 0; y <= getBlockHeight(); y++){
				for(int z = 0; z <= getBlockLength(); z++){
					list.add(getLocation().clone().add(x, y, z));
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		List<Block> list = new ArrayList<Block>();
		for(int x = 0; x <= getBlockWidth(); x++){
			for(int y = 0; y <= getBlockHeight(); y++){
				for(int z = 0; z <= getBlockLength(); z++){
					if((x == 0 || x == getBlockWidth()) || (y == 0 || y == getBlockHeight()) || (z == 0 || z == getBlockLength())){
						list.add(getLocation().clone().add(x, y, z).getBlock());
					}
				}
			}
		}
		return list.toArray(new Block[list.size()]);
	}
	
	public boolean intersect(Prism p){
		return (this.getMinX() < p.getMaxX() && this.getMaxX() > p.getMinX()) &&
				(this.getMinY() < p.getMaxY() && this.getMaxY() > p.getMinY()) &&
				(this.getMinZ() < p.getMaxZ() && this.getMaxZ() > p.getMinZ());
	}
	
	public boolean intersect(Sphere s){
		
		int sX = s.getLocation().getBlockX();
		int sY = s.getLocation().getBlockY();
		int sZ = s.getLocation().getBlockZ();
		
		double x = Math.max(this.getMinX(), Math.min(sX, this.getMaxX()));
		double y = Math.max(this.getMinY(), Math.min(sY, this.getMaxY()));
		double z = Math.max(this.getMinZ(), Math.min(sZ, this.getMaxZ()));
		
		double distanceSquared = (x - sX) * (x - sX)+
				(y - sY) * (y - sY)+
				(z - sZ) * (z - sZ);
		
		return distanceSquared < s.getRadius() * s.getRadius();
	}

	@Override
	public int getMinBlockX() {
		return getLocation().getBlockX();
	}

	@Override
	public int getMinBlockY() {
		return getLocation().getBlockY();
	}

	@Override
	public int getMinBlockZ() {
		return getLocation().getBlockZ();
	}

	@Override
	public int getMaxBlockX() {
		return getLocation().getBlockX() + getBlockWidth();
	}

	@Override
	public int getMaxBlockY() {
		return getLocation().getBlockY() + getBlockHeight();
	}

	@Override
	public int getMaxBlockZ() {
		return getLocation().getBlockZ() + getBlockLength();
	}
	
	@Override
	public double distanceSquared(Shape s) {
		if(this.intersect(s)) return 0;
		
		Location closest = new Location(s.getLocation().getWorld(),
				Math.max(s.getMinX(), Math.min(s.getMaxX(), getLocation().getX())),
				Math.max(s.getMinY(), Math.min(s.getMaxY(), getLocation().getY())),
				Math.max(s.getMinZ(), Math.min(s.getMaxZ(), getLocation().getZ())));
				
		
		return distanceSquared(closest);
	}
	
	@Override
	public boolean intersect(Shape s) {
		if(s instanceof Sphere) return intersect((Sphere)s);
		if(s instanceof Prism) return intersect((Prism)s);
		return false;
	}

	@Override
	public double distanceSquared(Location l) {
		double x = Math.max(getMinX(), Math.min(getMaxX(), l.getX()));
		double y = Math.max(getMinY(), Math.min(getMaxY(), l.getY()));
		double z = Math.max(getMinZ(), Math.min(getMaxZ(), l.getZ()));
		
		return new Location(l.getWorld(), x, y, z).distanceSquared(l);
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

	@Override
	public double[] getVerticesX() {
		double[] result = new double[4];
		result[0] = getMaxX();
		result[1] = getMaxX();
		result[2] = getMinX();
		result[3] = getMinX();
		return result;
	}

	@Override
	public double[] getVerticesZ() {
		double[] result = new double[4];
		result[0] = getMinZ();
		result[1] = getMaxZ();
		result[2] = getMaxZ();
		result[3] = getMinZ();
		return result;
	}
}