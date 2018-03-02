package com.kylantraynor.civilizations.protection;

import com.sun.istack.internal.NotNull;

import java.util.Map;
import java.util.UUID;

public class Permissions implements Comparable<Permissions>{
	final private Map<String, Boolean> perms;
	final int level;
	final UUID target;
	
	public Permissions(UUID target, int level, Map<String, Boolean> perms){
		this.target = target;
		this.level = level;
		this.perms = perms;
	}

    /**
     * Returns the {@linkplain UUID} of the entity targeted by this permission.
     * @return {@link UUID}
     */
	public UUID getTarget(){
	    return target;
    }

    /**
     * Returns the value of the given permission, or Null if it doesn't exist.
     * @param perm permission to check.
     * @return A {@link Boolean}, or Null.
     */
	public Boolean getPermission(@NotNull String perm){
	    if(perm == null) throw new NullPointerException("Permission can't be Null.");
	    return perms.get(perm);
    }

    @Override
    public int compareTo(Permissions o) {
	    return Integer.compareUnsigned(this.level, o.level);
    }
}