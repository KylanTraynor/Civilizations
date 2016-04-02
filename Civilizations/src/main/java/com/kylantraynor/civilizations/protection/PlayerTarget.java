package com.kylantraynor.civilizations.protection;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerTarget extends PermissionTarget{
	
	private UUID uuid;

	public PlayerTarget(OfflinePlayer p) {
		super(TargetType.PLAYER);
		this.uuid = p.getUniqueId();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void setUniqueId(UUID uuid) {
		this.uuid = uuid;
	}
	
	public OfflinePlayer getPlayer(){
		return Bukkit.getOfflinePlayer(this.uuid);
	}
	@Override
	public boolean equals(Object pt){
		if(!(pt instanceof PlayerTarget)) return false;
		if(((PlayerTarget) pt).getUniqueId().equals(this.getUniqueId())){
			return true;
		}
		return false;
	}
}
