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
	
	@Override
	public boolean equals(Object pt){
		if(!(pt instanceof GroupTarget)) return false;
		if(((GroupTarget) pt).getGroup().equals(this.getGroup())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int base = 13;
		return base * getGroup().getUniqueId().hashCode();
	}
}
