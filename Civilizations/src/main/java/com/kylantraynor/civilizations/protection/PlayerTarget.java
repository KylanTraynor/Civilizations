package com.kylantraynor.civilizations.protection;

import org.bukkit.OfflinePlayer;

public class PlayerTarget extends PermissionTarget{
	
	OfflinePlayer player;

	public PlayerTarget(OfflinePlayer p) {
		super(PermissionTarget.Type.PLAYER);
		this.player = p;
	}
}
