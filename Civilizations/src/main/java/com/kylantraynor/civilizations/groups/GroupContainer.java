package com.kylantraynor.civilizations.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

/**
 * Basically, a group of groups.
 * @author Baptiste
 *
 */
public class GroupContainer extends Group {
	
	private List<Group> groups = new ArrayList<Group>();
	
	@Override
	public List<UUID> getMembers(){
		List<UUID> list = new ArrayList<UUID>();
		for(Group g : getGroups()){
			if(g != null){
				for(UUID id : g.getMembers()){
					list.add(id);
				}
			}
		}
		return list;
	}
	
	@Override
	public void setMembers(List<UUID> list){
		return;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
}
