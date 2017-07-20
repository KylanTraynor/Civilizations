package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
	public int getBlockWidth() {
		return getMaxBlockX() - getMinBlockX();
	}

	@Override
	public int getBlockHeight() {
		return getMaxBlockY() - getMinBlockY();
	}

	@Override
	public int getBlockLength() {
		return getMaxBlockZ() - getMinBlockZ();
	}

	@Override
	public long getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInside(double x, double y, double z) {
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
		return operation.toUpperCase() + "({" + shape1.toString() + "}, {" + shape2.toString() + "})";
	}
	
	static public CompositeShape parse(String s){
		s = s.trim();
		int start = 0;
		int end = 0;
		start = s.indexOf('(');
		end = s.lastIndexOf(')');
		if(start == 0 || end == 0) return null;
		// get >OPERATION<({Stuff}, {Stuff})
		String operation = s.substring(0, start - 1);
		
		Shape shape1 = null;
		Shape shape2 = null;
		
		String s1 = s.substring(start + 1, end - 1).trim();
		
		if(s1.startsWith("{")){
			// get {>Stuff<}, {Stuff}
			String subShape = "";
			Stack<Character> stack = new Stack<Character>();
			int subShapeEnd = 0;
			for(int i = 0; i < s1.length(); i++){
				switch(s1.charAt(i)){
				case '{':
					stack.push('{');
					break;
				case '}':
					if(stack.peek().equals('}')){
						stack.pop();
						subShapeEnd = i;
						if(stack.empty()) break;
					}
				}
			}
			subShape = s1.substring(1, subShapeEnd - 1);
			shape1 = Shape.parse(subShape);
			
			// get {Stuff}, {>Stuff<}
			String subShape2 = "";
			stack = new Stack<Character>();
			int subShape2Begin = 0;
			int subShape2End = 0;
			for(int i = subShapeEnd + 1; i < s1.length(); i++){
				switch(s1.charAt(i)){
				case '{':
					if(stack.empty()){
						subShape2Begin = i;
					}
					stack.push('{');
					break;
				case '}':
					if(stack.peek().equals('}')){
						stack.pop();
						subShape2End = i;
						if(stack.empty()) break;
					}
				}
			}
			
			subShape2 = s1.substring(subShape2Begin + 1, subShape2End - 1);
			shape2 = Shape.parse(subShape2);
		}
		if(shape1 == null || shape2 == null) return null;
		return new CompositeShape(shape1, shape2, operation);
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
	public int getMinBlockX() {
		return Math.min(shape1.getMinBlockX(), shape2.getMinBlockX());
	}

	@Override
	public int getMinBlockY() {
		return Math.min(shape1.getMinBlockY(), shape2.getMinBlockY());
	}

	@Override
	public int getMinBlockZ() {
		return Math.min(shape1.getMinBlockZ(), shape2.getMinBlockZ());
	}

	@Override
	public int getMaxBlockX() {
		return Math.max(shape1.getMaxBlockX(), shape2.getMaxBlockX());
	}

	@Override
	public int getMaxBlockY() {
		return Math.max(shape1.getMaxBlockY(), shape2.getMaxBlockY());
	}

	@Override
	public int getMaxBlockZ() {
		return Math.max(shape1.getMaxBlockZ(), shape2.getMaxBlockZ());
	}

	@Override
	public double distanceSquared(Shape s){
		switch(operation.toUpperCase()){
		case "UNION":
			return Math.min(shape1.distanceSquared(s), shape2.distanceSquared(s));
		case "INTERSECTION":
			break;
		case "DIFFERENCE":
			break;
		}
		return 0;
	}
	
	@Override
	public boolean intersect(Shape s) {
		switch(operation.toUpperCase()){
		case "UNION":
			return (shape1.intersect(s) || shape2.intersect(s));
		case "INTERSECTION":
			break;
		case "DIFFERENCE":
			break;
		}
		return false;
	}

	@Override
	public double distanceSquared(Location l) {
		switch(operation.toUpperCase()){
		case "UNION":
			return Math.min(shape1.distanceSquared(l), shape2.distanceSquared(l));
		case "INTERSECTION":
			break;
		case "DIFFERENCE":
			break;
		}
		return 0;
	}

	@Override
	public double[] getVerticesX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getVerticesZ() {
		// TODO Auto-generated method stub
		return null;
	}
}
