package com.kylantraynor.civilizations.shapes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.kylantraynor.civilizations.utils.Utils;
import com.kylantraynor.voronoi.VectorXZ;

public class Polygon2D extends Shape {
	private double[] xVertices;
	private double[] zVertices;
	
	private VectorXZ[] normals;
	
	private double[] constant;
	private double[] multiple;
	
	private long area;
	private boolean selfIntersects;
	
	public Polygon2D(Location... locations) {
		super(get2DCenter(locations));
		xVertices = new double[locations.length];
		zVertices = new double[locations.length];
		
		normals = new VectorXZ[locations.length];
		
		constant = new double[locations.length];
		multiple = new double[locations.length];
		
		for(int i = 0; i < locations.length; i++){
			xVertices[i] = locations[i].getBlockX();
			zVertices[i] = locations[i].getBlockZ();
		}
		
		calcArea();
		precalcValues();
		checkSelfIntersect();
		// TODO Auto-generated constructor stub
	}
	
	public void calcArea(){
		area = 0;
		if(getVertices().size() >= 3){
			for(int i = 0; i < getVertices().size(); i++){
				int j = (i == getVertices().size() - 1 ? 0 : i + 1);
				area += Utils.det(xVertices[i], zVertices[i], xVertices[j], zVertices[j]);
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
	
	public List<Segment> getSegments(){
		List<Segment> segments = new ArrayList<Segment>();
		for(int i = 0; i < xVertices.length; i ++){
			int j = (i == xVertices.length - 1 ? 0 : i+1);
			segments.add(new Segment(xVertices[i], zVertices[i], xVertices[j], zVertices[j]));
		}
		return segments;
	}
	
	private void checkSelfIntersect() {
		List<Segment> segments = getSegments();
		for(Segment s : segments){
			for(Segment s2 : segments){
				if(s == s2) continue;
				if(s.intersects(s2)){
					selfIntersects = true;
					return;
				}
			}
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
		if(s instanceof Polygon2D){
			for(Segment seg : getSegments()){
				for(Segment seg2 : ((Polygon2D) s).getSegments()){
					if(seg.intersects(seg2)) return true;
				}
			}
		}
		return false;
	}
	
	public boolean selfIntersects(){
		return selfIntersects;
	}

	@Override
	public double distanceSquared(Location location) {
		if(!location.getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
		double distanceSquared = getLocation().distanceSquared(location);
		if(xVertices.length >= 3){
			if(isInside(location)) return 0;
			
			VectorXZ p0 = new VectorXZ(location.getBlockX(), location.getBlockZ());
			for(int i = 0 ; i < xVertices.length; i++){
				int j = (i == xVertices.length - 1 ? 0 : i + 1);
				Segment s = new Segment(xVertices[i], zVertices[i], xVertices[j], zVertices[j]);
				distanceSquared = Math.min(s.distanceSquared(p0), distanceSquared);
			}
		}
		return distanceSquared;
	}

	@Override
	public double distanceSquared(Shape shape) {
		if(!shape.getLocation().getWorld().equals(getLocation().getWorld())) return Double.POSITIVE_INFINITY;
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
	public int getMinBlockX() {
		int result = getLocation().getBlockX();
		for(double x : xVertices){
			result = x < result ? (int)x : result;
		}
		return result;
	}

	@Override
	public int getMinBlockY() {
		int result = getLocation().getBlockY();
		return result;
	}

	@Override
	public int getMinBlockZ() {
		int result = getLocation().getBlockZ();
		for(double z : zVertices){
			result = z < result ? (int)z : result;
		}
		return result;
	}

	@Override
	public int getMaxBlockX() {
		int result = getLocation().getBlockX();
		for(double x : xVertices){
			result = (x-1) > result ? (int)(x-1) : result;
		}
		return result;
	}

	@Override
	public int getMaxBlockY() {
		int result = getLocation().getBlockY();
		return result;
	}

	@Override
	public int getMaxBlockZ() {
		int result = getLocation().getBlockZ();
		for(double z : zVertices){
			result = (z-1) > result ? (int)(z-1) : result;
		}
		return result;
	}

	@Override
	public int getBlockWidth() {
		return getMaxBlockX() - getMinBlockX();
	}

	@Override
	public int getBlockHeight() {
		return 0;
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

	@Override
	public double[] getVerticesX() {
		return xVertices;
	}

	@Override
	public double[] getVerticesZ() {
		return zVertices;
	}

}
