package com.kylantraynor.civilizations.builder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Builder {
	private HasBuilder owner;
	private List<BuildProject> projects = new ArrayList<BuildProject>();
	private BuildProject currentProject;

	public Builder(HasBuilder group) {
		this.owner = group;
	}

	public ItemStack getSupplies(ItemStack is){
		if(owner == null) return null;
		return owner.getSuppliesAndRemove(is);
	}
	
	public void update(){
		if(projects.isEmpty()) return;
		if(currentProject == null) currentProject = projects.get((int) Math.floor((Math.random() * projects.size())));
		
		if(currentProject.isDone()){
			projects.remove(currentProject);
			currentProject = null;
			return;
		}
		
		ItemStack plan = currentProject.getNext();
		if(plan == null){
			currentProject = null;
			return;
		} else if(plan.getType() == Material.AIR){
			currentProject.buildNext();
			return;
		}
		ItemStack supply = getSupplies(plan);
		if(supply == null){
			//TODO Warn about lack of supplies.
			currentProject = null;
		} else {
			currentProject.buildNext();
		}
	}
	
	public HasBuilder getOwner() {
		return owner;
	}

	public void setOwner(HasBuilder owner) {
		this.owner = owner;
	}
	
	public List<BuildProject> getProjects(){
		return projects;
	}
	
	public BuildProject getCurrentProject(){
		return currentProject;
	}
}
