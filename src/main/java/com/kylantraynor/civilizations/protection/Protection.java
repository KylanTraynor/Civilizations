package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shapes.Sphere;

@Deprecated
public class Protection {
    /*
	private UUID groupId;
	private UUID parentId;
	private List<Shape> shapes;
	private PermissionSet permissionSet;
	//private List<Rank> ranks;
	
	public Protection(UUID groupId){
		this.groupId = groupId;
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		//ranks = new ArrayList<Rank>();
	}
	
	public Protection(UUID groupId, UUID parentId){
		this.groupId = groupId;
		this.parentId = parentId;
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		//ranks = new ArrayList<Rank>();
	}
	
	public void setParentId(UUID parentId){
		this.parentId = parentId;
	}
	
	public UUID getParentId(){
		return this.parentId;
	}
	
	public void show(final Player p){
		if(p != null){
			for(final Shape s : shapes){
				if(!s.getLocation().getWorld().equals(p.getLocation().getWorld())) continue;
				if(xzDistance(s.getLocation(), p.getLocation()) >= 100) continue;
				Civilizations.getProcesses().add(new BukkitRunnable(){

					@Override
					public void run() {
						s.show(p);
					}
					
				});
			}
		}
	}
	
	private int xzDistance(Location location, Location location2) {
		double x1 = location.getX();
		double x2 = location2.getX();
		double z1 = location.getZ();
		double z2 = location2.getZ();
		return (int) Math.sqrt(Math.pow(x2 - x1, 2) + (z2 - z1));
	}

	public void highlight(final Player p){
		if(p != null){
			for(final Shape s : shapes){
				if(!s.getLocation().getWorld().equals(p.getLocation().getWorld())) continue;
				if(xzDistance(s.getLocation(), p.getLocation()) >= 100) continue;
				Civilizations.getProcesses().add(new BukkitRunnable(){

					@Override
					public void run() {
						s.show(p);
					}
					
				});
			}
		}
	}
	
	public boolean walkThroughBlock(Block block){
		if(block.getType() == Material.AIR) return true;
		if(block.getType() == Material.DIRT) return false;
		if(block.getType() == Material.LONG_GRASS) return true;
		if(block.getType() == Material.RAILS) return true;
		if(block.getType() == Material.CAKE_BLOCK) return true;
		if(block.getType() == Material.CHEST) return true;
		if(block.getType() == Material.TRAPPED_CHEST) return true;
		if(block.getType() == Material.COBBLE_WALL) return true;
		return false;
	}
	
	public void hide(final Player p){
		if(p != null){
			for(final Shape s : shapes){
				if(!s.getLocation().getWorld().equals(p.getLocation().getWorld())) continue;
				if(xzDistance(s.getLocation(), p.getLocation()) >= 100) continue;
				Civilizations.getProcesses().add(new BukkitRunnable(){

					@Override
					public void run() {
						s.hide(p);
					}
					
				});
			}
		}
	}*/

	/*
	public boolean intersect(Shape s){
		for(Shape s1 : getShapes()){
			if(s1.intersect(s)) return true;
		}
		return false;
	}*/

}
