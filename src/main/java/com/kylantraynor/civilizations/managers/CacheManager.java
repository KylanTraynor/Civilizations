package com.kylantraynor.civilizations.managers;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.kylantraynor.cache.Cache;
import com.kylantraynor.voronoi.VTriangle;

public class CacheManager {
	
	private final static int CACHE_CAPACITY = 1000;
	private final static long CACHE_TIMER = 1l;
	private final static long CACHE_LIFESPAN = 30l * 60l;
	
	private static Cache<UUID, VTriangle> playerLocations;
	
	public static void init(){
		playerLocations = new Cache<UUID, VTriangle>(CACHE_LIFESPAN, CACHE_TIMER, CACHE_CAPACITY);
	}
	
	/**
	 * Gets the location of the given player that was cached from the influence map.
	 * @param p
	 * @return
	 */
	public static VTriangle getPlayerTriangulation(Player p){
		return playerLocations.get(p.getUniqueId());
	}
	
	/**
	 * Sets the location of the given player on the influence map to the cache.
	 * @param p
	 * @param t
	 * @return The old value, or Null.
	 */
	public static VTriangle setPlayerTriangulation(Player p, VTriangle t){
		return playerLocations.put(p.getUniqueId(), t);
	}
}