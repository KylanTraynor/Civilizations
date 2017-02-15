package com.kylantraynor.civilizations.protection;

public enum PermissionType {
	BREAK                  ("Permission to break blocks."),
	BUILD_BLUEPRINTS       ("Permission to build predefined blueprints."),
	BLUEPRINT_NOTIFICATIONS("Permission to receive notifications about build projects."),
	CLAIM                  ("Permission to claim areas."),
	DEGRADATION            ("Permission to cause degradations."),
	EXPLOSION              ("Permission to cause explosions."),
	FIRE                   ("Permission to start fires."),
	FIRESPREAD             ("Permission for fire to spread."),
	INVITE                 ("Permission to invite other players."),
	KICK                   ("Permission to kick lower rank players."),
	MANAGE                 ("Permission to manage this group."),
	MANAGE_BANNER          ("Permission to change the banner of this group."),
	MANAGE_HOUSE           ("Permission to ..."),
	MANAGE_PLOTS           ("Permission to manage the plots of this group."),
	MANAGE_RANKS           ("Permission to manage the ranks of this group."),
	MANAGE_STALLS          ("Permission to manage the stalls of this group."),
	MOBSPAWNING            ("Permission for mobs to spawn within this group."),
	PLACE                  ("Permission to place blocks within this group."),
	UNCLAIM                ("Permission to unclaim areas."),
	UPGRADE                ("Permission to upgrade this group.");
	
	
	private String description = "";
	
	PermissionType(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
}