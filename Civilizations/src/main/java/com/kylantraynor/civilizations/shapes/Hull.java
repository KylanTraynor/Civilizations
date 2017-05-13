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
import com.kylantraynor.voronoi.VectorXZ;

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
		Civilizations.currentInstance.getLogger().info("Recalculating hull.");
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
	public long getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getArea() {
		long area = 0;
		if(getVertices().size() >= 3){
			for(int i = 0; i < getVertices().size(); i++){
				int j = (i == getVertices().size() - 1 ? 0 : i + 1);
				area += Util.det(xVertices[i], zVertices[i], xVertices[j], zVertices[j]);
			}
		}
		return (long) Math.abs(area / 2l);
	}

	@Override
	public boolean isInside(double x, double y, double z) {
		if(verticesHaveChanged) updateHull();
		//if(y < getMinY() || y > getMaxY()) return false;
		if(x < getMinX() || x > getMaxX()) return false;
		if(z < getMinZ() || z > getMaxZ()) return false;
		if(constant == null || multiple == null){
			constant = new double[xVertices.length];
			multiple = new double[xVertices.length];
			precalcValues();
		}
		return pointInPolygon(x, z);
	}
	
	private double[] constant;
	private double[] multiple;

	private double maxDistanceSquared = Double.NaN;

	private Location massCenter;
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
		if(massCenter != null) return massCenter;
		int totalX = 0;
		int totalZ = 0;
		int totalY = 0;
		for(Location l : points){
			totalX += l.getBlockX();
			totalZ += l.getBlockZ();
			totalY += l.getBlockY();
		}
		massCenter =  new Location(points.first().getWorld(), totalX * (1.0 / points.size()), totalY * (1.0 / points.size()), totalZ * (1.0 / points.size()));
		return massCenter;
	}
	
	public double getMaxDistanceFromCenter(){
		return Math.sqrt(getMaxSquaredDistanceFromCenter());
	}
	public double getMaxSquaredDistanceFromCenter(){
		if(!Double.isNaN(maxDistanceSquared)) return maxDistanceSquared ; 
		double distanceSquared = 0.0;
		Location center = getMassCenter();
		if(exists()){
			for(Location l : getVertices()){
				distanceSquared = Math.max(l.distanceSquared(center), distanceSquared);
			}
		} else {
			for(Location l : points){
				distanceSquared = Math.max(l.distanceSquared(center), distanceSquared);
			}
		}
		maxDistanceSquared = distanceSquared;
		return distanceSquared;
		/*
		if(maxDistanceHasChanged || cachedMaxDistanceFromCenter == null){
			cachedMaxDistanceFromCenter = 0.0;
			double distanceSquared = 0.0;
			Location center = getMassCenter();
			if(exists()){
				for(Location l : getVertices()){
					if(l.distanceSquared(center) > distanceSquared){
						distanceSquared = l.distanceSquared(center);
					}
				}
			} else {
				for(Location l : points){
					if(l.distanceSquared(center) > distanceSquared){
						distanceSquared = l.distanceSquared(center);
					}
				}
			}
			
			cachedMaxDistanceFromCenter = Math.sqrt(distanceSquared);
			maxDistanceHasChanged = false;
		}
		return cachedMaxDistanceFromCenter;
		*/
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
		verticesHaveChanged = true;
		massCenter = null;
		maxDistanceSquared = Double.NaN;
		minX = null;
		minY = null;
		minZ = null;
		maxX = null;
		maxY = null;
		maxZ = null;
	}
	
	public boolean hasChanged(){
		if(verticesHaveChanged) return true;
		return false;
	}
	
	private Integer minX = null;
	private Integer minY = null;
	private Integer minZ = null;
	private Integer maxX = null;
	private Integer maxY = null;
	private Integer maxZ = null;
	
	@Override
	public int getMinX() {
		if(minX != null) return minX;
		Integer min = null;
		if(exists()){
			for(Location l : getVertices()){
				if(min == null){
					min = l.getBlockX();
				} else if (l.getBlockX() < min){
					min = l.getBlockX();
				}
			}
		} else {
			for(Location l : points){
				if(min == null){
					min = l.getBlockX();
				} else if (l.getBlockX() < min){
					min = l.getBlockX();
				}
			}
		}
		minX = min;
		return min;
	}

	@Override
	public int getMinY() {
		if(minY != null) return minY;
		Integer min = null;
		for(Location l : points){
			if(min == null){
				min = l.getBlockY();
			} else if (l.getBlockY() < min){
				min = l.getBlockY();
			}
		}
		minY = min;
		return min;
	}

	@Override
	public int getMinZ() {
		if(minZ != null) return minZ;
		Integer min = null;
		if(exists()){
			for(Location l : getVertices()){
				if(min == null){
					min = l.getBlockZ();
				} else if (l.getBlockZ() < min){
					min = l.getBlockZ();
				}
			}
		} else {
			for(Location l : points){
				if(min == null){
					min = l.getBlockZ();
				} else if (l.getBlockZ() < min){
					min = l.getBlockZ();
				}
			}
		}
		minZ = min;
		return min;
	}

	@Override
	public int getMaxX() {
		if(maxX != null) return maxX;
		Integer max = null;
		if(exists()){
			for(Location l : getVertices()){
				if(max == null){
					max = l.getBlockX();
				} else if (l.getBlockX() > max){
					max = l.getBlockX();
				}
			}
		} else {
			for(Location l : points){
				if(max == null){
					max = l.getBlockX();
				} else if (l.getBlockX() > max){
					max = l.getBlockX();
				}
			}
		}
		maxX = max;
		return max;
	}

	@Override
	public int getMaxY() {
		if(maxY != null) return maxY;
		Integer max = null;
		for(Location l : points){
			if(max == null){
				max = l.getBlockY();
			} else if (l.getBlockY() > max){
				max = l.getBlockY();
			}
		}
		maxY = max;
		return max;
	}

	@Override
	public int getMaxZ() {
		if(maxZ != null) return maxZ;
		Integer max = null;
		if(exists()){
			for(Location l : getVertices()){
				if(max == null){
					max = l.getBlockZ();
				} else if (l.getBlockZ() > max){
					max = l.getBlockZ();
				}
			}
		} else {
			for(Location l : points){
				if(max == null){
					max = l.getBlockZ();
				} else if (l.getBlockZ() > max){
					max = l.getBlockZ();
				}
			}
		}
		maxZ = max;
		return max;
	}

	@Override
	public double distanceSquared(Shape s) {
		if(!s.getLocation().getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
		if(intersect(s)) return 0.0;
		double distanceSquared = distanceSquared(s.getLocation());
		if(exists()){
			for(Location l : s.getVertices()){
				distanceSquared = Math.min(distanceSquared(l), distanceSquared);
			}
		}
		return distanceSquared;
	}
	
	@Override
	public boolean intersect(Shape s) {
		if(!s.getLocation().getWorld().equals(getLocation().getWorld())) return false;
		for(Location l : s.getBlockLocations()){
			if(isInside(l)) return true;
		}
		return false;
	}

	@Override
	public double distanceSquared(Location location) {
		if(!location.getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
		double distanceSquared = getMassCenter().distanceSquared(location);
		if(exists()){
			if(isInside(location)) return 0;
			
			List<Location> sorter = new ArrayList<Location>();
			for(Location l : getVertices()){
				sorter.add(l);
			}
			sorter.add(location);
			sorter.sort(getAngleComp(getMassCenter()));
			int i = sorter.indexOf(location);
			int h = i - 1;
			int j = i + 1;
			if(i == 0){
				h = sorter.size() - 1;
			} else if(i == sorter.size() - 1){
				j = 0;
			}
			
			VectorXZ p0 = new VectorXZ(location.getBlockX(), location.getBlockZ());
			Segment s = new Segment(
					sorter.get(h).getBlockX(), 
					sorter.get(h).getBlockZ(), 
					sorter.get(j).getBlockX(), 
					sorter.get(j).getBlockZ());
			distanceSquared = s.distanceSquared(p0);
			/*
			VectorXZ p1 = new VectorXZ(sorter.get(h).getBlockX(), sorter.get(h).getBlockZ());
			VectorXZ p2 = new VectorXZ(sorter.get(j).getBlockX(), sorter.get(j).getBlockZ());
			VectorXZ v = p2.substract(p1);
			VectorXZ n = v.getOrthogonal();
			VectorXZ p3 = VectorXZ.getRayIntersection(p1, v, p0, n);
			
			if(p1.getX() < p2.getX()){
				if(p3.getX() < p1.getX()){
					distanceSquared = Math.min(p0.distanceSquared(p1), distanceSquared);
				} else if(p3.getX() > p2.getX()){
					distanceSquared = Math.min(p0.distanceSquared(p2), distanceSquared);
				} else {
					distanceSquared = Math.min(p0.distanceSquared(p3), distanceSquared);
				}
			} else {
				if(p3.getX() > p1.getX()){
					distanceSquared = Math.min(p0.distanceSquared(p1), distanceSquared);
				} else if(p3.getX() < p2.getX()){
					distanceSquared = Math.min(p0.distanceSquared(p2), distanceSquared);
				} else {
					distanceSquared = Math.min(p0.distanceSquared(p3), distanceSquared);
				}
			}
			*/
		}
		/*
		if(exists()){
			for(Location l : getVertices()){
				distanceSquared = Math.min(l.distanceSquared(location), distanceSquared);
			}
			if(isInside(location.getX(), location.getY(), location.getZ())) return 0;
		}
		*/
		return distanceSquared;
	}

	public void clear() {
		points.clear();
		setChanged(true);
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
