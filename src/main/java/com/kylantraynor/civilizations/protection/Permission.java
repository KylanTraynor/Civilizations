package com.kylantraynor.civilizations.protection;

public class Permission {
	PermissionType type;
	Boolean value = null;
	
	/**
	 * Checks if this permission is set to true or not.
	 * @return false if the permission isn't set.
	 */
	public boolean hasPerm(){
		if(value == null) return false;
		return value;
	}
	
	/**
	 * Checks if this permission is set to true or not.
	 * @param defaultValue
	 * @return default value if the permission isn't set.
	 */
	public boolean hasPerm(boolean defaultValue){
		if(value == null) return defaultValue;
		return value;
	}
	
	/**
	 * Sets the value of this permission.
	 * @param newValue
	 */
	public void setValue(boolean newValue){
		value = newValue;
	}
	
	/**
	 * Checks if this permission is set (has a value).
	 * @return true if this permission has a value, false otherwise.
	 */
	public boolean isSet(){
		return value != null;
	}
}
