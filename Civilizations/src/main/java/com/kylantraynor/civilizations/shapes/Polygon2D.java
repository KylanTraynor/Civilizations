package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.kylantraynor.civilizations.util.Util;
import com.kylantraynor.voronoi.VectorXZ;

public class Polygon2D extends Shape {
	private int[] xVertices;
	private int[] zVertices;
	
	private VectorXZ[] normals;
	
	private double[] constant;
	private double[] multiple;
	
	private long area;
	
	public Polygon2D(Location... locations) {
		super(get2DCenter(locations));
		xVertices = new int[locations.length];
		zVertices = new int[locations.length];
		
		normals = new VectorXZ[locations.length];
		
		constant = new double[locations.length];
		multiple = new double[locations.length];
		
		for(int i = 0; i < locations.length; i++){
			xVertices[i] = locations[i].getBlockX();
			zVertices[i] = locations[i].getBlockZ();
		}
		
		calcArea();
		precalcValues();
		// TODO Auto-generated constructor stub
	}
	
	public void calcArea(){
		area = 0;
		if(getVertices().size() >= 3){
			for(int i = 0; i < getVertices().size(); i++){
				int j = (i == getVertices().size() - 1 ? 0 : i + 1);
				area += Util.det(xVertices[i], zVertices[i], xVertices[j], zVertices[j]);
			}
			area = (long) Math.abs(area / 2l);
		}
	}
	
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
	public boolean intersect(Shape s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distanceSquared(Location location) {
		if(location.getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
		double distanceSquared = getLocation().distanceSquared(location);
		if(xVertices.length >= 3){
			if(isInside(location)) return 0;
			
			VectorXZ p0 = new VectorXZ(location.getBlockX(), location.getBlockZ());
			for(int i = 0 ; i < xVertices.length; i++){
				int j = (i == xVertices.length - 1 ? 0 : i + 1);
				
				VectorXZ p1 = new VectorXZ(xVertices[i], zVertices[i]);
				VectorXZ p2 = new VectorXZ(xVertices[j], zVertices[j]);
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
			}
			/*
			List<Location> sorter = new ArrayList<Location>();
			for(Location l : getVertices()){
				sorter.add(l);
			}
			sorter.add(location);
			sorter.sort(getAngleComp(getLocation()));
			int i = sorter.indexOf(location);
			int h = i - 1;
			int j = i + 1;
			if(i == 0){
				h = sorter.size() - 1;
			} else if(i == sorter.size() - 1){
				j = 0;
			}
			VectorXZ p0 = new VectorXZ(location.getBlockX(), location.getBlockZ());
			VectorXZ p1 = new VectorXZ(sorter.get(h).getBlockX(), sorter.get(h).getBlockZ());
			VectorXZ p2 = new VectorXZ(sorter.get(j).getBlockX(), sorter.get(j).getBlockZ());
			VectorXZ v = p2.substract(p1);
			VectorXZ n = v.getOrthogonal();
			VectorXZ p3 = VectorXZ.getRayIntersection(p1, v, p0, n);
			
			if(p1.getX() < p2.getX()){
				if(p3.getX() < p1.getX()){
					distanceSquared = Math.min(p0.distance(p1), distanceSquared);
				} else if(p3.getX() > p2.getX()){
					distanceSquared = Math.min(p0.distance(p2), distanceSquared);
				} else {
					distanceSquared = Math.min(p0.distance(p3), distanceSquared);
				}
			} else {
				if(p3.getX() > p1.getX()){
					distanceSquared = Math.min(p0.distance(p1), distanceSquared);
				} else if(p3.getX() < p2.getX()){
					distanceSquared = Math.min(p0.distance(p2), distanceSquared);
				} else {
					distanceSquared = Math.min(p0.distance(p3), distanceSquared);
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

	@Override
	public double distanceSquared(Shape shape) {
		if(shape.getLocation().getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
		if(intersect(shape)) return 0.0;
		double distanceSquared = distanceSquared(shape.getLocation());
		if(xVertices.length >= 3){
			for(Location l : shape.getVertices()){
				distanceSquared = Math.min(distanceSquared(l), distanceSquared);
			}
		}
		return distanceSquared;
	}

	@Override
	public int getMinX() {
		int result = getLocation().getBlockX();
		for(int x : xVertices){
			result = x < result ? x : result;
		}
		return result;
	}

	@Override
	public int getMinY() {
		int result = getLocation().getBlockY();
		return result;
	}

	@Override
	public int getMinZ() {
		int result = getLocation().getBlockZ();
		for(int z : zVertices){
			result = z < result ? z : result;
		}
		return result;
	}

	@Override
	public int getMaxX() {
		int result = getLocation().getBlockX();
		for(int x : xVertices){
			result = x > result ? x : result;
		}
		return result;
	}

	@Override
	public int getMaxY() {
		int result = getLocation().getBlockY();
		return result;
	}

	@Override
	public int getMaxZ() {
		int result = getLocation().getBlockZ();
		for(int z : zVertices){
			result = z > result ? z : result;
		}
		return result;
	}

	@Override
	public int getWidth() {
		return getMaxX() - getMinX();
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getLength() {
		return getMaxZ() - getMinZ();
	}

	@Override
	public long getVolume() {
		return 0;
	}

	@Override
	public long getArea() {
		return area;
	}

	@Override
	public boolean isInside(double x, double y, double z) {
		//ignores y;
		//blockify
		x = Math.floor(x);
		z = Math.floor(z);
		
		if(getMinX() < x || getMinZ() < z || getMaxX() > x || getMaxZ() > z) return false;
		
		return pointInPolygon(x, z);
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
	
	public static Location get2DCenter(Location[] locations){
		double x = 0;
		double y = Double.POSITIVE_INFINITY;
		double z = 0;
		for(Location l : locations){
			x += l.getBlockX() + 0.5;
			z += l.getBlockZ() + 0.5;
			y = (l.getBlockY() < y ? y : l.getBlockY());
		}
		x /= locations.length;
		z /= locations.length;
		return new Location(locations[0].getWorld(), x, y, z);
	}

}
