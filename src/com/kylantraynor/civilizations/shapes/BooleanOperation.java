package com.kylantraynor.civilizations.shapes;

import java.util.List;

import org.bukkit.Location;

public class BooleanOperation {
	static Shape union(Shape shape1, Shape shape2){
		int blockCount = 0;
		boolean atLeastOneBlockIntersects = false;
		boolean oneIsFullyInsideTwo = false;
		boolean twoIsFullyInsideOne = false;
		Location[] blocks = shape1.getBlockLocations();
		for(Location l : blocks){
			if(shape2.isInside(l)){
				atLeastOneBlockIntersects = true;
				blockCount++;
			}
		}
		if(blockCount == blocks.length){
			oneIsFullyInsideTwo = true;
		}
		if(!oneIsFullyInsideTwo && atLeastOneBlockIntersects){
			blockCount = 0;
			Location[] blocks2 = shape2.getBlockLocations();
			for(Location l : blocks2){
				if(shape1.isInside(l)){
					blockCount++;
				}
			}
			if(blockCount == blocks2.length){
				twoIsFullyInsideOne = true;
			}
		}
		if(oneIsFullyInsideTwo){
			return shape2;
		}
		if(twoIsFullyInsideOne){
			return shape1;
		}
		if(atLeastOneBlockIntersects){
			return new CompositeShape(shape1, shape2, "UNION");
		}
		return new CompositeShape(shape1, shape2, "UNION");
	}
	
	static Shape intersection(Shape shape1, Shape shape2){
		int blockCount = 0;
		boolean atLeastOneBlockIntersects = false;
		boolean oneIsFullyInsideTwo = false;
		boolean twoIsFullyInsideOne = false;
		Location[] blocks = shape1.getBlockLocations();
		for(Location l : blocks){
			if(shape2.isInside(l)){
				atLeastOneBlockIntersects = true;
				blockCount++;
			}
		}
		if(blockCount == blocks.length){
			oneIsFullyInsideTwo = true;
		}
		if(!oneIsFullyInsideTwo && atLeastOneBlockIntersects){
			blockCount = 0;
			Location[] blocks2 = shape2.getBlockLocations();
			for(Location l : blocks2){
				if(shape1.isInside(l)){
					blockCount++;
				}
			}
			if(blockCount == blocks2.length){
				twoIsFullyInsideOne = true;
			}
		}
		if(oneIsFullyInsideTwo){
			return shape1;
		}
		if(twoIsFullyInsideOne){
			return shape2;
		}
		if(atLeastOneBlockIntersects){
			return new CompositeShape(shape1, shape2, "INTERSECTION");
		}
		return null;
	}
	
	static Shape difference(Shape shape1, Shape shape2){
		int blockCount = 0;
		boolean atLeastOneBlockIntersects = false;
		boolean oneIsFullyInsideTwo = false;
		boolean twoIsFullyInsideOne = false;
		Location[] blocks = shape1.getBlockLocations();
		for(Location l : blocks){
			if(shape2.isInside(l)){
				atLeastOneBlockIntersects = true;
				blockCount++;
			}
		}
		if(blockCount == blocks.length){
			oneIsFullyInsideTwo = true;
		}
		if(!oneIsFullyInsideTwo && atLeastOneBlockIntersects){
			blockCount = 0;
			Location[] blocks2 = shape2.getBlockLocations();
			for(Location l : blocks2){
				if(shape1.isInside(l)){
					blockCount++;
				}
			}
			if(blockCount == blocks2.length){
				twoIsFullyInsideOne = true;
			}
		}
		if(oneIsFullyInsideTwo){
			return null;
		}
		if(twoIsFullyInsideOne){
			return new CompositeShape(shape1, shape2, "DIFFERENCE");
		}
		if(atLeastOneBlockIntersects){
			return new CompositeShape(shape1, shape2, "UNION");
		}
		return null;
	}
}