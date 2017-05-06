package com.kylantraynor.civilizations.shapes;

import com.kylantraynor.voronoi.VectorXZ;

public class Segment {
	private VectorXZ p1;
	private VectorXZ p2;
	
	public Segment(double x1, double z1, double x2, double z2){
		p1 = new VectorXZ((float) x1, (float) z1);
		p2 = new VectorXZ((float) x2, (float) z2);
	}
	
	public VectorXZ getVector(){
		return new VectorXZ(p2.x - p1.x, p2.z - p1.z);
	}
	
	public VectorXZ getNormal(){
		return new VectorXZ(p2.z - p1.z, p1.x - p2.x);
	}
	
	public double distanceSquared(double x, double z){
		VectorXZ target = new VectorXZ((float) x, (float) z);
		return distanceSquared(target);
	}
	
	public double distanceSquared(VectorXZ target){
		VectorXZ p3 = VectorXZ.getRayIntersection(p1, getVector(), target, getNormal());
		if(p1.getX() < p2.getX()){
			if(p3.getX() < p1.getX()){
				return target.distanceSquared(p1);
			} else if(p3.getX() > p2.getX()){
				return target.distanceSquared(p2);
			} else {
				return target.distanceSquared(p3);
			}
		} else {
			if(p3.getX() > p1.getX()){
				return target.distanceSquared(p1);
			} else if(p3.getX() < p2.getX()){
				return target.distanceSquared(p2);
			} else {
				return target.distanceSquared(p3);
			}
		}
	}
	
	public boolean intersects(Segment segment){
		VectorXZ intersection = VectorXZ.getRayIntersection(p1, getVector(), segment.p1, segment.getVector());
		boolean thisIntersect = false;
		boolean otherIntersect = false;
		if(p1.x < p2.x){
			thisIntersect = (p1.x < intersection.x && intersection.x < p2.x);
		} else if(p1.x > p2.x){
			thisIntersect = (p1.x > intersection.x && intersection.x > p2.x);
		} else if(p1.z < p2.z){
			thisIntersect = (p1.z < intersection.z && intersection.z < p2.z);
		} else {
			thisIntersect = (p1.z > intersection.z && intersection.z > p2.z);
		}
		if(!thisIntersect) return false;
		if(segment.p1.x < segment.p2.x){
			otherIntersect = (segment.p1.x < intersection.x && intersection.x < segment.p2.x);
		} else if(segment.p1.x > segment.p2.x){
			otherIntersect = (segment.p1.x > intersection.x && intersection.x > segment.p2.x);
		} else if(segment.p1.z < segment.p2.z){
			otherIntersect = (segment.p1.z < intersection.z && intersection.z < segment.p2.z);
		} else {
			otherIntersect = (segment.p1.z > intersection.z && intersection.z > segment.p2.z);
		}
		return otherIntersect;
	}
}
