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

public class Protection {
	private Protection parent;
	private List<Shape> shapes;
	private PermissionSet permissionSet;
	//private List<Rank> ranks;
	
	public Protection(){
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		//ranks = new ArrayList<Rank>();
	}
	
	public Protection(Protection parent){
		this.parent = parent;
		shapes = new ArrayList<Shape>();
		permissionSet = new PermissionSet();
		//ranks = new ArrayList<Rank>();
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
		int result = add(shape, true);
		refreshParent();
		return result;
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
			refreshParent();
			return i;
		} else {
			shapes.add(shape);
			refreshParent();
			return 0;
		}
	}
	
	/**
	 * Adds the given Permissions to the protection's set.
	 * @param target of the permissions
	 * @param permission
	 * @return true
	 */
	public boolean addPermissions(PermissionTarget target, Permissions permission){
		setPermissions(target, permission);
		return true;
	}
	
	public boolean addRank(Rank rank){
		if(getRank(rank.getUniqueId()) != null){
			return false;
		} else {
			setPermissions(rank, new Permissions(new HashMap<PermissionType, Boolean>()));
			return true;
		}
	}
	
	public Rank getRank(UUID uuid){
		if(uuid == null) return null;
		for(PermissionTarget r : permissionSet.getTargets()){
			if(r instanceof Rank){
				if(((Rank)r).getUniqueId() == (uuid)){
					return (Rank) r;
				}
			}
		}
		return null;
	}
	
	public Rank getRank(String string) {
		if(string == null) return null;
		for(PermissionTarget r : permissionSet.getTargets()){
			if(r instanceof Rank){
				if(((Rank)r).getName().equalsIgnoreCase(string)){
					return (Rank) r;
				}
			}
		}
		return null;
	}
	
	public Rank getRank(OfflinePlayer player){
		if(player == null) return null;
		for(PermissionTarget r : permissionSet.getTargets()){
			if(r instanceof Rank){
				if(((Rank)r).includes(player)){
					return (Rank) r;
				}
			}
		}
		return null;
	}
	/**
	 * Sets the permissions for the given target.
	 * @param target
	 * @param permission
	 */
	public void setPermissions(PermissionTarget target, Permissions permission){
		permissionSet.add(target, permission);
	}
	
	/**
	 * Checks if the given players has the permission.
	 * @param player
	 * @param type
	 * @return
	 */
	@Deprecated
	public boolean hasPermission(OfflinePlayer player, PermissionType type){
		return ProtectionManager.hasPermission(this, type, player, false);
		/*// First, check if the player is op
		if(player.isOp()) return true;
		// If not, check if the protection has a specific permission set for the player
		PlayerTarget pt = new PlayerTarget(player);
		if(permissionSet.isSet(type, pt)){
			return getPermission(type, pt);
		}
		// If not, check if the protection has a specific permission set for the player's rank
		Rank r = getRank(player);
		if(r != null){
			if(permissionSet.isSet(type, r)){
				return getPermission(type, r);
			} else {
				while(r.getParentId() != null){
					Rank rParent = getRank(r.getParentId());
					if(permissionSet.isSet(type, rParent)){
						return getPermission(type, rParent);
					} else {
						r = rParent;
					}
				}
			}
		}
		
		// If not, check if the protection has a permission set for any group the player belongs to
		for(PermissionTarget target : permissionSet.getTargets()){
			if(target instanceof GroupTarget){
				if(((GroupTarget) target).isPartOf(player)){
					if(permissionSet.isSet(type, target))
						return getPermission(type, target);
				}
			}
		}
		
		// If not, check if the protection has a permission set for outsiders
		PermissionTarget o = new PermissionTarget(TargetType.OUTSIDERS);
		if(permissionSet.isSet(type, o)){
			return getPermission(type, o);
		}
		
		// If not, just return false.
		if(parent != null){
			return parent.hasPermission(player, type);
		} else {
			return false;
		}
		*/
	}
	
	/**
	 * Checks if this Protection has Permissions for the given target.
	 * @param target
	 * @return true if Permissions were found, false otherwise.
	 */
	public boolean hasTarget(PermissionTarget target){
		if(permissionSet.hasTarget(target)){
			return true;
		}
		//if(parent != null) return parent.hasTarget(target);
		return false;
	}
	/**
	 * Gets the Permissions for the given target.
	 * @param target
	 * @return Permission
	 */
	public Permissions getPermissions(PermissionTarget target){
		return permissionSet.get(target);
		/*} else {
			if(parent != null) return parent.getPermissions(target);*/
	}
	
	/**
	 * Gets the value of the permission of a certain type for the given target.
	 * @param target
	 * @param type
	 * @return
	 */
	public boolean getPermission(PermissionTarget target, PermissionType type){
		return permissionSet.get(target, type);
		/*} else if(parent != null) {
			return parent.getPermission(type, target);*/
		
		//if(target.getType() != TargetType.SERVER) {return false;} else {return true;}
	}
	
	/**
	 * Checks if the given location is inside of the protection.
	 * @param location
	 * @return
	 */
	public boolean isInside(Location location){
		for(Shape s : getShapes()){
			if(s.isInside(location)) return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given shape intersects with the protection.
	 * @param s
	 * @return
	 */
	public boolean intersect(Shape s){
		for(Shape s1 : getShapes()){
			if(s1.intersect(s)) return true;
		}
		return false;
	}

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
		refreshParent();
	}
	
	public void refreshParent(){
		if(parent != null){
			if(parent instanceof SettlementProtection){
				((SettlementProtection) parent).hullNeedsUpdate();
			}
		}
	}

	public Location getCenter() {
		World w = null;
		Double minX = null;
		Double minY = null;
		Double minZ = null;
		Double maxX = null;
		Double maxY = null;
		Double maxZ = null;
		for(Shape s : getShapes()){
			if(w == null){
				w = s.getWorld();
				minX = s.getMinX();
				minY = s.getMinY();
				minZ = s.getMinZ();
				maxX = s.getMaxX();
				maxY = s.getMaxY();
				maxZ = s.getMaxZ();
			} else {
				minX = Math.min(minX, s.getMinX());
				minY = Math.min(minY, s.getMinY());
				minZ = Math.min(minZ, s.getMinZ());
				maxX = Math.max(maxX, s.getMaxX());
				maxY = Math.max(maxY, s.getMaxY());
				maxZ = Math.max(maxZ, s.getMaxZ());
			}
		}
		if(w == null) throw new NullPointerException("World can't be null.");
		return new Location(w, (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}
	
	public Group getGroup(){
		for(Group g : Group.getList())
			if(g.getProtection().equals(this)){
				return g;
			}
		return null;
	}
}
