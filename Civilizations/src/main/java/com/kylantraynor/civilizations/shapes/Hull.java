package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.util.Util;

public class Hull extends Shape {
	
	//private int precision = 50;
	private TreeSet<Location> points = new TreeSet<Location>(getZComp());
	
	private List<Location> cachedHullPoints;
	private Double cachedMaxDistanceFromCenter;
	
	private boolean maxDistanceHasChanged = true;
	private boolean verticesHaveChanged = true;
	private double[] xVertices;
	private double[] zVertices;

	private ArrayList<Location> vertices;

	public Hull(Location location) {
		super(location);
	}

	public Hull() {
		super(null);
	}
	
	public Location getLocation(){
		if(super.getLocation() == null){
			return getMassCenter();
		}
		return super.getLocation();
	}

	/*
	private Comparator<? super Location> getYComp() {
		return (a, b) -> {
			if(a.getY() < b.getY()) return -1;
			if(a.getY() > b.getY()) return 1;
			return 0;
		};
	}
	*/
	private Comparator<? super Location> getZComp() {
		return (a, b) -> {
			if(a.getZ() < b.getZ()) return -1;
			if(a.getZ() > b.getZ()) return 1;
			if(a.getX() < b.getX()) return -1;
			if(a.getX() > b.getX()) return 1;
			return 0;
		};
	}
	
	Comparator<Location> getAngleComp(Location ref){
		return (a, b) -> {
			double dx1 = a.getX() - ref.getX();
			double dy1 = a.getZ() - ref.getZ();
			double dx2 = b.getX() - ref.getX();
			double dy2 = b.getZ() - ref.getZ();
			if(dy1 >= 0 && dy2 < 0){
				return -1; //a above, b below
			} else if(dy2 >= 0 && dy1 < 0){
				return 1; // a below, b above
			} else if(dy1 == 0 && dy2 == 0){
				// collinear and horizontal
				if(dx1 >= 0 && dx2 < 0){
					return -1;
				} else if (dx2 >=0 && dx1 < 0){
					return 1;	
				} else {
					return 0;
				}
			} else {
				double c = -ccw(ref, a, b); // both above or below
				if(c < 0) return -1;
				if(c > 0) return 1;
				return 0;
			}
			/*
			if(Math.atan2(a.getZ() - ref.getZ(), a.getX() - ref.getX()) < Math.atan2(b.getZ() - ref.getZ(), b.getX() - ref.getX())) return -1;
			if(Math.atan2(b.getZ() - ref.getZ(), b.getX() - ref.getX()) < Math.atan2(a.getZ() - ref.getZ(), a.getX() - ref.getX())) return 1;
			if(a.getX() < b.getX()) return -1;
			if(b.getX() < a.getX()) return 1;
			return 0;
			*/
		};
	}
	
	public double ccw(Location p1, Location p2, Location p3){
	    return (p2.getX() - p1.getX())*(p3.getZ() - p1.getZ()) - (p2.getZ() - p1.getZ())*(p3.getX() - p1.getX());
	}
	
	public double[] getVerticesX(){
		if(verticesHaveChanged) updateHull();
		return xVertices;
	}
	
	public double[] getVerticesZ(){
		if(verticesHaveChanged) updateHull();
		return zVertices;
	}
	
	@Override
	public List<Location> getVertices(){
		if(verticesHaveChanged) updateHull();
		return vertices;
	}
	
	public void updateHull(){
		if(points.size() < 3) return;
		Location[] pointArray = new Location[points.size() + 1];
		pointArray[1] = points.first();
		List<Location> sorter = new ArrayList<Location>();
		Iterator<Location> it = points.iterator();
		while(it.hasNext()){
			Location next = it.next();
			if(next == points.first()) continue;
			sorter.add(next);
		}
		sorter.sort(getAngleComp(pointArray[1]));
		int is = 2;
		while(!sorter.isEmpty()){
			pointArray[is] = sorter.remove(0);
			is++;
		}
		pointArray[0] = pointArray[points.size()];
		
		int m = 1;
		for(int i = 2; i <= points.size(); i++){
			while(ccw(pointArray[m - 1], pointArray[m], pointArray[i]) <= 0){
				if(m > 1){
					m -= 1;
					continue;
				} else if( i == points.size() ){
					break;
				} else {
					i++;
				}
			}
			m++;
			
			Location temp = pointArray[i];
			pointArray[i] = pointArray[m];
			pointArray[m] = temp;
		}
		
		vertices = new ArrayList<Location>();
		xVertices = new double[m];
		zVertices = new double[m];
		for(int i = 0; i < m; i++){
			vertices.add(pointArray[i]);
			xVertices[i] = pointArray[i].getX();
			zVertices[i] = pointArray[i].getZ();
		}
		constant = null;
		multiple = null;
		verticesHaveChanged = false;
		debugInfo();
	}

