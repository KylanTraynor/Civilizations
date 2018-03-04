package com.kylantraynor.civilizations.managers;

import java.util.*;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Location;

import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Permissions;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

public class ProtectionManager {

    /**
     * Checks if the given {@linkplain EconomicEntity} has the given {@linkplain PermissionType} at the given {@linkplain Location}.
     * @param location The {@link Location} where to test permissions for.
     * @param type The {@link PermissionType} of the permission to test for.
     * @param entity The {@link EconomicEntity} to test permissions for, or Null to check for Server permissions.
     * @return true if the permission is granted, false otherwise.
     */
	public static boolean hasPermissionAt(PermissionType type, Location location, @Nullable EconomicEntity entity, boolean recursive){
	    if(location == null) throw new NullPointerException("Location can't be Null!");
	    if(type == null) throw new NullPointerException("PermissionType can't be Null!");
        Group result;
        result = SettlementManager.getPlotAt(location);
        if(result == null) result = SettlementManager.getSettlementAt(location);
        return hasPermission(type, result, entity, recursive);
    }

    /**
     * Checks if the given {@linkplain EconomicEntity} has the given {@linkplain PermissionType} in the given {@linkplain Group}.
     * @param type The {@link PermissionType} of the permission.
     * @param group The {@link Group} to check permissions in.
     * @param entity The {@link EconomicEntity} to check permissions for.
     * @param recursive Whether the permission check should be deep (true) or shallow (false)
     * @return true if the permission was granted, false otherwise.
     */
    public static boolean hasPermission(PermissionType type, @Nullable Group group, @Nullable EconomicEntity entity, boolean recursive){
        return hasPermission(type, group, entity == null ? null : entity.getIdentifier(), recursive);
    }

    /**
     * Checks if the given {@linkplain OfflinePlayer} has the given {@linkplain PermissionType} in the given {@linkplain Group}.
     * @param type The {@link PermissionType} of the permission.
     * @param group The {@link Group} to check permissions in.
     * @param player The {@link OfflinePlayer} to check permissions for.
     * @param recursive Whether the permission check should be deep (true) or shallow (false)
     * @return true if the permission was granted, false otherwise.
     */
    public static boolean hasPermission(PermissionType type, @Nullable Group group, OfflinePlayer player, boolean recursive){
        return hasPermission(type, group, AccountManager.getCurrentIdentifier(player), recursive);
    }

    /**
     * Checks if the given {@linkplain UUID} has the given {@linkplain PermissionType} in the given {@linkplain Group}.
     * @param type The {@link PermissionType} of the permission.
     * @param group The {@link Group} to check permissions in.
     * @param target The {@link UUID} to check permissions for.
     * @param recursive Whether the permission check should be deep (true) or shallow (false)
     * @return true if the permission was granted, false otherwise.
     */
    public static boolean hasPermission(PermissionType type, @Nullable Group group, @Nullable UUID target, boolean recursive){
	    if(type == null) throw new NullPointerException("PermissionType can't be Null!");
	    if(group == null)  return hasDefaultPermissionFor(type, null, target);
	    @Nullable Boolean result = null;

	    Group current = group;
	    while(result == null && current != null){
	        if(target == null){
	            // Check Server Permissions
                result = current.getSettings().getServerPermission(type.toString());
            } else {
	            if(isOp(target)) return true;
                Permissions[] perms = current.getSettings().getPermissions();
                // Check low levels first (higher priority)
                Arrays.sort(perms);
                for(Permissions p : perms){
                    Boolean perm = p.getPermission(type.toString());
                    // Check if the permission was set
                    if(perm != null){
                        EconomicEntity ee = EconomicEntity.getOrNull(p.getTarget());
                        // Check if the target exists
                        if(ee != null){
                            if(ee instanceof Group &&
                                    (ee.getIdentifier().equals(target) ||
                                    ((Group) ee).isMember(target, true))){
                                // If the given entity is a deep member of the group
                                return perm;
                            } else if (ee.getIdentifier().equals(target)) {
                                // if the given entity's id is the same as the target's id
                                return perm;
                            }
                        }
                    }
                }

                // Check self permission
                if(target.equals(current.getIdentifier()) || current.isMember(target, true)){
                    result = current.getSettings().getSelfPermission(type.toString());
                    if(result != null) return result;
                }

                // Check outsider permission
                result = current.getSettings().getOutsidersPermission(type.toString());
                if(result != null) return result;
            }
            // Move to parent
            if(current.getParentId() != null && recursive){
	            current = current.getParent();
            } else {
	            current = null;
            }
        }

        // If nothing was set, return default. Otherwise return the result
	    if(result == null){
	        return hasDefaultPermissionFor(type, group, target);
        } else {
	        return result;
        }
    }

    /**
     * Sets the value of the given {@linkplain PermissionType} for the given {@linkplain EconomicEntity} in the given {@linkplain Group}.
     * @param type The {@link PermissionType} of the permission to set.
     * @param group The {@link Group} to set the permission in.
     * @param entity The {@link EconomicEntity} to set the permission for.
     * @param value The {@link Boolean} to set the permission to, or Null to unset it.
     * @return The previous value of the permission, or Null if it was not set.
     */
    public static Boolean setPermission(PermissionType type, Group group, @Nullable EconomicEntity entity, Boolean value){
        return setPermission(type, group, (entity == null ? null : entity.getIdentifier()), value);
    }

    /**
     * Sets the value of the given {@linkplain PermissionType} for the given {@linkplain UUID} in the given {@linkplain Group}.
     * @param type The {@link PermissionType} of the permission to set.
     * @param group The {@link Group} to set the permission in.
     * @param target The {@link UUID} to set the permission for.
     * @param value The {@link Boolean} to set the permission to, or Null to unset it.
     * @return The previous value of the permission, or Null if it was not set.
     */
    public static Boolean setPermission(PermissionType type, Group group, @Nullable UUID target, Boolean value){
        if(type == null) throw new NullPointerException("PermissionType can't be Null!");
        if(group == null) throw new NullPointerException("Group can't be Null!");
        if(target != null){
            return group.getSettings().setPermission(target, type.toString(), value);
        } else {
            // Set server permission
            return group.getSettings().setServerPermission(type.toString(), value);
        }
    }

    /**
     * Checks what the default value for this {@linkplain PermissionType} is.
     * @param type as {@link PermissionType}
     * @param group The {@link Group} to check the permissions in, or Null for wilderness.
     * @param id as {@link UUID} or Null to check for Server permissions.
     * @return true if the permission is granted, false otherwise.
     */
    private static boolean hasDefaultPermissionFor(PermissionType type, @Nullable Group group, @Nullable UUID id){
        if(type == null) throw new NullPointerException("PermissionType can't be Null!");
	    if(id == null){
	        // Server Permissions
            return true;
        } else if(group != null){
	        return false;
        } else {
	        // Wilderness Permissions
	        return true;
        }
    }

    /**
     * Checks if the given {@linkplain UUID} is OP.
     * @param id {@link UUID} to check OP state for.
     * @return true if is OP, false otherwise.
     */
    private static boolean isOp(UUID id){
        EconomicEntity entity = EconomicEntity.get(id);
        return entity.isPlayer() && entity.getOfflinePlayer().isOp();
    }
	
}
