package com.kylantraynor.civilizations.protection;

import org.bukkit.OfflinePlayer;

public class PlayerTarget extends PermissionTarget{
	
	OfflinePlayer player;

	public PlayerTarget(OfflinePlayer p) {
		super(TargetType.PLAYER);
		this.player = p;
	}
}
