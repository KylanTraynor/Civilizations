
package com.kylantraynor.civilizations.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

/**
 * Basically, a group of groups.
 * @author Baptiste Jacquet
 *
 */
public class GroupContainer<T extends Group> extends Group {
	
	private List<T> groups = new ArrayList<T>();
	
	@Override
	public List<UUID> getMembers(){
		List<UUID> list = new ArrayList<UUID>();
		for(T g : getGroups()){
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

	public List<T> getGroups() {
		return groups;
	}
	
	public void setGroups(List<T> groups) {
		this.groups = groups;
	}	
}