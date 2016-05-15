package com.kylantraynor.civilizations.protection;

import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.groups.Group;

public class GroupTarget extends PermissionTarget{

	private Group group;
	
	public GroupTarget(Group g) {
		super(TargetType.GROUP);
		this.setGroup(g);
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
	public boolean isPartOf(OfflinePlayer player){
		return getGroup().isMember(player);
	}
}
