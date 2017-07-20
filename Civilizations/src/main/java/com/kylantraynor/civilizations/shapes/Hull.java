package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
	
	private Double minX = null;
	private Double minY = null;
	private Double minZ = null;
	private Double maxX = null;
	private Double maxY = null;
	private Double maxZ = null;
	
	private World world = null;

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
	
	public boolean isInside(Block b){
		return isInside(b.getX() + 0.5, b.getY(), b.getZ() + 0.5);
	}

	@Override
	public boolean isInside(double x, double y, double z) {
		if(verticesHaveChanged) updateHull();
		//if(y < getMinY() || y > getMaxY()) return false;
		if(x < getMinX() || x >= getMaxX()) return false;
		if(z < getMinZ() || z >= getMaxZ()) return false;
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
		StringBuilder result = new StringBuilder("HULL;");
		result.append(getWorld().getName() + ";");
		result.append(getMinBlockY() + ";");
		result.append(getMaxBlockY() + ";");
		for(Location p : points){
			result.append(p.getBlockX() + "," + p.getBlockZ() + ";");
		}
		return result.toString();
	}
	
	public static Hull parse(String string){
		String[] component = string.split(";");
		Hull result = new Hull();
		World w = Bukkit.getWorld(component[1]);
		result.setWorld(w);
		int minY = Integer.parseInt(component[2]);
		int maxY = Integer.parseInt(component[3]);
		for(int i = 4; i < component.length; i++){
			String[] point = component[i].split(",");
			Location l = new Location(w, Integer.parseInt(point[0]), (i % 2 == 0 ? minY : maxY), Integer.parseInt(point[1]));
			result.addPoint(l);
		}
		return result;
	}

	@Override
	public Location[] getBlockLocations() {
		List<Location> list = new ArrayList<Location>();
		World w = getLocation().getWorld();
		for(int x = getMinBlockX(); x <= getMaxBlockX(); x++){
			for(int z = getMinBlockZ(); z <= getMaxBlockZ(); z++){
				for(int y = getMinBlockY(); y <= getMaxBlockY(); y++){
					if(isInside(x + 0.5, y, z + 0.5)){
						list.add(w.getBlockAt(x, y, z).getLocation());
					}
				}
			}
		}
		return list.toArray(new Location[list.size()]);
	}

	@Override
	public Block[] getBlockSurface() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addBlock(Block block){
		if(block == null) return;
		//Add the 4 corners of the block at BlockY;
		if(getWorld() == null){
			setWorld(block.getWorld());
		}
		if(points.size() == 0){
			minY = (double) block.getY();
			maxY = (double) block.getY() + 1;
		} else {
			minY = Math.min(minY, block.getY());
			maxY = Math.max(maxY, block.getY() + 1);
		}
		for(int x = 0; x <= 1; x++){
			for(int z = 0; z <= 1; z++){
				points.add(block.getLocation().clone().add(x, 0, z));
			}
		}
		setChanged(true);
	}
	
	public void addPoint(Location l){
		if(getWorld() == null){
			setWorld(l.getWorld());
		}
		if(points.size() == 0){
			minY = l.getY();
			maxY = l.getY();
		} else {
			minY = Math.min(minY, l.getY());
			maxY = Math.max(maxY, l.getY());
		}
		points.add(l);
		setChanged(true);
	}
	
	public void addBlocks(List<Block> blocks){
		for(Block b : blocks){
			addBlock(b);
		}
		setChanged(true);
	}
	
	public void addPoints(List<Location> locations){
		for(Location l : locations){
			if(l == null) continue;
			addPoint(l);
		}
		setChanged(true);
	}
	
	public Location getMassCenter(){
		double totalX = 0;
		double totalZ = 0;
		double totalY = 0;
		for(Location l : points){
			totalX += l.getX();
			totalZ += l.getZ();
			totalY += l.getY();
		}
		massCenter =  new Location(getWorld(), totalX * (1.0 / points.size()), totalY * (1.0 / points.size()), totalZ * (1.0 / points.size()));
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
		//minY = null;
		minZ = null;
		maxX = null;
		//maxY = null;
		maxZ = null;
	}
	
	public boolean hasChanged(){
		if(verticesHaveChanged) return true;
		return false;
	}
	
	@Override
	public double getMinX() {
		if(minX != null) return minX;
		if(exists()){
			for(Location l : getVertices()){
				if(minX == null){
					minX = l.getX();
				} else if (l.getX() < minX){
					minX = l.getX();
				}
			}
		} else {
			for(Location l : points){
				if(minX == null){
					minX = l.getX();
				} else if (l.getX() < minX){
					minX = l.getX();
				}
			}
		}
		return minX;
	}

	@Override
	public double getMinY() {
		if(minY != null) return minY;
		for(Location l : points){
			if(minY == null){
				minY = l.getY();
			} else if (l.getY() < minY){
				minY = l.getY();
			}
		}
		return minY;
	}

	@Override
	public double getMinZ() {
		if(minZ != null) return minZ;
		if(exists()){
			for(Location l : getVertices()){
				if(minZ == null){
					minZ = l.getZ();
				} else if (l.getZ() < minZ){
					minZ = l.getZ();
				}
			}
		} else {
			for(Location l : points){
				if(minZ == null){
					minZ = l.getZ();
				} else if (l.getZ() < minZ){
					minZ = l.getZ();
				}
			}
		}
		return minZ;
	}

	@Override
	public double getMaxX() {
		if(maxX != null) return maxX;
		if(exists()){
			for(Location l : getVertices()){
				if(maxX == null){
					maxX = l.getX();
				} else if (l.getX() > maxX){
					maxX = l.getX();
				}
			}
		} else {
			for(Location l : points){
				if(maxX == null){
					maxX = l.getX();
				} else if (l.getX() > maxX){
					maxX = l.getX();
				}
			}
		}
		return maxX;
	}

	@Override
	public double getMaxY() {
		if(maxY != null) return maxY;
		for(Location l : points){
			if(maxY == null){
				maxY = l.getY();
			} else if (l.getY() > maxY){
				maxY = l.getY();
			}
		}
		return maxY;
	}

	@Override
	public double getMaxZ() {
		if(maxZ != null) return maxZ;
		if(exists()){
			for(Location l : getVertices()){
				if(maxZ == null){
					maxZ = l.getZ();
				} else if (l.getZ() > maxZ){
					maxZ = l.getZ();
				}
			}
		} else {
			for(Location l : points){
				if(maxZ == null){
					maxZ = l.getZ();
				} else if (l.getZ() > maxZ){
					maxZ = l.getZ();
				}
			}
		}
		return maxZ;
	}
	
	@Override
	public int getMinBlockX() {
		return (int) Math.floor(getMinX());
	}

	@Override
	public int getMinBlockY() {
		return (int) Math.floor(getMinY());
	}

	@Override
	public int getMinBlockZ() {
		return (int) Math.floor(getMinZ());
	}

	@Override
	public int getMaxBlockX() {
		double dif = getMaxX() - Math.floor(getMaxX());
		if(dif > 0){
			return (int) Math.floor(getMaxX());
		} else {
			return (int) Math.floor(getMaxX()) - 1;
		}
	}

	@Override
	public int getMaxBlockY() {
		double dif = getMaxY() - Math.floor(getMaxY());
		if(dif > 0){
			return (int) Math.floor(getMaxY());
		} else {
			return (int) Math.floor(getMaxY()) - 1;
		}
	}

	@Override
	public int getMaxBlockZ() {
		double dif = getMaxZ() - Math.floor(getMaxZ());
		if(dif > 0){
			return (int) Math.floor(getMaxZ());
		} else {
			return (int) Math.floor(getMaxZ()) - 1;
		}
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
		if(getMinY() < location.getBlockY() && location.getBlockY() < getMaxY()){
			return distanceSquared;
		} else {
			if(getMinY() > location.getBlockY()){
				double dy = location.getBlockY() - getMinY();
				return distanceSquared + dy*dy;
			} else{
				double dy = getMaxY() - location.getBlockY();
				return distanceSquared + dy*dy;
			}
		}
		//return distanceSquared;
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

	@Override
	public World getWorld() {
		if(super.getLocation() != null) return super.getLocation().getWorld(); 
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
