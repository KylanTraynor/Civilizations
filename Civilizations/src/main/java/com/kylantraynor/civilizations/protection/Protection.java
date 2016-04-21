package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
	private PermissionSet permissionSet;
	List<Rank> ranks;
	
	public Protection(Group g){
		setGroup(g);
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		ranks = new ArrayList<Rank>();
	}
	
	public Protection(Group g, Protection parent){
		setGroup(g);
		this.parent = parent;
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		ranks = new ArrayList<Rank>();
	}
	
	public void setParent(Protection newParent){
		this.parent = newParent;
	}
	
	public Protection getParent(){
		return this.parent;
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
	}
	/**
	 * Adds the given shape to the protection.
	 * @param shape
	 * @return
	 */
	public int add(Shape shape){
		return add(shape, true);
	}
	/**
	 * Adds the given shape to the protection, and optionnally checks how many blocks have been added.
	 * @param shape
	 * @param check
	 * @return
	 */
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
	
	/**
	 * Adds the given Permissions to the protection's set.
	 * @param target of the permissions
	 * @param permission
	 * @return true
	 */
	public boolean addPermissions(PermissionTarget target, Permission permission){
		setPermissions(target, permission);
		return true;
	}
	
	public boolean addRank(Rank rank){
		if(getRank(rank.getName()) != null){
			return false;
		} else {
			setPermissions(rank, new Permission(getGroup(), new HashMap<PermissionType, Boolean>()));
			return true;
		}
	}
	
	public Rank getRank(String name){
		for(PermissionTarget r : permissionSet.getTargets()){
			if(r instanceof Rank){
				if(((Rank)r).getName().equalsIgnoreCase(name)){
					return (Rank) r;
				}
			}
		}
		if(parent != null){
			return parent.getRank(name);
		}
		return null;
	}
	
	public Rank getRank(OfflinePlayer player){
		for(PermissionTarget r : permissionSet.getTargets()){
			if(r instanceof Rank){
				if(((Rank)r).includes(player)){
					return (Rank) r;
				}
			}
		}
		if(parent != null){
			return parent.getRank(player);
		}
		return null;
	}
	/**
	 * Sets the permissions for the given target.
	 * @param target
	 * @param permission
	 */
	public void setPermissions(PermissionTarget target, Permission permission){
		permissionSet.add(target, permission);
	}
	public boolean hasPermission(Player p, PermissionType type){
		if(p.isOp()) return true;
		PlayerTarget pt = new PlayerTarget(p);
		if(hasPermissions(pt)){
			return getPermission(type, pt);
		}
		Rank r = getRank(p);
		if(hasPermissions(r)){
			return getPermission(type, r);
		}
		if(getGroup().isMember(p)){
			PermissionTarget m = new PermissionTarget(TargetType.MEMBERS);
			if(hasPermissions(m)){
				return getPermission(type, m);
			}
		} else {
			PermissionTarget o = new PermissionTarget(TargetType.OUTSIDERS);
			if(hasPermissions(o)){
				return getPermission(type, o);
			}
		}
		return false;
	}
	/**
	 * Checks if this Protection has Permissions for the given target.
	 * @param target
	 * @return true if Permissions were found, false otherwise.
	 */
	public boolean hasPermissions(PermissionTarget target){
		if(permissionSet.hasTarget(target)){
			return true;
		}
		if(parent != null) return parent.hasPermissions(target);
		return false;
	}
	/**
	 * Gets the Permissions for the given target.
	 * @param target
	 * @return Permission
	 */
	public Permission getPermissions(PermissionTarget target){
		return permissionSet.get(target);
	}
	
	public boolean getPermission(PermissionType type, PermissionTarget target){
		Permission perm = getPermissions(target);
		if(perm != null){
			if(perm.contains(type)){
				return perm.get(type);
			}
		}
		if(target.getType() != TargetType.SERVER) {return false;} else {return true;}
	}
	
	public boolean isInside(Location location){
		for(Shape s : shapes){
			if(s.isInside(location)) return true;
		}
		return false;
	}
	
	public boolean intersect(Shape s){
		for(Shape s1 : shapes){
			if(s1.intersect(s)) return true;
		}
		return false;
	}

	public Group getGroup() { return group; }
	public void setGroup(Group group) { this.group = group; }
	public PermissionSet getPermissionSet(){
		return permissionSet;
	}

	public List<Rank> getRanks() {
		List<Rank> list = new ArrayList<Rank>();
		for(PermissionTarget t : permissionSet.getTargets()){
			if(t instanceof Rank){
				list.add((Rank) t);
			}
		}
		return list;
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

	public Location getCenter() {
		World w = null;
		int minX = 0;
		int minY = 0;
		int minZ = 0;
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		for(Shape s : shapes){
			w = s.getLocation().getWorld();
			minX = Math.min(minX, s.getMinX());
			minY = Math.min(minY, s.getMinY());
			minZ = Math.min(minZ, s.getMinZ());
			maxX = Math.max(maxX, s.getMaxX());
			maxY = Math.max(maxY, s.getMaxY());
			maxZ = Math.max(maxZ, s.getMaxZ());
		}
		if(w == null) return null;
		return new Location(w, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}
}
