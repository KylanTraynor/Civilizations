
package com.kylantraynor.civilizations.groups;

import java.util.*;

import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.OfflinePlayer;

/**
 * Basically, a group of groups.
 * @author Baptiste Jacquet
 *
 */
public class GroupContainer<T extends Group> extends Group {
	
	private Set<T> groups = new TreeSet<T>();
	
	@Override
	public Set<Identifier> getMembers(){
		Set<Identifier> list = new TreeSet<>();
		for(T g : getGroups()){
			if(g != null){
				for(Identifier id : g.getMembers()){
					list.add(id);
				}
			}
		}
		return list;
	}
	
	@Override
	public void setMembers(Set<Identifier> list){
		return;
	}

	public Set<T> getGroups() {
		return groups;
	}
	
	public void setGroups(Set<T> groups) {
		this.groups = groups;
	}	
}