package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class CompositeShape extends Shape{
	
	Shape shape1;
	Shape shape2;
	String operation;
	Location[] cachedLocations;
	
	public CompositeShape(Shape shape1, Shape shape2, String string){
		super(new Location(shape1.getLocation().getWorld(), (shape1.getLocation().getBlockX() + shape2.getLocation().getBlockX())/2,
				(shape1.getLocation().getBlockY() + shape2.getLocation().getBlockY()) / 2,
				(shape1.getLocation().getBlockZ() + shape2.getLocation().getBlockZ()) / 2));
		this.shape1 = shape1;
		this.shape2 = shape2;
		this.operation = string;
	}

	@Override
	int getWidth() {
		return getMaxX() - getMinX();
	}

	@Override
	int getHeight() {
		return getMaxY() - getMinY();
	}

	@Override
	int getLength() {
		return getMaxZ() - getMinZ();
	}

	@Override
	int getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	boolean isInside(double x, double y, double z) {
		switch(operation){
		case "UNION":
			if(shape1.isInside(x, y, z) || shape2.isInside(x, y ,z)){
				return true;
			} else {
				return false;
			}
		case "INTERSECTION":
			if(shape1.isInside(x, y, z) && shape2.isInside(x, y, z)){
				return true;
			} else {
				return false;
			}
		case "DIFFERENCE":
			if(shape1.isInside(x, y, z) && !shape2.isInside(x, y, z)){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return operation + "({" + shape1.toString() + "}, {" + shape2.toString() + "})";
	}

	@Override
	public Location[] getBlockLocations() {
		if(cachedLocations != null){
			return cachedLocations;
		}
		switch(operation){
		case "UNION":
			List<Location> list = new ArrayList<Location>();
			for(Location l : shape1.getBlockLocations()){
				if(!shape2.isInside(l)){
					list.add(l);
				}
			}
			for(Location l : shape2.getBlockLocations()){
				list.add(l);
			}
			cachedLocations = list.toArray(new Location[list.size()]);
			return cachedLocations;
		case "INTERSECTION":
			List<Location> list1 = new ArrayList<Location>();
			for(Location l : shape1.getBlockLocations()){
				if(shape2.isInside(l)){
					list1.add(l);
				}
			}
			cachedLocations = list1.toArray(new Location[list1.size()]);
			return cachedLocations;
		case "DIFFERENCE":
			List<Location> list2 = new ArrayList<Location>();
			for(Location l : shape1.getBlockLocations()){
				if(!shape2.isInside(l)){
					list2.add(l);
				}
			}
			cachedLocations = list2.toArray(new Location[list2.size()]);
			return cachedLocations;
		}
		return null;
	}

	@Override
	public Block[] getBlockSurface() {
		// NOT IMPLEMENTED
		return shape1.getBlockSurface();
	}

	@Override
	int getMinX() {
		return Math.min(shape1.getMinX(), shape2.getMinX());
	}

	@Override
	int getMinY() {
		return Math.min(shape1.getMinY(), shape2.getMinY());
	}

	@Override
	int getMinZ() {
		return Math.min(shape1.getMinZ(), shape2.getMinZ());
	}

	@Override
	int getMaxX() {
		return Math.max(shape1.getMaxX(), shape2.getMaxX());
	}

	@Override
	int getMaxY() {
		return Math.max(shape1.getMaxY(), shape2.getMaxY());
	}

	@Override
	int getMaxZ() {
		return Math.max(shape1.getMaxZ(), shape2.getMaxZ());
	}

	@Override
	public boolean intersect(Shape s) {
		// TODO Auto-generated method stub
		return false;
	}
}
