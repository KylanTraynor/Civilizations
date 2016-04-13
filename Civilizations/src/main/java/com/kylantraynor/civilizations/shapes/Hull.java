package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Hull extends Shape {
	
	private int precision = 50;
	private List<Location> points = new ArrayList<Location>();
	
	private List<Location> cachedHullPoints;
	private Double cachedMaxDistanceFromCenter;
	
	private boolean maxDistanceHasChanged = true;
	private boolean verticesHaveChanged = true;

	public Hull(Location location) {
		super(location);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location[] getBlockLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block[] getBlockSurface() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addPoint(Location location){
		points.add(location);
		setChanged(true);
	}
	
	public void addPoints(List<Location> list){
		for(Location l : list){
			points.add(l);
		}
		setChanged(true);
	}
	
	public Location getMassCenter(){
		int totalX = 0;
		int totalZ = 0;
		int totalY = 0;
		for(Location l : points){
			totalX += l.getBlockX();
			totalZ += l.getBlockZ();
			totalY += l.getBlockY();
		}
		return new Location(getLocation().getWorld(), totalX / points.size(), totalY / points.size(), totalZ / points.size());
	}
	
	public double getMaxDistanceFromCenter(){
		if(maxDistanceHasChanged || cachedMaxDistanceFromCenter == null){
			cachedMaxDistanceFromCenter = 0.0;
			Location center = getMassCenter();
			for(Location l : points){
				if(l.distance(center) > cachedMaxDistanceFromCenter){
					cachedMaxDistanceFromCenter = l.distance(center);
				}
			}
			maxDistanceHasChanged = false;
		}
		return cachedMaxDistanceFromCenter;
	}
	
	public List<Location> get2DVertices(int y){
		if(verticesHaveChanged  || cachedHullPoints == null){
			cachedHullPoints = new ArrayList<Location>();
			
			for(double angle = 0; angle < Math.PI * 2; angle += (Math.PI * 2) / precision){
				double x = Math.cos(angle) * getMaxDistanceFromCenter();
				double z = Math.sin(angle) * getMaxDistanceFromCenter();
				Location onCircle = new Location(getLocation().getWorld(), x, y, z);
				Location closest = null;
				for(Location l : points){
					if(closest == null){
						closest = l.clone();
						closest.setY(y);
						continue;
					} else {
						Location temp = l.clone();
						temp.setY(y);
						if(onCircle.distance(temp) < onCircle.distance(closest)){
							closest = temp;
						}
					}
				}
				cachedHullPoints.add(closest);
			}
			verticesHaveChanged = false;
		}
		return cachedHullPoints;
	}
	
	public void setChanged(boolean changed){
		maxDistanceHasChanged = true;
		verticesHaveChanged = true;
	}
	
	public boolean hasChanged(){
		if(maxDistanceHasChanged) return true;
		if(verticesHaveChanged) return true;
		return false;
	}
	
	@Override
	int getMinX() {
		Integer min = null;
		for(Location l : points){
			if(min == null){
				min = l.getBlockX();
			} else if (l.getBlockX() < min){
				min = l.getBlockX();
			}
		}
		return min;
	}

	@Override
	int getMinY() {
		Integer min = null;
		for(Location l : points){
			if(min == null){
				min = l.getBlockY();
			} else if (l.getBlockY() < min){
				min = l.getBlockY();
			}
		}
		return min;
	}

	@Override
	int getMinZ() {
		Integer min = null;
		for(Location l : points){
			if(min == null){
				min = l.getBlockZ();
			} else if (l.getBlockZ() < min){
				min = l.getBlockZ();
			}
		}
		return min;
	}

	@Override
	int getMaxX() {
		Integer max = null;
		for(Location l : points){
			if(max == null){
				max = l.getBlockX();
			} else if (l.getBlockX() > max){
				max = l.getBlockX();
			}
		}
		return max;
	}

	@Override
	int getMaxY() {
		Integer max = null;
		for(Location l : points){
			if(max == null){
				max = l.getBlockY();
			} else if (l.getBlockY() > max){
				max = l.getBlockY();
			}
		}
		return max;
	}

	@Override
	int getMaxZ() {
		Integer max = null;
		for(Location l : points){
			if(max == null){
				max = l.getBlockZ();
			} else if (l.getBlockZ() > max){
				max = l.getBlockZ();
			}
		}
		return max;
	}

	@Override
	public boolean intersect(Shape s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distance(Location location) {
		double distance = getMassCenter().distance(location);
		for(Location l : points){
			distance = Math.min(l.distance(location), distance);
		}
		return distance;
	}
}