	@Override
	public int getWidth() {
		return getMaxX() - getMinX();
	}

	@Override
	public int getHeight() {
		return getMaxY() - getMinY();
	}

	@Override
	public int getLength() {
		return getMaxZ() - getMinZ();
	}

	@Override
	public int getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getArea() {
		double area = 0;
		if(verticesHaveChanged) updateHull();
		if(vertices != null){
			if(vertices.size() >= 3){
				for(int i = 0; i < vertices.size(); i++){
					int j = i + 1;
					if(j >= vertices.size()) j = 0;
					area += Util.det(xVertices[i], zVertices[i], xVertices[j], zVertices[j]);
				}
			}
		}
		return (int) Math.abs(area * 0.5);
	}

	@Override
	public boolean isInside(double x, double y, double z) {
		if(y < getMinY() || y > getMaxY()) return false;
		if(verticesHaveChanged) updateHull();
		if(constant == null || multiple == null){
			constant = new double[xVertices.length];
			multiple = new double[xVertices.length];
			precalcValues();
		}
		return pointInPolygon(x, z);
	}
	
	private double[] constant;
	private double[] multiple;
	private void precalcValues() {
		int   i, j=xVertices.length-1 ;
		for(i=0; i<xVertices.length; i++) {
		    if(zVertices[j]==zVertices[i]) {
		    	constant[i]=xVertices[i];
		    	multiple[i]=0;
		    } else {
		    	constant[i]=xVertices[i]-(zVertices[i]*xVertices[j])/(zVertices[j]-zVertices[i])+(zVertices[i]*xVertices[i])/(zVertices[j]-zVertices[i]);
		    	multiple[i]=(xVertices[j]-xVertices[i])/(zVertices[j]-zVertices[i]); 
		    }
		    j=i;
		}
	}
	
	private boolean pointInPolygon(double x, double z) {
		int   i, j=xVertices.length-1 ;
		boolean  oddNodes=false;
		for (i=0; i<xVertices.length; i++) {
			if ((zVertices[i]< z && zVertices[j]>= z || zVertices[j]< z && zVertices[i]>= z)) {
				oddNodes^=(z*multiple[i]+constant[i]< x);
			}
		    j=i;
		}
		return oddNodes;
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
		return new Location(points.first().getWorld(), totalX * (1.0 / points.size()), totalY * (1.0 / points.size()), totalZ * (1.0 / points.size()));
	}
	
	public double getMaxDistanceFromCenter(){
		if(maxDistanceHasChanged || cachedMaxDistanceFromCenter == null){
			cachedMaxDistanceFromCenter = 0.0;
			Location center = getMassCenter();
			for(Location l : points){
				if(l.distanceSquared(center) > cachedMaxDistanceFromCenter * cachedMaxDistanceFromCenter){
					cachedMaxDistanceFromCenter = l.distance(center);
				}
			}
			maxDistanceHasChanged = false;
		}
		return cachedMaxDistanceFromCenter;
	}
	/*
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
						if(onCircle.distanceSquared(temp) < onCircle.distanceSquared(closest)){
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
	*/
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
	public int getMinX() {
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
	public int getMinY() {
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
	public int getMinZ() {
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
	public int getMaxX() {
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
	public int getMaxY() {
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
	public int getMaxZ() {
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
	public double distanceSquared(Shape s) {
		double distanceSquared = s.distanceSquared(getLocation());
		for(Location l : getVertices()){
			distanceSquared = Math.min(s.distanceSquared(l), distanceSquared);
		}
		return distanceSquared;
	}
	
	@Override
	public boolean intersect(Shape s) {
		for(Location l : s.getBlockLocations()){
			if(isInside(l)) return true;
		}
		return false;
	}

	@Override
	public double distanceSquared(Location location) {
		double distanceSquared = getMassCenter().distanceSquared(location);
		for(Location l : getVertices()){
			distanceSquared = Math.min(l.distanceSquared(location), distanceSquared);
		}
		if(isInside(location.getX(), location.getY(), location.getZ())) return 0;
		return distanceSquared;
	}

	public void clear() {
		points.clear();
	}

	public boolean exists() {
		return points.size() >= 3;
	}
	
	public void debugInfo(){
		for(Location l : points){
			Civilizations.DEBUG("Point at : " + l.getX() + ", " + l.getY() + ", " + l.getZ());
		}
		for(Location l : vertices){
			Civilizations.DEBUG("Hull vertex at : " + l.getX() + ", " + l.getY() + ", " + l.getZ());
		}
		for(int i = 0; i < xVertices.length; i++){
			Civilizations.DEBUG("Hull vertex["+i+"] at : " + xVertices[i] + ", " + zVertices[i]);
		}
	}
}
