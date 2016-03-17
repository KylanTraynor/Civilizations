package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.shapes.Sphere;

public class Protection {
	private Group group;
	Protection parent;
	List<Shape> shapes;
	List<Permission> permissions;
	List<Rank> ranks;
	
	public Protection(Group g){
		setGroup(g);
		shapes = new ArrayList<Shape>();
		permissions = new ArrayList<Permission>();
		ranks = new ArrayList<Rank>();
	}
	
	public Protection(Group g, Protection parent){
		setGroup(g);
		this.parent = parent;
		shapes = new ArrayList<Shape>();
		permissions = new ArrayList<Permission>();
		ranks = new ArrayList<Rank>();
	}
	
	public void show(final Player p){
		if(p != null){
			for(final Shape s : shapes){
				if(!s.getLocation().getWorld().equals(p.getLocation().getWorld())) continue;
				if(xzDistance(s.getLocation(), p.getLocation()) >= 100) continue;
				Civilizations.getProcesses().add(new BukkitRunnable(){

					@Override
					public void run() {
						for(Block b : s.getBlockSurface()){
							if(b.getLocation().distance(p.getLocation()) <= 50 && !walkThroughBlock(b)){
								p.sendBlockChange(b.getLocation(), Material.GOLD_BLOCK, (byte) 0);
							}
						}
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
						for(Block b : s.getBlockSurface()){
							if(b.getLocation().distance(p.getLocation()) <= 50 && !walkThroughBlock(b)){
								p.sendBlockChange(b.getLocation(), Material.GLOWSTONE, (byte) 0);
							}
						}
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
						for(Block b : s.getBlockSurface()){
							if(b.getLocation().distance(p.getLocation()) <= 255){
								p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
							}
						}
					}
					
				});
			}
		}
	}
	
	public int add(Shape shape){
		return add(shape, true);
	}
	
	public int add(Shape shape, boolean check){
		if(check){
			int i = 0;
			for(Location l : shape.getBlockLocations()){
				if(!isInside(l)){i++;}
			}
			if(i != 0 || shape instanceof Sphere){shapes.add(shape);}
			return i;
		} else {
			shapes.add(shape);
			return 0;
		}
	}
	
	public boolean addPermission(Permission perm){
		if(hasPermission(perm.getTargetType(), perm.getTargetId())){
			return false;
		} else {
			permissions.add(perm);
			return true;
		}
	}
	
	public boolean addRank(Rank rank){
		if(ranks.contains(rank)){
			return false;
		}
		for(Rank r : ranks){
			if(r.getName().equals(rank.getName())) return false;
		}
		return true;
	}
	
	public void setPermission(Permission perm){
		Permission p = getPermission(perm.getTargetType(), perm.getTargetId());
		if(p != null){permissions.remove(p);}
		permissions.add(perm);
	}
	
	public boolean hasPermission(PermissionTarget target, String id){
		for(Permission p : permissions){
			if(p.isTarget(target, id)) return true;
		}
		if(parent != null) return parent.hasPermission(target, id);
		return false;
	}
	
	public Permission getPermission(PermissionTarget target, String id){
		for(Permission p : permissions){
			if(p.isTarget(target, id)) return p;
		}
		return null;
	}
	
	public boolean getType(PermissionType type, PermissionTarget target, String id){
		Permission p = getPermission(target, id);
		if(p != null){
			if(p.getTypes().containsKey(type)){
				return p.getTypes().get(type);
			}
		}
		if(target != PermissionTarget.SERVER){return false;} else {return true;}
	}
	
	public boolean isInside(Location location){
		for(Shape s : shapes){
			if(s.isInside(location)) return true;
		}
		return false;
	}

	public Group getGroup() { return group; }
	public void setGroup(Group group) { this.group = group; }
}
